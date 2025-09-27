package lk.jiat.sltb;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import lk.jiat.sltb.model.DatabaseHelper;

public class Bookticket2 extends AppCompatActivity {

    private RecyclerView hospitalrecy;
    private OparatorAdapter oparatorAdapter;
    private ArrayList<Operator> operatorList;
    private Button apponext2;

    private String selectedDate;
    private String BusName;

    private AlertDialog loadingDialog;

    private String emailPhone;
    private DatabaseHelper databaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_bookticket2);

        // Initialize views
        hospitalrecy = findViewById(R.id.hospitalrecy);
        apponext2 = findViewById(R.id.apponext2);

        // Retrieve data from the previous activity
        Intent intent = getIntent();
        selectedDate = intent.getStringExtra("SELECTED_DATE");
        BusName = intent.getStringExtra("bus_name");

        Log.d("bp2", "Received data: " +
                "\nBus: " + BusName +
                "\nDate: " + selectedDate +
                "\nEmail/Phone: " + emailPhone);
        emailPhone = getIntent().getStringExtra("EMAIL_PHONE");
        databaseHelper = new DatabaseHelper(this);
        // Initialize hospital list and adapter
        operatorList = new ArrayList<>();
        oparatorAdapter = new OparatorAdapter(operatorList);
        hospitalrecy.setLayoutManager(new LinearLayoutManager(this));
        hospitalrecy.setAdapter(oparatorAdapter);

        // Show loading dialog
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(this);
        builder.setTitle("Loading...");
        builder.setMessage("Fetching Bus Stand data...");
        builder.setCancelable(false);
        loadingDialog = builder.create();
        loadingDialog.show();

        // Fetch relevant hospitals for the selected doctor
        fetchStandForBus(BusName);

        // Handle next button click
        apponext2.setOnClickListener(v -> {
            Operator selectedOperator = oparatorAdapter.getSelectedHospital();

            if (selectedOperator != null) {
                Intent intentNext = new Intent(Bookticket2.this, TimeSlotActivity.class);
                intentNext.putExtra("SELECTED_DATE", selectedDate);
                intentNext.putExtra("SELECTED_ARRIVAL_STATION", selectedOperator.getName()); // This is the arrival station (selected by user)
                intentNext.putExtra("bus_name", BusName);
                intentNext.putExtra("EMAIL_PHONE", emailPhone);
                startActivity(intentNext);
            } else {
                new MaterialAlertDialogBuilder(Bookticket2.this)
                        .setTitle("Error")
                        .setMessage("Please select a Bus.")
                        .setPositiveButton("OK", null)
                        .show();
            }
        });

        // Handle back button click
        ImageView imageViewa2 = findViewById(R.id.imageViewa2);
        imageViewa2.setOnClickListener(view -> {
            Intent intentBack = new Intent(Bookticket2.this, bookticket.class);
            startActivity(intentBack);
        });
    }

    /**
     * Fetches bus stations associated with the selected bus.
     *
     * @param busName The name of the selected bus.
     */
    private void fetchStandForBus(String busName) {
        // Log the start of the operation
        Log.d("Bookappointment2", "Fetching stations for bus: " + busName);

        // Fetch the bus's document to get associated station IDs
        databaseHelper.fetchBusByName(busName, new DatabaseHelper.OnBusFetchedListener() {
            @Override
            public void onBusFetched(Map<String, Object> bus) {
                Log.d("Bookappointment2", "Successfully fetched bus data");
                
                @SuppressWarnings("unchecked")
                List<String> stationIds = (List<String>) bus.get("stationIds");

                if (stationIds != null && !stationIds.isEmpty()) {
                    Log.d("Bookappointment2", "Fetched station IDs: " + stationIds);
                    // Fetch station details for the associated station IDs
                    fetchStationsByIds(stationIds);
                } else {
                    dismissLoadingDialog();
                    new MaterialAlertDialogBuilder(Bookticket2.this)
                            .setTitle("Error")
                            .setMessage("No stations found for this bus.")
                            .setPositiveButton("OK", null)
                            .show();
                }
            }

            @Override
            public void onBusFetchFailed(String error) {
                dismissLoadingDialog();
                new MaterialAlertDialogBuilder(Bookticket2.this)
                        .setTitle("Error")
                        .setMessage("No matching bus found: " + error)
                        .setPositiveButton("OK", null)
                        .show();
            }
        });
    }

    /**
     * Fetches station details for the given station IDs.
     *
     * @param stationIds List of station IDs to fetch.
     */
    private void fetchStationsByIds(List<String> stationIds) {
        operatorList.clear(); // Clear the list before adding new data

        int totalStations = stationIds.size();
        AtomicInteger fetchedCount = new AtomicInteger();

        for (String stationId : stationIds) {
            Log.d("Bookappointment2", "Fetching station with ID: " + stationId);

            databaseHelper.fetchStationById(stationId, new DatabaseHelper.OnStationFetchedListener() {
                @Override
                public void onStationFetched(Map<String, Object> stationData) {
                    Log.d("Bookappointment2", "Successfully fetched station: " + stationId);

                    // Create Operator object from station data
                    Operator operator = new Operator();
                    operator.setName((String) stationData.get("station_name"));
                    operator.setAddress((String) stationData.get("station_address"));
                    operator.setCity((String) stationData.get("city"));
                    operator.setProvince((String) stationData.get("province"));
                    operator.setContactNumber((String) stationData.get("contact_number"));
                    
                    @SuppressWarnings("unchecked")
                    List<String> facilities = (List<String>) stationData.get("facilities");
                    if (facilities != null) {
                        operator.setFacilities(facilities);
                    }

                    Log.d("Bookappointment2", "Parsed station: " + operator.getName());
                    operatorList.add(operator);

                    fetchedCount.getAndIncrement();
                    Log.d("Bookappointment2", "Fetched " + fetchedCount + "/" + totalStations + " stations");

                    if (fetchedCount.get() == totalStations) {
                        Log.d("Bookappointment2", "All stations fetched");
                        oparatorAdapter.notifyDataSetChanged();
                        dismissLoadingDialog();
                    }
                }

                @Override
                public void onStationFetchFailed(String error) {
                    Log.e("Bookappointment2", "Failed to fetch station with ID: " + stationId + " - " + error);
                    
                    fetchedCount.getAndIncrement();
                    Log.d("Bookappointment2", "Fetched " + fetchedCount + "/" + totalStations + " stations");

                    if (fetchedCount.get() == totalStations) {
                        Log.d("Bookappointment2", "All stations fetched");
                        oparatorAdapter.notifyDataSetChanged();
                        dismissLoadingDialog();
                    }
                }
            });
        }
    }

    /**
     * Dismisses the loading dialog if it is showing.
     */
    private void dismissLoadingDialog() {
        if (loadingDialog != null && loadingDialog.isShowing()) {
            loadingDialog.dismiss();
        }
    }
}
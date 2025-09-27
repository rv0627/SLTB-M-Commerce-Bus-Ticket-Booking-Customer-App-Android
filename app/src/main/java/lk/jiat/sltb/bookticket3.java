package lk.jiat.sltb;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import java.util.List;
import java.util.Map;
import lk.jiat.sltb.model.DatabaseHelper;

public class bookticket3 extends AppCompatActivity {

    private EditText firstNameEditText, lastNameEditText, emailEditText, mobileNumberEditText;
    private Button nextButton;
    private FirebaseFirestore db;
    private CollectionReference appointmentsRef;
    private String registeredMobileNumber;
    private DatabaseHelper databaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_bookticket3);

        // Initialize Firebase Firestore
        db = FirebaseFirestore.getInstance();
        appointmentsRef = db.collection("bookings");
        databaseHelper = new DatabaseHelper(this);

        // Initialize views
        firstNameEditText = findViewById(R.id.editTextText6);
        lastNameEditText = findViewById(R.id.editTextLastName);
        emailEditText = findViewById(R.id.editTextEmail);
        mobileNumberEditText = findViewById(R.id.editTextNumber);
        nextButton = findViewById(R.id.nextappo3);

        // Retrieve the registered mobile number from the intent or database
        registeredMobileNumber = getIntent().getStringExtra("EMAIL_PHONE");

        if (registeredMobileNumber != null && !registeredMobileNumber.isEmpty()) {
            // Set the mobile number in the EditText
            mobileNumberEditText.setText(registeredMobileNumber);

            // Disable editing for the mobile number field
            mobileNumberEditText.setEnabled(false); // Makes the field non-editable
        } else {
            Toast.makeText(this, "Failed to retrieve registered mobile number", Toast.LENGTH_SHORT).show();
        }

        // Handle "Next" button click
        nextButton.setOnClickListener(v -> {
            String firstName = firstNameEditText.getText().toString().trim();
            String lastName = lastNameEditText.getText().toString().trim();
            String email = emailEditText.getText().toString().trim();
            String mobileNumber = mobileNumberEditText.getText().toString().trim();

            // Validate input fields
            if (TextUtils.isEmpty(firstName)) {
                Toast.makeText(this, "Please enter your first name", Toast.LENGTH_SHORT).show();
                return;
            }
            if (TextUtils.isEmpty(lastName)) {
                Toast.makeText(this, "Please enter your last name", Toast.LENGTH_SHORT).show();
                return;
            }
            if (TextUtils.isEmpty(email)) {
                Toast.makeText(this, "Please enter your email", Toast.LENGTH_SHORT).show();
                return;
            }
            if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                Toast.makeText(this, "Please enter a valid email", Toast.LENGTH_SHORT).show();
                return;
            }
            if (TextUtils.isEmpty(mobileNumber)) {
                Toast.makeText(this, "Please enter your mobile number", Toast.LENGTH_SHORT).show();
                return;
            }
            if (mobileNumber.length() < 10) {
                Toast.makeText(this, "Mobile number must be at least 10 digits", Toast.LENGTH_SHORT).show();
                return;
            }

            // Generate booking number and save the booking
            String selectedDate = getIntent().getStringExtra("SELECTED_DATE");
            String selectedArrivalStation = getIntent().getStringExtra("SELECTED_ARRIVAL_STATION");
            String busName = getIntent().getStringExtra("bus_name");
            String selectedTime = getIntent().getStringExtra("SELECTED_TIME");
            
            // Debug logging
            android.util.Log.d("BookTicket3", "Received data: " +
                    "\nSelected Date: " + selectedDate +
                    "\nArrival Station: " + selectedArrivalStation +
                    "\nBus Name: " + busName +
                    "\nSelected Time: " + selectedTime);
            
            // Fetch departure station from bus document
            fetchDepartureStationAndSave(
                    firstName,
                    lastName,
                    email,
                    mobileNumber,
                    selectedDate,
                    selectedArrivalStation,
                    busName,
                    selectedTime
            );
        });

        // Handle back button click
        ImageView imageViewa3 = findViewById(R.id.imageViewa3);
        imageViewa3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(bookticket3.this, Bookticket2.class);
                startActivity(intent);
            }
        });
    }

    /**
     * Fetch departure station from bus document and then save the booking
     */
    private void fetchDepartureStationAndSave(String firstName, String lastName, String email, String mobileNumber,
                                              String selectedDate, String selectedArrivalStation, String busName, String selectedTime) {
        
        // Fetch bus document to get departure station
        databaseHelper.fetchBusByName(busName, new DatabaseHelper.OnBusFetchedListener() {
            @Override
            public void onBusFetched(Map<String, Object> busData) {
                // Get departure station from bus document
                String departureStation = (String) busData.get("departure_station");
                if (departureStation == null) {
                    // If no specific departure station field, use the first station in the route
                    @SuppressWarnings("unchecked")
                    List<String> stationIds = (List<String>) busData.get("stationIds");
                    if (stationIds != null && !stationIds.isEmpty()) {
                        // Fetch the first station to get its name
                        databaseHelper.fetchStationById(stationIds.get(0), new DatabaseHelper.OnStationFetchedListener() {
                            @Override
                            public void onStationFetched(Map<String, Object> stationData) {
                                String firstStationName = (String) stationData.get("station_name");
                                android.util.Log.d("BookTicket3", "Using first station as departure: " + firstStationName);
                                generateBookingNumberAndSave(firstName, lastName, email, mobileNumber, selectedDate, firstStationName, selectedArrivalStation, busName, selectedTime);
                            }
                            
                            @Override
                            public void onStationFetchFailed(String error) {
                                android.util.Log.e("BookTicket3", "Failed to fetch first station: " + error);
                                // Use a default departure station
                                generateBookingNumberAndSave(firstName, lastName, email, mobileNumber, selectedDate, "Main Terminal", selectedArrivalStation, busName, selectedTime);
                            }
                        });
                    } else {
                        // No stations found, use default
                        generateBookingNumberAndSave(firstName, lastName, email, mobileNumber, selectedDate, "Main Terminal", selectedArrivalStation, busName, selectedTime);
                    }
                } else {
                    android.util.Log.d("BookTicket3", "Using departure station from bus document: " + departureStation);
                    generateBookingNumberAndSave(firstName, lastName, email, mobileNumber, selectedDate, departureStation, selectedArrivalStation, busName, selectedTime);
                }
            }
            
            @Override
            public void onBusFetchFailed(String error) {
                android.util.Log.e("BookTicket3", "Failed to fetch bus data: " + error);
                Toast.makeText(bookticket3.this, "Failed to fetch bus information", Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * Generate a booking number and save the booking details to Firestore.
     */
    private void generateBookingNumberAndSave(String firstName, String lastName, String email, String mobileNumber,
                                              String selectedDate, String selectedDepartureStation, String selectedArrivalStation, String busName, String selectedTime) {

        appointmentsRef.whereEqualTo("travelDate", selectedDate)
                .whereEqualTo("busName", busName)
                .whereEqualTo("departureStation", selectedDepartureStation)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        int bookingNumber = 1;
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            // Retrieve bookingNumber as Integer
                            int currentNumber = document.getLong("bookingNumber").intValue();
                            if (currentNumber >= bookingNumber) {
                                bookingNumber = currentNumber + 1;
                            }
                        }

                        int finalBookingNumber = bookingNumber;

                        databaseHelper.fetchFareByBusName(busName, new DatabaseHelper.OnFareFetchedListener() {
                            @Override
                            public void onFareFetched(Map<String, Object> fareData) {
                                int baseFare = ((Long) fareData.get("base_fare")).intValue();
                                int stationFee = ((Long) fareData.get("station_fee")).intValue();
                                int serviceCharge = ((Long) fareData.get("service_charge")).intValue();

                                        Ticket ticket = new Ticket(
                                                finalBookingNumber,
                                                firstName,
                                                lastName,
                                                email,
                                                mobileNumber,
                                                selectedDate,
                                                selectedTime,
                                                busName,
                                                selectedDepartureStation,
                                                selectedArrivalStation,
                                                baseFare,
                                                stationFee,
                                                serviceCharge,
                                                "Pending", // Default status
                                                "A1", // Default seat number - can be made dynamic later
                                                selectedDepartureStation + " to " + selectedArrivalStation // Route
                                        );

                                int finalBaseFare = baseFare;
                                int finalStationFee = stationFee;
                                int finalServiceCharge = serviceCharge;
                                appointmentsRef.document("booking_" + finalBookingNumber + selectedDate + selectedDepartureStation).set(ticket)
                                        .addOnSuccessListener(aVoid -> {
                                            Toast.makeText(bookticket3.this, "Ticket Booked Successfully", Toast.LENGTH_SHORT).show();
                                            Intent intent = new Intent(bookticket3.this, bookticket4.class);
                                            intent.putExtra("BOOKING_NUMBER", finalBookingNumber);
                                            intent.putExtra("SELECTED_DATE", selectedDate);
                                            intent.putExtra("SELECTED_DEPARTURE_STATION", selectedDepartureStation);
                                            intent.putExtra("SELECTED_ARRIVAL_STATION", selectedArrivalStation);
                                            intent.putExtra("BASE_FARE", finalBaseFare);
                                            intent.putExtra("STATION_FEE", finalStationFee);
                                            intent.putExtra("SERVICE_CHARGE", finalServiceCharge);
                                            intent.putExtra("SELECTED_TIME", selectedTime);
                                            intent.putExtra("BUS_NAME", busName);
                                            intent.putExtra("EMAIL_PHONE", registeredMobileNumber);
                                            startActivity(intent);
                                        })
                                        .addOnFailureListener(e -> {
                                            Toast.makeText(bookticket3.this, "Failed to save booking", Toast.LENGTH_SHORT).show();
                                        });
                            }

                            @Override
                            public void onFareFetchFailed(String error) {
                                Toast.makeText(bookticket3.this, "Failed to fetch bus fare: " + error, Toast.LENGTH_SHORT).show();
                            }
                        });
                    } else {
                        Toast.makeText(bookticket3.this, "Failed to fetch booking data", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
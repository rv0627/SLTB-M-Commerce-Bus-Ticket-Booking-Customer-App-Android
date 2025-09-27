package lk.jiat.sltb;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import lk.jiat.sltb.model.DatabaseHelper;

public class TimeSlotActivity extends AppCompatActivity {

    private TextView textViewDoctorName;
    private TextView textViewHospitalName;
    private TextView textViewSelectedDate;
    private TextView textViewSelectedhospitaladress;
    private RecyclerView recyclerViewTimeSlots;

    private String selectedDoctor;
    private String selectedHospital;
    private String selectedHospitaladress;
    private String selectedDate;

    private TimeSlotAdapter adapter;
    private List<TimeSlot> timeSlots;
    private String emailPhone;
    private DatabaseHelper databaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_time_slot);

        // Initialize views
        textViewDoctorName = findViewById(R.id.textViewDoctorName);
        textViewHospitalName = findViewById(R.id.textViewHospitalName);
        textViewSelectedDate = findViewById(R.id.textViewSelectedDate);
        textViewSelectedhospitaladress= findViewById(R.id.textViewSelectedhospitaladress);
        recyclerViewTimeSlots = findViewById(R.id.recyclerViewTimeSlots);

        // Retrieve data from the previous activity
        selectedDoctor = getIntent().getStringExtra("bus_name");
        selectedHospital = getIntent().getStringExtra("SELECTED_HOSPITAL_NAME");
        selectedHospitaladress = getIntent().getStringExtra("SELECTED_HOSPITAL_ADDRESS");
        selectedDate = getIntent().getStringExtra("SELECTED_DATE");
        emailPhone = getIntent().getStringExtra("EMAIL_PHONE");
        databaseHelper = new DatabaseHelper(this);

        Log.d("timeslot", "Received data: " +
                "\nDoctor: " + selectedDoctor +
                "\nHospital: " + selectedHospital +
                "\nAddress: " + selectedHospitaladress +
                "\nDate: " + selectedDate +
                "\nEmail/Phone: " + emailPhone);

        // Display selected details
        textViewDoctorName.setText("Operator: " + selectedDoctor);
        textViewHospitalName.setText("Route: " + selectedHospital);
        textViewSelectedDate.setText("Date: " + selectedDate);
        textViewSelectedhospitaladress.setText("Depature Station:"+selectedHospitaladress);

        // Initialize RecyclerView
        timeSlots = new ArrayList<>();
        adapter = new TimeSlotAdapter(timeSlots, this::onTimeSlotClicked);
        recyclerViewTimeSlots.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewTimeSlots.setAdapter(adapter);

        // Check if the bus exists in Firestore
        checkBusAvailability();
    }

    private void checkBusAvailability() {
        // Query the "bus_schedules" collection to find a document where the "busName" matches the selectedBus
        databaseHelper.fetchBusSchedule(selectedDoctor, new DatabaseHelper.OnScheduleFetchedListener() {
            @Override
            public void onScheduleFetched(Map<String, Object> schedule) {
                Log.d("Firestore", "Bus schedule found for: " + selectedDoctor);
                // Proceed to fetch time slots for the bus
                fetchTimeSlotsFromFirestore(selectedDoctor);
            }

            @Override
            public void onScheduleFetchFailed(String error) {
                // Handle the case where the bus does not exist
                Log.e("Firestore", "Bus schedule not found: " + error);
                Toast.makeText(TimeSlotActivity.this, "Bus schedule not found", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void fetchTimeSlotsFromFirestore(String busName) {
        // Determine if the selected date is a weekday or weekend
        String dayType = getDayType(selectedDate);
        if (dayType == null) {
            Log.e("TimeSlotActivity", "Invalid date format");
            return;
        }

        // Fetch time slots for the selected bus and day type
        databaseHelper.fetchTimeSlots(busName, dayType, new DatabaseHelper.OnTimeSlotsFetchedListener() {
            @Override
            public void onTimeSlotsFetched(List<Map<String, Object>> timeSlotData) {
                timeSlots.clear(); // Clear existing time slots
                for (Map<String, Object> timeSlot : timeSlotData) {
                    String time = (String) timeSlot.get("time");
                    Long availableSeatsLong = (Long) timeSlot.get("available_seats");
                    Integer availableSeats = availableSeatsLong != null ? availableSeatsLong.intValue() : 0;
                    boolean isAvailable = availableSeats != null && availableSeats > 0;

                    // Add the time slot to the list
                    timeSlots.add(new TimeSlot(time, time, isAvailable));
                }
                adapter.notifyDataSetChanged(); // Notify adapter of data changes
            }

            @Override
            public void onTimeSlotsFetchFailed(String error) {
                // Handle Firestore fetch error
                Log.e("Firestore", "Failed to fetch time slots: " + error);
                Toast.makeText(TimeSlotActivity.this, "Failed to fetch time slots: " + error, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void onTimeSlotClicked(TimeSlot timeSlot) {
        // Create an Intent to navigate to bookappointment3
        Intent intent = new Intent(this, bookticket3.class);

        // Pass all relevant details to the next activity
        intent.putExtra("bus_name", selectedDoctor);
        intent.putExtra("SELECTED_ARRIVAL_STATION", getIntent().getStringExtra("SELECTED_ARRIVAL_STATION")); // Pass the arrival station from previous activity
        intent.putExtra("SELECTED_DATE", selectedDate);
        intent.putExtra("SELECTED_TIME", timeSlot.getStartTime() + " - " + timeSlot.getEndTime());
        intent.putExtra("EMAIL_PHONE", emailPhone);
        startActivity(intent); // Start the bookappointment3 activity
    }

    public static String getDayType(String selectedDate) {
        try {
            // Parse the selected date into a Calendar instance
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).parse(selectedDate));

            // Get the day of the week
            int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);

            // Check if it's a weekend (Saturday or Sunday)
            if (dayOfWeek == Calendar.SATURDAY || dayOfWeek == Calendar.SUNDAY) {
                return "weekends";
            } else {
                return "weekdays";
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
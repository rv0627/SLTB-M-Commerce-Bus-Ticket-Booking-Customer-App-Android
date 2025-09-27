package lk.jiat.sltb;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

public class ticketview extends AppCompatActivity {

    private String emailPhone; // To store the user's email/phone

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ticketview);

        // Retrieve email/phone from Intent
        emailPhone = getIntent().getStringExtra("EMAIL_PHONE");
        if (emailPhone == null || emailPhone.isEmpty()) {
            Toast.makeText(this, "Email/Phone not found", Toast.LENGTH_SHORT).show();
            finish(); // Close the activity if no email/phone is found
            return;
        }

        // Initialize buttons
        Button todayAppointmentsTab = findViewById(R.id.todayAppointmentsTab);
        Button appointmentHistoryTab = findViewById(R.id.appointmentHistoryTab);

        // Back button (imageViewa)
        ImageView imageViewa = findViewById(R.id.imageViewa);
        imageViewa.setOnClickListener(view -> {
            Intent intent = new Intent(ticketview.this, dashboard.class);
            intent.putExtra("EMAIL_PHONE", emailPhone); // Pass email/phone back to dashboard
            startActivity(intent);
            finish(); // Close this activity
        });

        // Default fragment: Load "Today's Appointments" when the activity starts
        loadFragment(new today_tickets(), emailPhone);

        // Handle "Today's Appointments" tab click
        todayAppointmentsTab.setOnClickListener(v -> {
            loadFragment(new today_tickets(), emailPhone);
        });

        // Handle "Appointment History" tab click
        appointmentHistoryTab.setOnClickListener(v -> {
            loadFragment(new ticket_history(), emailPhone);
        });
    }

    /**
     * Helper method to load a fragment into the content container.
     *
     * @param fragment   The fragment to load.
     * @param emailPhone The email/phone to pass to the fragment.
     */
    private void loadFragment(Fragment fragment, String emailPhone) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        // Pass email/phone to the fragment using a Bundle
        Bundle bundle = new Bundle();
        bundle.putString("EMAIL_PHONE", emailPhone);
        fragment.setArguments(bundle);

        // Replace the current fragment in the container
        fragmentTransaction.replace(R.id.contentContainer, fragment);

        // Add the transaction to the back stack (optional)
        fragmentTransaction.addToBackStack(null);

        // Commit the transaction
        fragmentTransaction.commit();
    }
}
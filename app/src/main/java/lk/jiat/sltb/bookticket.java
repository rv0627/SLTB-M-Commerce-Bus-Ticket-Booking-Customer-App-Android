package lk.jiat.sltb;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.ImageView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

public class bookticket extends AppCompatActivity {
    private CalendarView appobookcalender;
    private Button nextappo1;
    private String selectedDate;
    private String bus_name;

    private String emailPhone;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_bookticket);

        Intent intent = getIntent();
        bus_name= intent.getStringExtra("bus_name");
        emailPhone = intent.getStringExtra("EMAIL_PHONE");

        Log.d("bp", "Received data: " +
                "\nBus: " + bus_name +
                "\nDate: " + selectedDate +
                "\nEmail/Phone: " + emailPhone);

        appobookcalender = findViewById(R.id.appobookcalender);
        nextappo1 = findViewById(R.id.nextappo1);

        // Set default date
        selectedDate = "";

        // Handle date selection
        appobookcalender.setOnDateChangeListener((view, year, month, dayOfMonth) -> {
            // Format: yyyy-MM-dd
            selectedDate = String.format("%d-%02d-%02d", year, month + 1, dayOfMonth);
        });

        // Handle "Next" button click
        nextappo1.setOnClickListener(v -> {
            if (!selectedDate.isEmpty()) {
                Intent intentNext = new Intent(bookticket.this, Bookticket2.class);



                intentNext.putExtra("SELECTED_DATE", selectedDate);

                intentNext.putExtra("bus_name", bus_name);
                intentNext.putExtra("EMAIL_PHONE", emailPhone);

                startActivity(intentNext);
            }
        });


        ImageView imageViewa1 = findViewById(R.id.imageViewa1);
        imageViewa1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(bookticket.this , opratorprofile.class);

                startActivity(intent);

            }
        });
    }
}
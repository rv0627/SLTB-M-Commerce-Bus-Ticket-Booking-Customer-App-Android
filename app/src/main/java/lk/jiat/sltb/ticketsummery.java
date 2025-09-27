package lk.jiat.sltb;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.Date;

public class ticketsummery extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_ticketsummery);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        Button btnRescheduleAppointment = findViewById(R.id.reschedule);
        btnRescheduleAppointment.setOnClickListener(v -> showReschedulePopup());

        Button btnCancelAppointment = findViewById(R.id.cancel);
        btnCancelAppointment.setOnClickListener(v -> showCancelAppointmentPopup());
    }




        public void showReschedulePopup() {

            LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
            View popupView = inflater.inflate(R.layout.activity_rescheduleticket, null);


            int width = LinearLayout.LayoutParams.MATCH_PARENT;
            int height = LinearLayout.LayoutParams.WRAP_CONTENT;
            boolean focusable = true;
            final PopupWindow popupWindow = new PopupWindow(popupView, width, height, focusable);

            popupWindow.setBackgroundDrawable(ContextCompat.getDrawable(this, R.drawable.popup_background));
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                popupWindow.setElevation(10f);
            }

            popupWindow.showAtLocation(popupView, Gravity.CENTER, 0, 0);


            CalendarView calendarView = popupView.findViewById(R.id.calendarView);
            calendarView.setMinDate(System.currentTimeMillis());
            Button btnReschedule = popupView.findViewById(R.id.btnReschedule);


            btnReschedule.setOnClickListener(v -> {

                long selectedDateMillis = calendarView.getDate();
                Date selectedDate = new Date(selectedDateMillis);


                updateAppointmentDate(selectedDate);

                // Dismiss the popup
                popupWindow.dismiss();
            });
        }

        private void updateAppointmentDate(Date newDate) {

            System.out.println("Appointment rescheduled to: " + newDate);
        }

    public void showCancelAppointmentPopup() {

        LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        View popupView = inflater.inflate(R.layout.activity_cancelticket, null);


        int width = LinearLayout.LayoutParams.MATCH_PARENT;
        int height = LinearLayout.LayoutParams.WRAP_CONTENT;
        boolean focusable = true;
        PopupWindow popupWindow = new PopupWindow(popupView, width, height, focusable);


        popupWindow.setBackgroundDrawable(ContextCompat.getDrawable(this, R.drawable.popup_background));


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            popupWindow.setElevation(10f);
        }


        popupWindow.showAtLocation(popupView, Gravity.CENTER, 0, 0);


        Button btnProceed = popupView.findViewById(R.id.btnProceed);
        Button btnCancel = popupView.findViewById(R.id.btnCancel);


        btnProceed.setOnClickListener(v -> {

            Intent intent = new Intent(this, ticketview.class);
            startActivity(intent);


            popupWindow.dismiss();
        });


        btnCancel.setOnClickListener(v -> {

            popupWindow.dismiss();
        });
    }

    }




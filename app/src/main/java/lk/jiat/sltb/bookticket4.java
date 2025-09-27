package lk.jiat.sltb;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import lk.payhere.androidsdk.PHConfigs;
import lk.payhere.androidsdk.PHConstants;
import lk.payhere.androidsdk.PHMainActivity;
import lk.payhere.androidsdk.PHResponse;
import lk.payhere.androidsdk.model.InitRequest;
import lk.payhere.androidsdk.model.StatusResponse;

public class bookticket4 extends AppCompatActivity {

    private static final String TAG = "PayHereDemo";

    private TextView pname, anumber, aemail, amobile, adate, adocname, ahospitalname, ahospitaladdress,time;
    private TextView doctorFeeTextView, hospitalFeeTextView, serviceChargeTextView;

    private FirebaseFirestore db;
    private int appointmentNumber;
    private String selectedHospital;
    private String selectedDate;
    private String selectedTime;
    private int doctorFee;
    private int hospitalFee;
    private int serviceCharge;
    private String firstName, lastName, email, mobileNumber;
    private String emailPhone;
    private String selectedDepartureStation;
    private String selectedArrivalStation;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bookticket4);

        // Initialize views
        anumber = findViewById(R.id.anumber);
        pname = findViewById(R.id.pname);
        aemail = findViewById(R.id.aemail);
        amobile = findViewById(R.id.amobile);
        adate = findViewById(R.id.adate);
       time = findViewById(R.id.time);
        adocname = findViewById(R.id.adocname);
        ahospitalname = findViewById(R.id.ahospitalname);
        ahospitaladdress = findViewById(R.id.ahospitaladdress);
        doctorFeeTextView = findViewById(R.id.doctorFeeTextView);
        hospitalFeeTextView = findViewById(R.id.hospitalFeeTextView);
        serviceChargeTextView = findViewById(R.id.serviceChargeTextView);

        // Initialize Firebase Firestore
        db = FirebaseFirestore.getInstance();

        // Retrieve data from the previous activity
        appointmentNumber = getIntent().getIntExtra("BOOKING_NUMBER", -1);
        selectedDate = getIntent().getStringExtra("SELECTED_DATE");
        selectedTime = getIntent().getStringExtra("SELECTED_TIME");
        selectedHospital = getIntent().getStringExtra("SELECTED_DEPARTURE_STATION");
        selectedDepartureStation = getIntent().getStringExtra("SELECTED_DEPARTURE_STATION");
        selectedArrivalStation = getIntent().getStringExtra("SELECTED_ARRIVAL_STATION");
        doctorFee = getIntent().getIntExtra("BASE_FARE", 0);
        hospitalFee = getIntent().getIntExtra("STATION_FEE", 0);
        serviceCharge = getIntent().getIntExtra("SERVICE_CHARGE", 0);
        emailPhone = getIntent().getStringExtra("EMAIL_PHONE");

        // Debug logging for Intent data
        Log.d("TicketSummary", "Intent data received: " +
                "\nBooking Number: " + appointmentNumber +
                "\nSelected Date: " + selectedDate +
                "\nSelected Time: " + selectedTime +
                "\nDeparture Station: " + selectedDepartureStation +
                "\nArrival Station: " + selectedArrivalStation +
                "\nBase Fare: " + doctorFee +
                "\nStation Fee: " + hospitalFee +
                "\nService Charge: " + serviceCharge);

        if (appointmentNumber == -1) {
            Toast.makeText(this, "Invalid appointment number", Toast.LENGTH_SHORT).show();
            return;
        }

        // Display fees
        doctorFeeTextView.setText("Base Fare: Rs. " + doctorFee);
        hospitalFeeTextView.setText("Station Fee: Rs. " + hospitalFee);
        serviceChargeTextView.setText("Service Charge: Rs. " + serviceCharge);

        if (selectedTime != null && !selectedTime.isEmpty()) {
            time.setText("Selected Time: " + selectedTime);
        } else {
            time.setText("Selected Time: Not Available");
        }
        // Fetch booking details from Firestore
        db.collection("bookings")
                .document("booking_" + appointmentNumber + selectedDate + selectedHospital)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document != null && document.exists()) {
                            // Debug: Log all available fields in the document
                            Log.d("TicketSummary", "Document exists. All fields: " + document.getData());
                            
                            firstName = document.getString("passengerFirstName") != null ? document.getString("passengerFirstName") : "N/A";
                            lastName = document.getString("passengerLastName") != null ? document.getString("passengerLastName") : "N/A";
                            email = document.getString("email") != null ? document.getString("email") : "N/A";
                            mobileNumber = document.getString("mobileNumber") != null ? document.getString("mobileNumber") : "N/A";
                            String selectedDate = document.getString("travelDate") != null ? document.getString("travelDate") : "N/A";
                            String busName = document.getString("busName") != null ? document.getString("busName") : "N/A";
                            String departureStation = document.getString("departureStation") != null ? document.getString("departureStation") : "N/A";
                            String arrivalStation = document.getString("arrivalStation") != null ? document.getString("arrivalStation") : "N/A";

                            // Debug logging
                            Log.d("TicketSummary", "Retrieved data: " +
                                    "\nFirst Name: " + firstName +
                                    "\nLast Name: " + lastName +
                                    "\nEmail: " + email +
                                    "\nMobile: " + mobileNumber +
                                    "\nTravel Date: " + selectedDate +
                                    "\nBus Name: " + busName +
                                    "\nDeparture Station: " + departureStation +
                                    "\nArrival Station: " + arrivalStation);

                            // Display booking details
                            anumber.setText("Booking Number: " + appointmentNumber);
                            pname.setText("Full Name: " + firstName + " " + lastName);
                            aemail.setText("Email: " + email);
                            amobile.setText("Mobile Number: " + mobileNumber);
                            adate.setText("Travel Date: " + selectedDate);
                            adocname.setText("Bus Name: " + busName);
                            // Use Intent data for stations instead of Firestore data
                            ahospitalname.setText("Departure Station: " + (selectedDepartureStation != null ? selectedDepartureStation : departureStation));
                            ahospitaladdress.setText("Arrival Station: " + (selectedArrivalStation != null ? selectedArrivalStation : arrivalStation));
                        } else {
                            // If document doesn't exist, still display what we have from Intent
                            Log.d("TicketSummary", "Document not found, using Intent data");
                            displayBasicInfo();
                        }
                    } else {
                        // Handle Firestore fetch error - still display what we have from Intent
                        String errorMessage = "Failed to fetch booking data: " + task.getException().getMessage();
                        Log.e(TAG, errorMessage);
                        Toast.makeText(this, "Using cached data", Toast.LENGTH_SHORT).show();
                        displayBasicInfo();
                    }
                });

        // Pay Later Button
        Button payLaterButton = findViewById(R.id.Paylater);
        payLaterButton.setOnClickListener(v -> saveBooking("unpaid"));

        // Pay Now Button
        Button payNowButton = findViewById(R.id.paynow);
        payNowButton.setOnClickListener(v -> initiatePayment());
    }

    private void displayBasicInfo() {
        // Display basic information from Intent data when Firestore fails
        anumber.setText("Booking Number: " + appointmentNumber);
        pname.setText("Full Name: Loading...");
        aemail.setText("Email: Loading...");
        amobile.setText("Mobile Number: Loading...");
        adate.setText("Travel Date: " + selectedDate);
        adocname.setText("Bus Name: Loading...");
        ahospitalname.setText("Departure Station: " + (selectedDepartureStation != null ? selectedDepartureStation : "N/A"));
        ahospitaladdress.setText("Arrival Station: " + (selectedArrivalStation != null ? selectedArrivalStation : "N/A"));
    }

    private void saveBooking(String paymentStatus) {
        Map<String, Object> bookingData = new HashMap<>();
        bookingData.put("paymentStatus", paymentStatus);

        db.collection("bookings")
                .document("booking_" + appointmentNumber + selectedDate + selectedHospital)
                .update(bookingData)
                .addOnSuccessListener(aVoid -> {
                    // Show notification
                    showBookingNotification(firstName + " " + lastName, String.valueOf(appointmentNumber), selectedHospital,selectedTime,selectedDate);
                    saveNotificationToFirebase(firstName + " " + lastName, String.valueOf(appointmentNumber), selectedHospital,selectedTime,selectedDate);
                    Toast.makeText(this, "Booking saved as " + paymentStatus, Toast.LENGTH_SHORT).show();
                    finish(); // Close the activity after saving

                    // Navigate to dashboard
                    Intent intent = new Intent(this, dashboard.class);
                    intent.putExtra("EMAIL_PHONE", emailPhone);
                    startActivity(intent);
                })
                .addOnFailureListener(e -> {
                    // Handle Firestore update error
                    String errorMessage = "Failed to save appointment: " + e.getMessage();
                    Log.e(TAG, errorMessage);
                    Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show();
                });
    }

    private void initiatePayment() {
        try {
            // Validate customer data before proceeding
            if (firstName == null || firstName.isEmpty() || 
                lastName == null || lastName.isEmpty() ||
                email == null || email.isEmpty() ||
                mobileNumber == null || mobileNumber.isEmpty()) {
                Toast.makeText(this, "Customer information not available. Please try again.", Toast.LENGTH_LONG).show();
                Log.e(TAG, "Customer data validation failed: " +
                        "\nFirst Name: " + firstName +
                        "\nLast Name: " + lastName +
                        "\nEmail: " + email +
                        "\nMobile: " + mobileNumber);
                return;
            }

            InitRequest req = new InitRequest();
            req.setMerchantId("1224299"); // Replace with your Merchant ID
            req.setAmount(doctorFee + hospitalFee + serviceCharge); // Total amount to charge
            req.setCurrency("LKR"); // Currency code
            req.setOrderId("ORDER_" + System.currentTimeMillis()); // Unique order ID
            req.setItemsDescription("Bus Ticket Payment - " + selectedDepartureStation + " to " + selectedArrivalStation);

            // Set customer details dynamically
            req.getCustomer().setFirstName(firstName);
            req.getCustomer().setLastName(lastName);
            req.getCustomer().setEmail(email);
            req.getCustomer().setPhone(mobileNumber);

            // Set customer address
            req.getCustomer().getAddress().setAddress("N/A");
            req.getCustomer().getAddress().setCity("Colombo");
            req.getCustomer().getAddress().setCountry("Sri Lanka");

            // Debug logging
            Log.d(TAG, "PayHere Payment Details: " +
                    "\nAmount: " + (doctorFee + hospitalFee + serviceCharge) +
                    "\nCustomer: " + firstName + " " + lastName +
                    "\nEmail: " + email +
                    "\nPhone: " + mobileNumber +
                    "\nDescription: " + req.getItemsDescription());

            // Launch PayHere payment activity
            Intent intent = new Intent(this, PHMainActivity.class);
            intent.putExtra(PHConstants.INTENT_EXTRA_DATA, req);
            PHConfigs.setBaseUrl(PHConfigs.SANDBOX_URL);
            startActivityForResult(intent, 1);
        } catch (Exception e) {
            Log.e(TAG, "Payment failed: " + e.getMessage());
            Toast.makeText(this, "Payment initialization failed: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        Log.d(TAG, "PayHere Payment Result: " +
                "\nRequest Code: " + requestCode +
                "\nResult Code: " + resultCode +
                "\nData: " + (data != null ? "Available" : "Null"));

        if (requestCode == 1) {
            if (resultCode == RESULT_OK && data != null) {
                Log.d(TAG, "Payment successful - processing result");
                if (data.hasExtra(PHConstants.INTENT_EXTRA_RESULT)) {
                    Serializable serializable = data.getSerializableExtra(PHConstants.INTENT_EXTRA_RESULT);
                    if (serializable instanceof PHResponse) {
                        PHResponse<StatusResponse> response = (PHResponse<StatusResponse>) serializable;
                        if (response.isSuccess()) {
                            Log.d(TAG, "Payment confirmed successful");
                            saveBooking("paid");
                            Toast.makeText(this, "Payment successful!", Toast.LENGTH_LONG).show();
                        } else {
                            // Handle payment failure
                            String errorMessage = "Payment failed: " + (response.getData() != null ? response.getData().getMessage() : "Unknown error");
                            Log.e(TAG, "Payment failed: " + errorMessage);
                            saveBooking("unpaid");
                            Toast.makeText(this, errorMessage, Toast.LENGTH_LONG).show();
                        }
                    } else {
                        Log.e(TAG, "Invalid response type from PayHere");
                        saveBooking("unpaid");
                        Toast.makeText(this, "Invalid payment response", Toast.LENGTH_LONG).show();
                    }
                } else {
                    Log.e(TAG, "No result data from PayHere");
                    saveBooking("unpaid");
                    Toast.makeText(this, "No payment data received", Toast.LENGTH_LONG).show();
                }
            } else if (resultCode == RESULT_CANCELED) {
                Log.d(TAG, "User canceled PayHere payment");
                Toast.makeText(this, "Payment canceled by user", Toast.LENGTH_SHORT).show();
            } else {
                Log.e(TAG, "Payment failed with result code: " + resultCode);
                Toast.makeText(this, "Payment failed", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void showBookingNotification(String name, String appointmentNumber, String hospital, String time,String date) {
        // Step 1: Create a Notification Channel (Required for Android 8.0+)
        String channelId = "booking_channel";
        String channelName = "Booking Notifications";

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    channelId,
                    channelName,
                    NotificationManager.IMPORTANCE_DEFAULT
            );
            channel.setDescription("Notifications for bus bookings");
            notificationManager.createNotificationChannel(channel);
        }

        // Step 2: Build the Notification
        String contentText = "Booking Number: " + appointmentNumber + "\nStation: " + hospital +"\nTime:" + time +"\nDate:" + date ;

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, channelId)
                .setSmallIcon(android.R.drawable.ic_dialog_info) // Use a default icon or replace with your own
                .setContentTitle("Bus Ticket Booked - " + name)
                .setContentText(contentText)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(contentText)) // Expandable text
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setAutoCancel(true); // Dismiss notification when clicked

        // Step 3: Show the Notification
        notificationManager.notify(1, builder.build());
    }
    private void saveNotificationToFirebase(String name, String appointmentNumber, String hospital, String selectedTime, String selectedDate) {
        // Get the current timestamp
        String timestamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new Date());

        // Create a map of notification data
        Map<String, Object> notificationData = new HashMap<>();
        notificationData.put("name", name);
        notificationData.put("bookingNumber", appointmentNumber);
        notificationData.put("station", hospital);
        notificationData.put("timestamp", timestamp);
        notificationData.put("isRead", false); // Mark as unread by default

        // Save the notification to Firebase Firestore
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("notifications")
                .add(notificationData) // Add a new document to the "notifications" collection
                .addOnSuccessListener(documentReference -> {
                    Log.d(TAG, "Notification saved to Firebase successfully: " + documentReference.getId());
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Failed to save notification to Firebase", e);
                });
    }
}
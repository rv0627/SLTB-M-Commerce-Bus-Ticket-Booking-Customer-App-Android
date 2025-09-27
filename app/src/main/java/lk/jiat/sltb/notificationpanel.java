package lk.jiat.sltb;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lk.jiat.sltb.model.DatabaseHelper;

public class notificationpanel extends AppCompatActivity {

    private RecyclerView recyclerView;
    private NotificationAdapter adapter;
    private List<Notification> notifications;
    private String emailPhone;
    private DatabaseHelper databaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notificationpanel);

        // Get email/phone from intent
        emailPhone = getIntent().getStringExtra("EMAIL_PHONE");
        databaseHelper = new DatabaseHelper(this);

        // Initialize back button
        ImageView backButton = findViewById(R.id.backButton);
        backButton.setOnClickListener(v -> {
            Intent intent = new Intent(notificationpanel.this, dashboard.class);
            intent.putExtra("EMAIL_PHONE", emailPhone);
            startActivity(intent);
            finish();
        });

        recyclerView = findViewById(R.id.recyclerViewNotifications);
        Button btnMarkAllAsRead = findViewById(R.id.btnMarkAllAsRead);
        Button btnClearAll = findViewById(R.id.btnClearAll);

        // Initialize the list of notifications
        notifications = new ArrayList<>();

        // Initialize RecyclerView
        adapter = new NotificationAdapter(notifications);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        // Fetch notifications from Firebase Firestore
        fetchNotificationsFromFirebase();

        // Mark All As Read
        btnMarkAllAsRead.setOnClickListener(v -> markAllNotificationsAsRead());

        // Clear All Notifications
        btnClearAll.setOnClickListener(v -> clearAllNotifications());
    }

    private void fetchNotificationsFromFirebase() {
        // Fetch notifications from the "notifications" collection
        databaseHelper.fetchNotifications(new DatabaseHelper.OnNotificationsFetchedListener() {
            @Override
            public void onNotificationsFetched(List<Map<String, Object>> notificationData) {
                notifications.clear(); // Clear existing notifications
                for (Map<String, Object> notification : notificationData) {
                    String title = (String) notification.get("name");
                    String content = "Booking Number: " + notification.get("bookingNumber") +
                            "\nStation: " + notification.get("station");
                    boolean isRead = Boolean.TRUE.equals(notification.get("isRead"));

                    // Add notification to the list
                    notifications.add(new Notification(title, content, isRead));
                }
                adapter.notifyDataSetChanged(); // Notify adapter of data changes
            }

            @Override
            public void onNotificationsFetchFailed(String error) {
                // Handle Firestore fetch error
                System.out.println("Failed to fetch notifications: " + error);
            }
        });
    }

    private void markAllNotificationsAsRead() {
        // Locally update notifications
        for (Notification notification : notifications) {
            notification.setRead(true);
        }
        adapter.notifyDataSetChanged(); // Refresh RecyclerView

        // Remotely update notifications in Firebase Firestore
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("notifications")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            String docId = document.getId();
                            Map<String, Object> updates = new HashMap<>();
                            updates.put("isRead", true); // Mark as read

                            // Update the document in Firestore
                            db.collection("notifications").document(docId).update(updates)
                                    .addOnSuccessListener(aVoid -> Log.d("Firebase", "Notification marked as read: " + docId))
                                    .addOnFailureListener(e -> Log.e("Firebase", "Failed to mark notification as read", e));
                        }
                    } else {
                        // Handle Firestore fetch error
                        String errorMessage = "Failed to fetch notifications for marking as read: " + task.getException().getMessage();
                        System.out.println(errorMessage);
                    }
                });
    }

    private void clearAllNotifications() {
        // Locally clear notifications
        notifications.clear();
        adapter.notifyDataSetChanged(); // Refresh RecyclerView

        // Remotely delete notifications in Firebase Firestore
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("notifications")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            String docId = document.getId();

                            // Delete the document in Firestore
                            db.collection("notifications").document(docId).delete()
                                    .addOnSuccessListener(aVoid -> Log.d("Firebase", "Notification deleted: " + docId))
                                    .addOnFailureListener(e -> Log.e("Firebase", "Failed to delete notification", e));
                        }
                    } else {
                        // Handle Firestore fetch error
                        String errorMessage = "Failed to fetch notifications for deletion: " + task.getException().getMessage();
                        System.out.println(errorMessage);
                    }
                });
    }
}
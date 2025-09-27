package lk.jiat.sltb.model;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.List;
import java.util.ArrayList;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String TAG = "DatabaseHelper";

    private static final int DATABASE_VERSION = 5; // Incremented version
    private static final String DATABASE_NAME = "SLTB.db";

    public static final String TABLE_USER_DETAILS = "user_details";
    public static final String COLUMN_ID = "id";
    public static final String COLUMN_EMAIL_PHONE = "email_phone";
    public static final String COLUMN_FIRST_NAME = "first_name";
    public static final String COLUMN_LAST_NAME = "last_name";
    public static final String COLUMN_EMAIL = "email";
    public static final String COLUMN_TOWN = "town";
    public static final String COLUMN_PASSWORD = "password";
    public static final String COLUMN_REGISTRATION_DATE = "registration_date";
    public static final String COLUMN_IMAGE_PATH = "image_path";
    public static final String COLUMN_IS_ACTIVE = "is_active";

    private static final String CREATE_TABLE_USER_DETAILS = "CREATE TABLE " + TABLE_USER_DETAILS + " (" +
            COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            COLUMN_EMAIL_PHONE + " TEXT UNIQUE, " +
            COLUMN_FIRST_NAME + " TEXT NOT NULL, " +
            COLUMN_LAST_NAME + " TEXT NOT NULL, " +
            COLUMN_EMAIL + " TEXT NOT NULL, " +
            COLUMN_TOWN + " TEXT NOT NULL, " +
            COLUMN_PASSWORD + " TEXT NOT NULL, " +
            COLUMN_REGISTRATION_DATE + " TEXT NOT NULL," +
            COLUMN_IMAGE_PATH + " TEXT, " +
            COLUMN_IS_ACTIVE + " BOOLEAN DEFAULT 1" + // Default value set to true (1)
            ");";

    private FirebaseFirestore firebaseFirestore;

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        firebaseFirestore = FirebaseFirestore.getInstance(); // Initialize Firebase Firestore
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_USER_DETAILS);
    }
    public void validateUserInFirestore(String emailPhone, String password, OnValidationListener listener) {
        firebaseFirestore.collection("passengers")
                .document(emailPhone)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        if (task.getResult() != null && task.getResult().exists()) {
                            String storedPassword = task.getResult().getString(COLUMN_PASSWORD);
                            if (storedPassword != null && storedPassword.equals(password)) {
                                listener.onValidationSuccess();
                            } else {
                                listener.onValidationFailure("Invalid Credentials");
                            }
                        } else {
                            listener.onValidationFailure("User not found in Firestore");
                        }
                    } else {
                        listener.onValidationFailure("Error checking Firestore: " + task.getException().getMessage());
                    }
                });
    }
    public void isUserActive(String emailPhone, OnIsActiveListener listener) {
        firebaseFirestore.collection("passengers")
                .document(emailPhone)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document != null && document.exists()) {
                            Boolean isActive = document.getBoolean("is_active");
                            listener.onIsActiveChecked(isActive != null && isActive);
                        } else {
                            listener.onIsActiveChecked(false); // User not found
                        }
                    } else {
                        listener.onIsActiveChecked(false); // Error occurred
                    }
                });
    }

    /**
     * Interface for checking active status.
     */
    public interface OnIsActiveListener {
        void onIsActiveChecked(boolean isActive);
    }



    // Define an interface for callback
    public interface OnValidationListener {
        void onValidationSuccess();
        void onValidationFailure(String errorMessage);
    }


    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion < 5) {
            // Add isActive column to existing table with a default value of true (1)
            db.execSQL("ALTER TABLE " + TABLE_USER_DETAILS + " ADD COLUMN " + COLUMN_IS_ACTIVE + " BOOLEAN DEFAULT 1");
        }
    }

    /**
     * Add a new user to both SQLite and Firebase Firestore.
     */
    public boolean addUser(String emailPhone, String firstName, String lastName, String email, String town, String password, String imagePath) {
        if (emailPhone == null || emailPhone.isEmpty() ||
                firstName == null || firstName.isEmpty() ||
                lastName == null || lastName.isEmpty() ||
                email == null || email.isEmpty() ||
                town == null || town.isEmpty() ||
                password == null || password.isEmpty()) {
            Log.e(TAG, "Invalid input: One or more fields are empty");
            return false;
        }

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_EMAIL_PHONE, emailPhone);
        values.put(COLUMN_FIRST_NAME, firstName);
        values.put(COLUMN_LAST_NAME, lastName);
        values.put(COLUMN_EMAIL, email);
        values.put(COLUMN_TOWN, town);
        values.put(COLUMN_PASSWORD, password);

        // Add registration date in "YYYY-MM-DD" format
        String registrationDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
        values.put(COLUMN_REGISTRATION_DATE, registrationDate);

        // Handle image path as NULL if no image is uploaded
        values.put(COLUMN_IMAGE_PATH, imagePath != null && !imagePath.isEmpty() ? imagePath : null);

        values.put(COLUMN_IS_ACTIVE, true);

        long result = db.insert(TABLE_USER_DETAILS, null, values);

        Log.d(TAG, "User added successfully: " + (result != -1));

        // Add user to Firebase Firestore
        if (result != -1) {
            Map<String, Object> user = new HashMap<>();
            user.put(COLUMN_EMAIL_PHONE, emailPhone);
            user.put(COLUMN_FIRST_NAME, firstName);
            user.put(COLUMN_LAST_NAME, lastName);
            user.put(COLUMN_EMAIL, email);
            user.put(COLUMN_TOWN, town);
            user.put(COLUMN_PASSWORD, password);
            user.put(COLUMN_REGISTRATION_DATE, registrationDate);
            user.put(COLUMN_IMAGE_PATH, imagePath != null && !imagePath.isEmpty() ? imagePath : null);
            user.put(COLUMN_IS_ACTIVE,true);

            firebaseFirestore.collection("passengers")
                    .document(emailPhone) // Use email/phone as the unique document ID
                    .set(user)
                    .addOnSuccessListener(aVoid -> Log.d(TAG, "User added to Firebase successfully"))
                    .addOnFailureListener(e -> Log.e(TAG, "Failed to add user to Firebase", e));
        }

        return result != -1;
    }
    private void syncWithFirestore(String emailPhone, String firstName, String lastName, String email, String town, String password, String registrationDate, boolean isActive, String imagePath) {
        Map<String, Object> user = new HashMap<>();
        user.put(COLUMN_EMAIL_PHONE, emailPhone);
        user.put(COLUMN_FIRST_NAME, firstName);
        user.put(COLUMN_LAST_NAME, lastName);
        user.put(COLUMN_EMAIL, email);
        user.put(COLUMN_TOWN, town);
        user.put(COLUMN_PASSWORD, password);
        user.put(COLUMN_REGISTRATION_DATE, registrationDate);
        user.put(COLUMN_IS_ACTIVE, isActive); // Include isActive in Firestore
        user.put(COLUMN_IMAGE_PATH, imagePath);

        firebaseFirestore.collection("passengers")
                .document(emailPhone) // Use emailPhone as the unique document ID
                .set(user)
                .addOnSuccessListener(aVoid -> Log.d(TAG, "User synced with Firestore"))
                .addOnFailureListener(e -> Log.e(TAG, "Failed to sync user with Firestore", e));
    }
    /**
     * Validate user credentials.
     */
    public boolean validateUser(String emailPhone, String password) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(
                TABLE_USER_DETAILS,
                new String[]{COLUMN_ID},
                COLUMN_EMAIL_PHONE + "=? AND " + COLUMN_PASSWORD + "=?",
                new String[]{emailPhone, password},
                null, null, null
        );
        boolean isValid = cursor.getCount() > 0;
        cursor.close();
        return isValid;
    }

    /**
     * Retrieve user details by email/phone.
     */
    public Cursor getUserDetailsByEmailPhone(String emailPhone) {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.query(
                TABLE_USER_DETAILS,
                new String[]{
                        COLUMN_ID,
                        COLUMN_EMAIL_PHONE,
                        COLUMN_FIRST_NAME,
                        COLUMN_LAST_NAME,
                        COLUMN_EMAIL,
                        COLUMN_TOWN,
                        COLUMN_PASSWORD,
                        COLUMN_REGISTRATION_DATE,
                        COLUMN_IMAGE_PATH
                },
                COLUMN_EMAIL_PHONE + "=?",
                new String[]{emailPhone},
                null, null, null
        );
    }

    /**
     * Update user password in both SQLite and Firebase Firestore.
     */
    public boolean updatePassword(String emailPhone, String newPassword) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_PASSWORD, newPassword);

        // Define the WHERE clause to identify the user
        String whereClause = COLUMN_EMAIL_PHONE + "=?";
        String[] whereArgs = {emailPhone};

        // Perform the update
        int rowsAffected = db.update(TABLE_USER_DETAILS, values, whereClause, whereArgs);

        // Update Firebase Firestore
        if (rowsAffected > 0) {
            Map<String, Object> updates = new HashMap<>();
            updates.put(COLUMN_PASSWORD, newPassword);

            firebaseFirestore.collection("passengers")
                    .document(emailPhone)
                    .update(updates)
                    .addOnSuccessListener(aVoid -> Log.d(TAG, "Password updated in Firebase successfully"))
                    .addOnFailureListener(e -> Log.e(TAG, "Failed to update password in Firebase", e));
        }

        return rowsAffected > 0;
    }

    /**
     * Update user details in both SQLite and Firebase Firestore.
     */
    public boolean updateUser(String emailPhone, String firstName, String lastName, String email, String town) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_FIRST_NAME, firstName);
        values.put(COLUMN_LAST_NAME, lastName);
        values.put(COLUMN_EMAIL, email);
        values.put(COLUMN_TOWN, town);


        // Define the WHERE clause to identify the user
        String whereClause = COLUMN_EMAIL_PHONE + "=?";
        String[] whereArgs = {emailPhone};

        // Perform the update
        int rowsAffected = db.update(TABLE_USER_DETAILS, values, whereClause, whereArgs);

        // Update Firebase Firestore
        if (rowsAffected > 0) {
            Map<String, Object> updates = new HashMap<>();
            updates.put(COLUMN_FIRST_NAME, firstName);
            updates.put(COLUMN_LAST_NAME, lastName);
            updates.put(COLUMN_EMAIL, email);
            updates.put(COLUMN_TOWN, town);


            firebaseFirestore.collection("passengers")
                    .document(emailPhone)
                    .update(updates)
                    .addOnSuccessListener(aVoid -> Log.d(TAG, "User details updated in Firebase successfully"))
                    .addOnFailureListener(e -> Log.e(TAG, "Failed to update user details in Firebase", e));
        }

        return rowsAffected > 0;
    }
    public boolean updateUserImage(String emailPhone, String imagePath) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_IMAGE_PATH, imagePath);
        String whereClause = COLUMN_EMAIL_PHONE + "=?";
        String[] whereArgs = {emailPhone};
        int rowsAffected = db.update(TABLE_USER_DETAILS, values, whereClause, whereArgs);
        // Update Firebase Firestore
        if (rowsAffected > 0) {
            Map<String, Object> updates = new HashMap<>();
            updates.put(COLUMN_IMAGE_PATH, imagePath);
            firebaseFirestore.collection("passengers")
                    .document(emailPhone)
                    .update(updates)
                    .addOnSuccessListener(aVoid -> Log.d(TAG, "User image updated in Firebase successfully"))
                    .addOnFailureListener(e -> Log.e(TAG, "Failed to update user image in Firebase", e));
        }
        return rowsAffected > 0;
    }

    // ==================== BUS OPERATIONS ====================
    
    /**
     * Fetch all buses from Firestore
     */
    public void fetchAllBuses(OnBusesFetchedListener listener) {
        firebaseFirestore.collection("buses")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        List<Map<String, Object>> buses = new ArrayList<>();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            buses.add(document.getData());
                        }
                        listener.onBusesFetched(buses);
                    } else {
                        listener.onBusesFetchFailed("Failed to fetch buses: " + task.getException().getMessage());
                    }
                });
    }

    /**
     * Fetch buses by route
     */
    public void fetchBusesByRoute(String route, OnBusesFetchedListener listener) {
        firebaseFirestore.collection("buses")
                .whereEqualTo("route", route)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        List<Map<String, Object>> buses = new ArrayList<>();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            buses.add(document.getData());
                        }
                        listener.onBusesFetched(buses);
                    } else {
                        listener.onBusesFetchFailed("Failed to fetch buses: " + task.getException().getMessage());
                    }
                });
    }

    /**
     * Fetch bus by name
     */
    public void fetchBusByName(String busName, OnBusFetchedListener listener) {
        firebaseFirestore.collection("buses")
                .whereEqualTo("bus_name", busName)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        if (!task.getResult().isEmpty()) {
                            QueryDocumentSnapshot document = (QueryDocumentSnapshot) task.getResult().getDocuments().get(0);
                            listener.onBusFetched(document.getData());
                        } else {
                            listener.onBusFetchFailed("Bus not found");
                        }
                    } else {
                        listener.onBusFetchFailed("Failed to fetch bus: " + task.getException().getMessage());
                    }
                });
    }

    // ==================== BUS STATIONS OPERATIONS ====================
    
    /**
     * Fetch all bus stations
     */
    public void fetchAllBusStations(OnStationsFetchedListener listener) {
        firebaseFirestore.collection("bus_stations")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        List<Map<String, Object>> stations = new ArrayList<>();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            stations.add(document.getData());
                        }
                        listener.onStationsFetched(stations);
                    } else {
                        listener.onStationsFetchFailed("Failed to fetch stations: " + task.getException().getMessage());
                    }
                });
    }

    /**
     * Fetch station by ID
     */
    public void fetchStationById(String stationId, OnStationFetchedListener listener) {
        firebaseFirestore.collection("bus_stations")
                .document(stationId)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
                            listener.onStationFetched(document.getData());
                        } else {
                            listener.onStationFetchFailed("Station not found");
                        }
                    } else {
                        listener.onStationFetchFailed("Failed to fetch station: " + task.getException().getMessage());
                    }
                });
    }

    // ==================== BUS SCHEDULES OPERATIONS ====================
    
    /**
     * Fetch bus schedule by bus name
     */
    public void fetchBusSchedule(String busName, OnScheduleFetchedListener listener) {
        firebaseFirestore.collection("bus_schedules")
                .whereEqualTo("busName", busName)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        if (!task.getResult().isEmpty()) {
                            QueryDocumentSnapshot document = (QueryDocumentSnapshot) task.getResult().getDocuments().get(0);
                            listener.onScheduleFetched(document.getData());
                        } else {
                            listener.onScheduleFetchFailed("Schedule not found");
                        }
                    } else {
                        listener.onScheduleFetchFailed("Failed to fetch schedule: " + task.getException().getMessage());
                    }
                });
    }

    /**
     * Fetch time slots for a specific bus and day type
     */
    public void fetchTimeSlots(String busName, String dayType, OnTimeSlotsFetchedListener listener) {
        firebaseFirestore.collection("bus_schedules")
                .whereEqualTo("busName", busName)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && !task.getResult().isEmpty()) {
                        String scheduleId = task.getResult().getDocuments().get(0).getId();
                        firebaseFirestore.collection("bus_schedules")
                                .document(scheduleId)
                                .collection(dayType)
                                .get()
                                .addOnCompleteListener(timeTask -> {
                                    if (timeTask.isSuccessful()) {
                                        List<Map<String, Object>> timeSlots = new ArrayList<>();
                                        for (QueryDocumentSnapshot document : timeTask.getResult()) {
                                            timeSlots.add(document.getData());
                                        }
                                        listener.onTimeSlotsFetched(timeSlots);
                                    } else {
                                        listener.onTimeSlotsFetchFailed("Failed to fetch time slots: " + timeTask.getException().getMessage());
                                    }
                                });
                    } else {
                        listener.onTimeSlotsFetchFailed("Schedule not found");
                    }
                });
    }

    // ==================== FARES OPERATIONS ====================
    
    /**
     * Fetch fare by bus name
     */
    public void fetchFareByBusName(String busName, OnFareFetchedListener listener) {
        firebaseFirestore.collection("fares")
                .whereEqualTo("busName", busName)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        if (!task.getResult().isEmpty()) {
                            QueryDocumentSnapshot document = (QueryDocumentSnapshot) task.getResult().getDocuments().get(0);
                            listener.onFareFetched(document.getData());
                        } else {
                            listener.onFareFetchFailed("Fare not found");
                        }
                    } else {
                        listener.onFareFetchFailed("Failed to fetch fare: " + task.getException().getMessage());
                    }
                });
    }

    // ==================== BOOKING OPERATIONS ====================
    
    /**
     * Create a new booking
     */
    public void createBooking(Map<String, Object> bookingData, OnBookingCreatedListener listener) {
        String bookingId = "booking_" + bookingData.get("bookingNumber") + "_" + 
                          bookingData.get("travelDate") + "_" + bookingData.get("departureStation");
        
        firebaseFirestore.collection("bookings")
                .document(bookingId)
                .set(bookingData)
                .addOnSuccessListener(aVoid -> {
                    Log.d(TAG, "Booking created successfully");
                    listener.onBookingCreated(true, "Booking created successfully");
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Failed to create booking", e);
                    listener.onBookingCreated(false, "Failed to create booking: " + e.getMessage());
                });
    }

    /**
     * Fetch bookings by mobile number
     */
    public void fetchBookingsByMobileNumber(String mobileNumber, OnBookingsFetchedListener listener) {
        firebaseFirestore.collection("bookings")
                .whereEqualTo("mobileNumber", mobileNumber)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        List<Map<String, Object>> bookings = new ArrayList<>();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            bookings.add(document.getData());
                        }
                        listener.onBookingsFetched(bookings);
                    } else {
                        listener.onBookingsFetchFailed("Failed to fetch bookings: " + task.getException().getMessage());
                    }
                });
    }

    /**
     * Update booking payment status
     */
    public void updateBookingPaymentStatus(String bookingId, String paymentStatus, OnBookingUpdatedListener listener) {
        Map<String, Object> updates = new HashMap<>();
        updates.put("paymentStatus", paymentStatus);
        
        firebaseFirestore.collection("bookings")
                .document(bookingId)
                .update(updates)
                .addOnSuccessListener(aVoid -> {
                    Log.d(TAG, "Booking payment status updated successfully");
                    listener.onBookingUpdated(true, "Payment status updated successfully");
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Failed to update booking payment status", e);
                    listener.onBookingUpdated(false, "Failed to update payment status: " + e.getMessage());
                });
    }

    // ==================== REVIEWS OPERATIONS ====================
    
    /**
     * Fetch reviews by bus name
     */
    public void fetchReviewsByBusName(String busName, OnReviewsFetchedListener listener) {
        firebaseFirestore.collection("reviews")
                .whereEqualTo("busName", busName)
                .orderBy("timestamp", com.google.firebase.firestore.Query.Direction.DESCENDING)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        List<Map<String, Object>> reviews = new ArrayList<>();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            reviews.add(document.getData());
                        }
                        listener.onReviewsFetched(reviews);
                    } else {
                        listener.onReviewsFetchFailed("Failed to fetch reviews: " + task.getException().getMessage());
                    }
                });
    }

    /**
     * Add a new review
     */
    public void addReview(Map<String, Object> reviewData, OnReviewAddedListener listener) {
        firebaseFirestore.collection("reviews")
                .add(reviewData)
                .addOnSuccessListener(documentReference -> {
                    Log.d(TAG, "Review added successfully");
                    listener.onReviewAdded(true, "Review added successfully");
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Failed to add review", e);
                    listener.onReviewAdded(false, "Failed to add review: " + e.getMessage());
                });
    }

    // ==================== NOTIFICATIONS OPERATIONS ====================
    
    /**
     * Fetch notifications for a user
     */
    public void fetchNotifications(OnNotificationsFetchedListener listener) {
        firebaseFirestore.collection("notifications")
                .orderBy("timestamp", com.google.firebase.firestore.Query.Direction.DESCENDING)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        List<Map<String, Object>> notifications = new ArrayList<>();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            notifications.add(document.getData());
                        }
                        listener.onNotificationsFetched(notifications);
                    } else {
                        listener.onNotificationsFetchFailed("Failed to fetch notifications: " + task.getException().getMessage());
                    }
                });
    }

    /**
     * Add a new notification
     */
    public void addNotification(Map<String, Object> notificationData, OnNotificationAddedListener listener) {
        firebaseFirestore.collection("notifications")
                .add(notificationData)
                .addOnSuccessListener(documentReference -> {
                    Log.d(TAG, "Notification added successfully");
                    listener.onNotificationAdded(true, "Notification added successfully");
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Failed to add notification", e);
                    listener.onNotificationAdded(false, "Failed to add notification: " + e.getMessage());
                });
    }

    /**
     * Mark notification as read
     */
    public void markNotificationAsRead(String notificationId, OnNotificationUpdatedListener listener) {
        Map<String, Object> updates = new HashMap<>();
        updates.put("isRead", true);
        
        firebaseFirestore.collection("notifications")
                .document(notificationId)
                .update(updates)
                .addOnSuccessListener(aVoid -> {
                    Log.d(TAG, "Notification marked as read");
                    listener.onNotificationUpdated(true, "Notification marked as read");
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Failed to mark notification as read", e);
                    listener.onNotificationUpdated(false, "Failed to mark notification as read: " + e.getMessage());
                });
    }

    // ==================== INTERFACES ====================
    
    public interface OnBusesFetchedListener {
        void onBusesFetched(List<Map<String, Object>> buses);
        void onBusesFetchFailed(String error);
    }

    public interface OnBusFetchedListener {
        void onBusFetched(Map<String, Object> bus);
        void onBusFetchFailed(String error);
    }

    public interface OnStationsFetchedListener {
        void onStationsFetched(List<Map<String, Object>> stations);
        void onStationsFetchFailed(String error);
    }

    public interface OnStationFetchedListener {
        void onStationFetched(Map<String, Object> station);
        void onStationFetchFailed(String error);
    }

    public interface OnScheduleFetchedListener {
        void onScheduleFetched(Map<String, Object> schedule);
        void onScheduleFetchFailed(String error);
    }

    public interface OnTimeSlotsFetchedListener {
        void onTimeSlotsFetched(List<Map<String, Object>> timeSlots);
        void onTimeSlotsFetchFailed(String error);
    }

    public interface OnFareFetchedListener {
        void onFareFetched(Map<String, Object> fare);
        void onFareFetchFailed(String error);
    }

    public interface OnBookingCreatedListener {
        void onBookingCreated(boolean success, String message);
    }

    public interface OnBookingsFetchedListener {
        void onBookingsFetched(List<Map<String, Object>> bookings);
        void onBookingsFetchFailed(String error);
    }

    public interface OnBookingUpdatedListener {
        void onBookingUpdated(boolean success, String message);
    }

    public interface OnReviewsFetchedListener {
        void onReviewsFetched(List<Map<String, Object>> reviews);
        void onReviewsFetchFailed(String error);
    }

    public interface OnReviewAddedListener {
        void onReviewAdded(boolean success, String message);
    }

    public interface OnNotificationsFetchedListener {
        void onNotificationsFetched(List<Map<String, Object>> notifications);
        void onNotificationsFetchFailed(String error);
    }

    public interface OnNotificationAddedListener {
        void onNotificationAdded(boolean success, String message);
    }

    public interface OnNotificationUpdatedListener {
        void onNotificationUpdated(boolean success, String message);
    }

}

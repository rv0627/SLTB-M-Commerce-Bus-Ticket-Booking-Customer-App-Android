package lk.jiat.sltb;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import com.bumptech.glide.Glide;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class Editprofile extends AppCompatActivity {

    private EditText firstNameEditText, lastNameEditText, emailEditText, contactEditText;
    private Spinner townSpinner;
    private Button updateProfileButton;
    private String emailPhone; // To identify the user
    private static final int PICK_IMAGE_REQUEST = 1; // Request code for picking an image
    private static final int CAMERA_REQUEST = 2; // Request code for camera
    private ImageView profilePicture;
    private Uri selectedImageUri; // To store the selected image URI
    private Uri cameraImageUri; // To store the camera image URI

    private FirebaseFirestore db;
    private FirebaseStorage storage;
    
    // Activity result launchers for modern Android approach
    private ActivityResultLauncher<Intent> galleryLauncher;
    private ActivityResultLauncher<Intent> cameraLauncher;
    private ActivityResultLauncher<String> permissionLauncher;

    @SuppressLint("WrongViewCast")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editprofile);

        // Initialize Firestore
        db = FirebaseFirestore.getInstance();

        // Initialize Firebase Storage
        storage = FirebaseStorage.getInstance();

        // Initialize views
        profilePicture = findViewById(R.id.profileImage);
        firstNameEditText = findViewById(R.id.firstNameedithere);
        lastNameEditText = findViewById(R.id.lastNameedithere);
        emailEditText = findViewById(R.id.emailedithere);
        contactEditText = findViewById(R.id.contactedithere);
        townSpinner = findViewById(R.id.spinner2);
        updateProfileButton = findViewById(R.id.updateprof);
        Button changeProfilePictureButton = findViewById(R.id.button5);

        // Get email/phone from the intent
        emailPhone = getIntent().getStringExtra("EMAIL_PHONE");

        // Initialize activity result launchers
        initializeLaunchers();

        // Fetch and populate current user details
        fetchUserDetailsFromFirestore(emailPhone);

        // Set click listeners
        profilePicture.setOnClickListener(v -> showImageSelectionDialog());

        changeProfilePictureButton.setOnClickListener(v -> {
            if (selectedImageUri != null || cameraImageUri != null) {
                Uri imageToUpload = selectedImageUri != null ? selectedImageUri : cameraImageUri;
                uploadImageToFirebaseStorage(imageToUpload);
            } else {
                Toast.makeText(Editprofile.this, "No image selected", Toast.LENGTH_SHORT).show();
            }
        });

        updateProfileButton.setOnClickListener(v -> {
            String firstName = firstNameEditText.getText().toString().trim();
            String lastName = lastNameEditText.getText().toString().trim();
            String email = emailEditText.getText().toString().trim();
            String contactNumber = contactEditText.getText().toString().trim();
            String town = townSpinner.getSelectedItem().toString();

            if (firstName.isEmpty() || lastName.isEmpty() || email.isEmpty() || contactNumber.isEmpty() || town.equals("Select Town")) {
                Toast.makeText(Editprofile.this, "Please fill all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            updateUserDetailsInFirestore(emailPhone, firstName, lastName, email, town);
            Toast.makeText(Editprofile.this, "Profile updated successfully", Toast.LENGTH_SHORT).show();
            finish();
        });
    }

    private void initializeLaunchers() {
        // Gallery launcher
        galleryLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    selectedImageUri = result.getData().getData();
                    if (selectedImageUri != null) {
                        profilePicture.setImageURI(selectedImageUri);
                        cameraImageUri = null; // Clear camera image when gallery is selected
                    }
                }
            }
        );

        // Camera launcher
        cameraLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK) {
                    if (cameraImageUri != null) {
                        profilePicture.setImageURI(cameraImageUri);
                        selectedImageUri = null; // Clear gallery image when camera is selected
                    }
                }
            }
        );

        // Permission launcher
        permissionLauncher = registerForActivityResult(
            new ActivityResultContracts.RequestPermission(),
            isGranted -> {
                if (isGranted) {
                    openCamera();
                } else {
                    Toast.makeText(this, "Camera permission is required to take photos", Toast.LENGTH_SHORT).show();
                }
            }
        );
    }

    private void showImageSelectionDialog() {
        androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(this);
        builder.setTitle("Select Image Source");
        builder.setItems(new String[]{"Camera", "Gallery"}, (dialog, which) -> {
            if (which == 0) {
                // Camera
                checkCameraPermissionAndOpen();
            } else {
                // Gallery
                openGallery();
            }
        });
        builder.show();
    }

    private void checkCameraPermissionAndOpen() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) 
                != PackageManager.PERMISSION_GRANTED) {
            permissionLauncher.launch(Manifest.permission.CAMERA);
        } else {
            openCamera();
        }
    }

    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        galleryLauncher.launch(intent);
    }

    private void openCamera() {
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (cameraIntent.resolveActivity(getPackageManager()) != null) {
            // Create a file to store the image
            try {
                java.io.File photoFile = createImageFile();
                if (photoFile != null) {
                    // Use FileProvider to get a secure URI
                    cameraImageUri = FileProvider.getUriForFile(this,
                            "lk.jiat.sltb.fileprovider",
                            photoFile);
                    cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, cameraImageUri);
                    cameraIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    cameraLauncher.launch(cameraIntent);
                }
            } catch (Exception e) {
                Log.e("Editprofile", "Error creating image file: " + e.getMessage());
                Toast.makeText(this, "Error creating image file", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "No camera app found", Toast.LENGTH_SHORT).show();
        }
    }

    private java.io.File createImageFile() throws java.io.IOException {
        String timeStamp = new java.text.SimpleDateFormat("yyyyMMdd_HHmmss", java.util.Locale.getDefault()).format(new java.util.Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        java.io.File storageDir = getExternalFilesDir(android.os.Environment.DIRECTORY_PICTURES);
        
        // Fallback to internal storage if external storage is not available
        if (storageDir == null) {
            storageDir = new java.io.File(getFilesDir(), "Pictures");
            if (!storageDir.exists()) {
                storageDir.mkdirs();
            }
        }
        
        return java.io.File.createTempFile(imageFileName, ".jpg", storageDir);
    }


    private void uploadImageToFirebaseStorage(Uri imageUri) {
        String fileName = "profile_" + System.currentTimeMillis() + ".jpg";
        StorageReference storageRef = storage.getReference().child("profile_images/" + fileName);

        storageRef.putFile(imageUri)
                .addOnSuccessListener(taskSnapshot -> taskSnapshot.getMetadata().getReference().getDownloadUrl()
                        .addOnSuccessListener(uri -> {
                            String imagePath = uri.toString();
                            Log.d("Editprofile", "Download URL: " + imagePath);
                            updateUserImageInFirestore(emailPhone, imagePath);
                        })
                        .addOnFailureListener(e -> {
                            Log.e("Editprofile", "Failed to get download URL: " + e.getMessage());
                            Toast.makeText(Editprofile.this, "Failed to retrieve image URL", Toast.LENGTH_SHORT).show();
                        }))
                .addOnFailureListener(e -> {
                    Log.e("Editprofile", "Image upload failed: " + e.getMessage());
                    Toast.makeText(Editprofile.this, "Failed to upload image", Toast.LENGTH_SHORT).show();
                });
    }

    private void fetchUserDetailsFromFirestore(String emailPhone) {
        db.collection("passengers")
                .whereEqualTo("email_phone", emailPhone)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        QuerySnapshot querySnapshot = task.getResult();
                        if (!querySnapshot.isEmpty()) {
                            for (QueryDocumentSnapshot document : querySnapshot) {
                                String firstName = document.getString("first_name");
                                String lastName = document.getString("last_name");
                                String email = document.getString("email");
                                String town = document.getString("town");
                                String contact = document.getString("email_phone");
                                String imagePath = document.getString("image_path");

                                firstNameEditText.setText(firstName != null ? firstName : "");
                                lastNameEditText.setText(lastName != null ? lastName : "");
                                emailEditText.setText(email != null ? email : "");
                                contactEditText.setText(contact != null ? contact : "");

                                int spinnerPosition = getSpinnerPosition(townSpinner, town != null ? town : "Select Town");
                                townSpinner.setSelection(spinnerPosition);

                                if (imagePath != null && !imagePath.isEmpty()) {
                                    Glide.with(this)
                                            .load(imagePath)
                                            .placeholder(R.drawable.defaultprofilepicture)
                                            .error(R.drawable.defaultprofilepicture)
                                            .into(profilePicture);
                                } else {
                                    profilePicture.setImageResource(R.drawable.defaultprofilepicture);
                                }
                            }
                        } else {
                            Toast.makeText(Editprofile.this, "No user data found.", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(Editprofile.this, "Failed to fetch user data from Firestore.", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void updateUserDetailsInFirestore(String emailPhone, String firstName, String lastName, String email, String town) {
        db.collection("passengers")
                .whereEqualTo("email_phone", emailPhone)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        QuerySnapshot querySnapshot = task.getResult();
                        if (!querySnapshot.isEmpty()) {
                            for (QueryDocumentSnapshot document : querySnapshot) {
                                String documentId = document.getId();
                                db.collection("passengers").document(documentId)
                                        .update(
                                                "first_name", firstName,
                                                "last_name", lastName,
                                                "email", email,
                                                "town", town
                                        )
                                        .addOnSuccessListener(aVoid -> Log.d("Editprofile", "User details updated in Firestore"))
                                        .addOnFailureListener(e -> Log.e("Editprofile", "Failed to update user details in Firestore: " + e.getMessage()));
                            }
                        }
                    } else {
                        Log.e("Editprofile", "Failed to fetch user data from Firestore: " + task.getException());
                    }
                });
    }

    private void updateUserImageInFirestore(String emailPhone, String imagePath) {
        db.collection("passengers")
                .whereEqualTo("email_phone", emailPhone)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        QuerySnapshot querySnapshot = task.getResult();
                        if (!querySnapshot.isEmpty()) {
                            for (QueryDocumentSnapshot document : querySnapshot) {
                                String documentId = document.getId();
                                db.collection("passengers").document(documentId)
                                        .update("image_path", imagePath)
                                        .addOnSuccessListener(aVoid -> {
                                            Log.d("Editprofile", "User image path updated in Firestore");
                                            Toast.makeText(Editprofile.this, "Profile picture updated", Toast.LENGTH_SHORT).show();
                                        })
                                        .addOnFailureListener(e -> {
                                            Log.e("Editprofile", "Failed to update user image path in Firestore: " + e.getMessage());
                                            Toast.makeText(Editprofile.this, "Failed to update profile picture", Toast.LENGTH_SHORT).show();
                                        });
                            }
                        }
                    } else {
                        Log.e("Editprofile", "Failed to fetch user data from Firestore: " + task.getException());
                    }
                });
    }

    private int getSpinnerPosition(Spinner spinner, String value) {
        for (int i = 0; i < spinner.getCount(); i++) {
            if (spinner.getItemAtPosition(i).toString().equalsIgnoreCase(value)) {
                return i;
            }
        }
        return 0; // Default to the first item if not found
    }
}
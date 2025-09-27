package lk.jiat.sltb;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

public class userprofile extends AppCompatActivity {

    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_userprofile);

        // Initialize views
        ImageView profilePicture = findViewById(R.id.profileImage);
        TextView userName = findViewById(R.id.UserNamehere);
        TextView email = findViewById(R.id.Emailhere);
        TextView town = findViewById(R.id.usernearesttownhere);
        TextView phone = findViewById(R.id.Contactnumberhere);
        TextView registerdate = findViewById(R.id.Registrationdatehere);

        // Set default profile picture initially
        profilePicture.setImageResource(R.drawable.defaultprofilepicture);

        // Initialize Firestore
        db = FirebaseFirestore.getInstance();

        // Get email/phone from login session
        String emailPhone = getIntent().getStringExtra("EMAIL_PHONE");

        // Fetch user details from Firestore
        fetchUserDetailsFromFirestore(emailPhone, userName, email, town, phone,registerdate, profilePicture);

        // Edit Profile Button
        Button editProfileButton = findViewById(R.id.editprofile);
        editProfileButton.setOnClickListener(v -> {
            Intent intent = new Intent(userprofile.this, Editprofile.class);
            intent.putExtra("EMAIL_PHONE", emailPhone);
            startActivity(intent);
        });

        // Change Password Button
        Button changePasswordButton = findViewById(R.id.changepw);
        changePasswordButton.setOnClickListener(v -> {
            Intent intent = new Intent(userprofile.this, Changepassword.class);
            intent.putExtra("EMAIL_PHONE", emailPhone);
            startActivity(intent);
        });
    }

    private void fetchUserDetailsFromFirestore(String emailPhone, TextView userName, TextView email,
                                               TextView town, TextView phone,TextView registerdate, ImageView profilePicture) {
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
                                String userEmail = document.getString("email");
                                String userTown = document.getString("town");
                                String contact = document.getString("email_phone");
                                String imagePath = document.getString("image_path");
                                String registerationdate = document.getString("registration_date");

                                userName.setText(firstName + " " + lastName);
                                email.setText(userEmail);
                                town.setText(userTown);
                                phone.setText(contact);
                                registerdate.setText(registerationdate);

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
                            Toast.makeText(userprofile.this, "No user data found.", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(userprofile.this, "Failed to fetch user data from Firestore.", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
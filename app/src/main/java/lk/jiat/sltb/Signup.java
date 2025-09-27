package lk.jiat.sltb;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

public class Signup extends AppCompatActivity {

    private EditText editTextMobile, editTextPassword, editTextConfirmPassword;
    private Button buttonNext;
    private FirebaseFirestore db; // Firebase Firestore instance

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_signup);

        // Initialize views
        editTextMobile = findViewById(R.id.editTextMobile);
        editTextPassword = findViewById(R.id.editTextPasswordS);
        editTextConfirmPassword = findViewById(R.id.editTextConfirmPassword);
        buttonNext = findViewById(R.id.buttonNext);

        // Initialize Firebase Firestore
        db = FirebaseFirestore.getInstance();

        // Handle "Next" button click
        buttonNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String mobileNumber = editTextMobile.getText().toString().trim();
                String password = editTextPassword.getText().toString().trim();
                String confirmPassword = editTextConfirmPassword.getText().toString().trim();

                // Validate input fields
                if (mobileNumber.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
                    Toast.makeText(Signup.this, "Please fill all fields", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (!password.equals(confirmPassword)) {
                    Toast.makeText(Signup.this, "Passwords do not match", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (mobileNumber.length() < 10) {
                    Toast.makeText(Signup.this, "Please enter a valid mobile number", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Check if the mobile number is already registered in Firestore
                checkIfMobileIsRegistered(mobileNumber, password);
            }
        });
    }

    /**
     * Check if the mobile number is already registered in Firestore.
     *
     * @param mobileNumber The mobile number to check.
     * @param password      The password entered by the user.
     */
    private void checkIfMobileIsRegistered(String mobileNumber, String password) {
        db.collection("passengers")
                .whereEqualTo("email_phone", mobileNumber) // Query the 'emailPhone' field
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        QuerySnapshot querySnapshot = task.getResult();
                        if (querySnapshot != null && !querySnapshot.isEmpty()) {
                            // Mobile number is already registered
                            Toast.makeText(Signup.this, "This mobile number is already registered", Toast.LENGTH_SHORT).show();
                        } else {
                            // Mobile number is not registered, proceed to the next activity
                            Intent intent = new Intent(Signup.this, EmailVerify.class);
                            intent.putExtra("MOBILE_NUMBER", mobileNumber);
                            intent.putExtra("PASSWORD", password); // Optional: Pass password if needed
                            startActivity(intent);
                        }
                    } else {
                        // Error occurred while querying Firestore
                        Toast.makeText(Signup.this, "Failed to check registration status", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
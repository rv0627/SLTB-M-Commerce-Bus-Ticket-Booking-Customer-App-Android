package lk.jiat.sltb;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class VerifyCode extends AppCompatActivity {
    private EditText editTextCode;
    private Button buttonVerify;
    private FirebaseFirestore firebaseFirestore;
    private String email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verify_code);

        // Initialize views
        editTextCode = findViewById(R.id.editTextCode);
        buttonVerify = findViewById(R.id.buttonVerify);
        firebaseFirestore = FirebaseFirestore.getInstance();

        // Retrieve email from Intent
        String mobileNumber = getIntent().getStringExtra("MOBILE_NUMBER");
        String password = getIntent().getStringExtra("PASSWORD");
        email = getIntent().getStringExtra("EMAIL");

        // Verify the code
        buttonVerify.setOnClickListener(v -> {
            String enteredCode = editTextCode.getText().toString().trim();
            if (enteredCode.isEmpty()) {
                Toast.makeText(this, "Please enter the verification code", Toast.LENGTH_SHORT).show();
                return;
            }

            // Check the code in Firestore
            firebaseFirestore.collection("verificationCodes")
                    .document(email)
                    .get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document != null && document.exists()) {
                                String storedCode = document.getString("code");
                                if (enteredCode.equals(storedCode)) {
                                    // Code matches
                                    Toast.makeText(this, "Email verified successfully", Toast.LENGTH_SHORT).show();

                                    // Proceed to the next activity
                                    Intent intent = new Intent(VerifyCode.this, Signupmember.class);
                                    intent.putExtra("EMAIL", email);
                                    intent.putExtra("MOBILE_NUMBER", mobileNumber);
                                    intent.putExtra("PASSWORD", password);
                                    startActivity(intent);
                                } else {
                                    // Code does not match
                                    Toast.makeText(this, "Invalid verification code", Toast.LENGTH_SHORT).show();
                                }
                            } else {
                                Toast.makeText(this, "No verification code found for this email", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(this, "Failed to verify code", Toast.LENGTH_SHORT).show();
                        }
                    });
        });
    }
}
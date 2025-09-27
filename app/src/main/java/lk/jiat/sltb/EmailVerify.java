package lk.jiat.sltb;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class EmailVerify extends AppCompatActivity {
    private EditText editTextText5;
    private Button buttonSendCode;
    private FirebaseFirestore firebaseFirestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_emailverify);



        // Initialize views
        editTextText5 = findViewById(R.id.editTextText5);
        buttonSendCode = findViewById(R.id.buttonSendCode);
        firebaseFirestore = FirebaseFirestore.getInstance();

        String mobileNumber = getIntent().getStringExtra("MOBILE_NUMBER");
        String password = getIntent().getStringExtra("PASSWORD");

        // Send verification code
        buttonSendCode.setOnClickListener(v -> {
            String email = editTextText5.getText().toString().trim();
            if (email.isEmpty()) {
                Toast.makeText(this, "Please enter your email", Toast.LENGTH_SHORT).show();
                return;
            }

            // Generate a unique verification code
            String verificationCode = UUID.randomUUID().toString().substring(0, 6); // 6-character code

            // Store the code in Firestore
            Map<String, Object> verificationData = new HashMap<>();
            verificationData.put("email", email);
            verificationData.put("code", verificationCode);

            firebaseFirestore.collection("verificationCodes")
                    .document(email)
                    .set(verificationData)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            // Send the code via email using EmailSender
                            sendVerificationEmail(email, verificationCode);

                            Toast.makeText(this, "Verification code sent to " + email, Toast.LENGTH_SHORT).show();

                            // Navigate to the VerifyCode activity
                            Intent intent = new Intent(this, VerifyCode.class);
                            intent.putExtra("EMAIL", email); // Pass the email to the next activity
                            intent.putExtra("MOBILE_NUMBER", mobileNumber);
                            intent.putExtra("PASSWORD", password);
                            startActivity(intent);
                        } else {
                            Toast.makeText(this, "Failed to send verification code", Toast.LENGTH_SHORT).show();
                        }
                    });
        });
    }

    private void sendVerificationEmail(String email, String verificationCode) {
        // Sender email and password (replace with your credentials)
        String senderEmail = "ravindumaleesha077@gmail.com"; // Replace with your email
        String senderPassword = "ojem wpgb yehp qbvo"; // Replace with your email password

        // Email subject and body
        String subject = "Your Verification Code";
        String body = "Your verification code is: " + verificationCode;

        // Use EmailSender to send the email
        new EmailSender(senderEmail, senderPassword, email, subject, body).execute();
    }
}
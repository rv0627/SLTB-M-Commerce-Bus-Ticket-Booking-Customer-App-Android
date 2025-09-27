package lk.jiat.sltb;

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

import lk.jiat.sltb.model.DatabaseHelper;

public class Changepassword extends AppCompatActivity {

    private EditText currentPasswordEditText, newPasswordEditText, reEnterPasswordEditText;
    private Button updatePasswordButton;
    private DatabaseHelper databaseHelper;
    private String emailPhone; // To identify the user

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_changepassword);

        currentPasswordEditText = findViewById(R.id.currentpwhere);
        newPasswordEditText = findViewById(R.id.newpwhere);
        reEnterPasswordEditText = findViewById(R.id.reenternewpwhere);
        updatePasswordButton = findViewById(R.id.updatepwd);

        // Initialize DatabaseHelper
        databaseHelper = new DatabaseHelper(this);

        // Get email/phone from the intent
        emailPhone = getIntent().getStringExtra("EMAIL_PHONE");

        // Set click listener for the update password button
        updatePasswordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Retrieve input values
                String currentPassword = currentPasswordEditText.getText().toString().trim();
                String newPassword = newPasswordEditText.getText().toString().trim();
                String reEnterPassword = reEnterPasswordEditText.getText().toString().trim();

                // Validate input
                if (currentPassword.isEmpty() || newPassword.isEmpty() || reEnterPassword.isEmpty()) {
                    Toast.makeText(Changepassword.this, "Please fill all fields", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (!newPassword.equals(reEnterPassword)) {
                    Toast.makeText(Changepassword.this, "New passwords do not match", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Validate current password
                if (!databaseHelper.validateUser(emailPhone, currentPassword)) {
                    Toast.makeText(Changepassword.this, "Current password is incorrect", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Update password in the database
                boolean isUpdated = databaseHelper.updatePassword(emailPhone, newPassword);
                if (isUpdated) {
                    Toast.makeText(Changepassword.this, "Password updated successfully", Toast.LENGTH_SHORT).show();
                    finish(); // Close the activity after updating
                } else {
                    Toast.makeText(Changepassword.this, "Failed to update password", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
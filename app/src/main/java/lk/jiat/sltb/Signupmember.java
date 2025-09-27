package lk.jiat.sltb;

import static android.content.ContentValues.TAG;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import lk.jiat.sltb.model.DatabaseHelper;

public class Signupmember extends AppCompatActivity {
    private EditText editTextFirstName, editTextLastName, editTextEmail;
    private Spinner spinnerTown;
    private Button buttonSignUp;
    private TextView textViewSignIn;
    private DatabaseHelper databaseHelper;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_signupmember);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });


        editTextFirstName = findViewById(R.id.editTextFirstName);
        editTextLastName = findViewById(R.id.editTextLastName);

        spinnerTown = findViewById(R.id.spinnerTown);
        buttonSignUp = findViewById(R.id.buttonSignUp);
        textViewSignIn = findViewById(R.id.textViewSignIn);


        databaseHelper = new DatabaseHelper(this);


        String mobileNumber = getIntent().getStringExtra("MOBILE_NUMBER");
        String password = getIntent().getStringExtra("PASSWORD");
        String email = getIntent().getStringExtra("EMAIL");

        buttonSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String firstName = editTextFirstName.getText().toString().trim();
                String lastName = editTextLastName.getText().toString().trim();

                String town = spinnerTown.getSelectedItem().toString();


                if (firstName.isEmpty() || lastName.isEmpty() || town.equals("Select Town")) {
                    Toast.makeText(Signupmember.this, "Please fill all fields", Toast.LENGTH_SHORT).show();
                    return;
                }

                String registrationDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
                boolean isAdded = databaseHelper.addUser(mobileNumber, firstName, lastName, email, town, password,registrationDate);
                if (isAdded) {
                    Log.d(TAG, "User added successfully");
                    Toast.makeText(Signupmember.this, "Registration Successful", Toast.LENGTH_SHORT).show();


                    Intent intent = new Intent(Signupmember.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                } else {
                    Log.e(TAG, "Failed to add user");
                    Toast.makeText(Signupmember.this, "Error: Failed to register. Please try again.", Toast.LENGTH_SHORT).show();
                }
            }
        });


        textViewSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Signupmember.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });





    }
}
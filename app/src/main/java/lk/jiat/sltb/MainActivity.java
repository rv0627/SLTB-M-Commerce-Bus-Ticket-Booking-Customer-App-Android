package lk.jiat.sltb;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.LinearLayout;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.animation.ObjectAnimator;
import android.view.animation.LinearInterpolator;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import lk.jiat.sltb.model.DatabaseHelper;

public class MainActivity extends AppCompatActivity {

    private EditText editTextEmailPhone, editTextPassword;
    private Button buttonSignIn;
    private TextView textViewForgotPassword, textViewSignUp, textViewTitle, textViewSubtitle;
    private DatabaseHelper databaseHelper;
    private CardView logoContainer, inputContainer;
    private LinearLayout signUpContainer, footer;
    private ImageView shapeOne, shapeTwo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Initialize views
        initializeViews();
        
        // Initialize database helper
        databaseHelper = new DatabaseHelper(this);

        // Start animations
        startAnimations();

        // Set click listeners
        setupClickListeners();
    }

    private void initializeViews() {
        logoContainer = findViewById(R.id.logoContainer);
        textViewTitle = findViewById(R.id.textView);
        textViewSubtitle = findViewById(R.id.textView2);
        inputContainer = findViewById(R.id.inputContainer);
        editTextEmailPhone = findViewById(R.id.editTextEmailPhone);
        editTextPassword = findViewById(R.id.editTextPassword);
        buttonSignIn = findViewById(R.id.buttonSignIn);
        textViewForgotPassword = findViewById(R.id.textViewForgotPassword);
        textViewSignUp = findViewById(R.id.textViewSignUp);
        signUpContainer = findViewById(R.id.signUpContainer);
        footer = findViewById(R.id.footer);
        shapeOne = findViewById(R.id.shapeOne);
        shapeTwo = findViewById(R.id.shapeTwo);
    }

    private void startAnimations() {
        // Load animations
        Animation fadeIn = AnimationUtils.loadAnimation(this, R.anim.fade_in);
        Animation slideUp = AnimationUtils.loadAnimation(this, R.anim.slide_up);
        Animation floatAnimation = AnimationUtils.loadAnimation(this, R.anim.float_animation);

        // Start floating shapes animation
        new Handler().postDelayed(() -> {
            shapeOne.setAlpha(1f);
            shapeTwo.setAlpha(1f);
            shapeOne.startAnimation(floatAnimation);
            shapeTwo.startAnimation(floatAnimation);
        }, 200);

        // Logo animation with rotation
        new Handler().postDelayed(() -> {
            logoContainer.setAlpha(1f);
            logoContainer.startAnimation(fadeIn);
            ObjectAnimator rotation = ObjectAnimator.ofFloat(logoContainer, "rotation", 0f, 360f);
            rotation.setDuration(1000);
            rotation.setInterpolator(new AccelerateDecelerateInterpolator());
            rotation.start();
        }, 300);

        // Title animations with scale
        new Handler().postDelayed(() -> {
            textViewTitle.setAlpha(1f);
            textViewTitle.startAnimation(fadeIn);
            ObjectAnimator scaleX = ObjectAnimator.ofFloat(textViewTitle, "scaleX", 0.8f, 1f);
            ObjectAnimator scaleY = ObjectAnimator.ofFloat(textViewTitle, "scaleY", 0.8f, 1f);
            scaleX.setDuration(500);
            scaleY.setDuration(500);
            scaleX.start();
            scaleY.start();
        }, 600);

        // Subtitle animation
        new Handler().postDelayed(() -> {
            textViewSubtitle.setAlpha(1f);
            textViewSubtitle.startAnimation(fadeIn);
        }, 800);

        // Input container animation with scale
        new Handler().postDelayed(() -> {
            inputContainer.setVisibility(View.VISIBLE);
            inputContainer.startAnimation(slideUp);
            ObjectAnimator scaleX = ObjectAnimator.ofFloat(inputContainer, "scaleX", 0.9f, 1f);
            ObjectAnimator scaleY = ObjectAnimator.ofFloat(inputContainer, "scaleY", 0.9f, 1f);
            scaleX.setDuration(500);
            scaleY.setDuration(500);
            scaleX.start();
            scaleY.start();
        }, 1000);

        // Button animation with bounce
        new Handler().postDelayed(() -> {
            buttonSignIn.setVisibility(View.VISIBLE);
            buttonSignIn.startAnimation(slideUp);
            ObjectAnimator scaleX = ObjectAnimator.ofFloat(buttonSignIn, "scaleX", 0.8f, 1.1f, 1f);
            ObjectAnimator scaleY = ObjectAnimator.ofFloat(buttonSignIn, "scaleY", 0.8f, 1.1f, 1f);
            scaleX.setDuration(800);
            scaleY.setDuration(800);
            scaleX.start();
            scaleY.start();
        }, 1200);

        // Forgot password animation
        new Handler().postDelayed(() -> {
            textViewForgotPassword.setAlpha(1f);
            textViewForgotPassword.startAnimation(fadeIn);
        }, 1400);

        // Sign up container animation
        new Handler().postDelayed(() -> {
            signUpContainer.setAlpha(1f);
            signUpContainer.startAnimation(fadeIn);
        }, 1600);

        // Footer animation
        new Handler().postDelayed(() -> {
            footer.setAlpha(1f);
            footer.startAnimation(fadeIn);
        }, 1800);
    }

    private void setupClickListeners() {
        textViewSignUp.setOnClickListener(view -> {
            Intent intent = new Intent(MainActivity.this, Signup.class);
            startActivity(intent);
            overridePendingTransition(R.anim.slide_in_right, android.R.anim.fade_out);
        });

        buttonSignIn.setOnClickListener(view -> {
            String emailPhone = editTextEmailPhone.getText().toString().trim();
            String password = editTextPassword.getText().toString().trim();

            if (emailPhone.isEmpty() || password.isEmpty()) {
                showError("Please fill in all fields");
                return;
            }

            // Show loading state with animation
            ObjectAnimator scaleX = ObjectAnimator.ofFloat(buttonSignIn, "scaleX", 1f, 0.95f);
            ObjectAnimator scaleY = ObjectAnimator.ofFloat(buttonSignIn, "scaleY", 1f, 0.95f);
            scaleX.setDuration(200);
            scaleY.setDuration(200);
            scaleX.start();
            scaleY.start();
            
            buttonSignIn.setEnabled(false);
            buttonSignIn.setText("Signing in...");

            // Validate remotely (Firestore)
            databaseHelper.validateUserInFirestore(emailPhone, password, new DatabaseHelper.OnValidationListener() {
                @Override
                public void onValidationSuccess() {
                    // Check if the user is active in Firestore
                    databaseHelper.isUserActive(emailPhone, isActive -> {
                        if (isActive) {
                            handleLoginSuccess(emailPhone);
                        } else {
                            showError("Your account is inactive. Please contact help center.");
                        }
                        resetButton();
                    });
                }

                @Override
                public void onValidationFailure(String errorMessage) {
                    showError(errorMessage);
                    resetButton();
                }
            });
        });
    }

    private void showError(String message) {
        Toast.makeText(MainActivity.this, message, Toast.LENGTH_SHORT).show();
    }

    private void resetButton() {
        buttonSignIn.setEnabled(true);
        buttonSignIn.setText("Sign In");
        ObjectAnimator scaleX = ObjectAnimator.ofFloat(buttonSignIn, "scaleX", 0.95f, 1f);
        ObjectAnimator scaleY = ObjectAnimator.ofFloat(buttonSignIn, "scaleY", 0.95f, 1f);
        scaleX.setDuration(200);
        scaleY.setDuration(200);
        scaleX.start();
        scaleY.start();
    }

    private void handleLoginSuccess(String emailPhone) {
        Toast.makeText(MainActivity.this, "Login Successful", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(MainActivity.this, dashboard.class);
        intent.putExtra("EMAIL_PHONE", emailPhone);
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in_right, android.R.anim.fade_out);
        finish();
    }
}


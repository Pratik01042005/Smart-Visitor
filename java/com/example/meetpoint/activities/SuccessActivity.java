package com.example.meetpoint.activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.animation.AlphaAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.meetpoint.R;

public class SuccessActivity extends AppCompatActivity {

    Button btnCreateNewPass;
    TextView tvEmailMessage, tvResubmit;
    ImageView ivSuccessCheck;

    String email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_success);

        initViews();
        loadEmailMessage();
        playSuccessAnimation();
        setupListeners();
    }

    private void initViews() {
        btnCreateNewPass = findViewById(R.id.btnCreateNewPass);
        tvEmailMessage = findViewById(R.id.tvEmailMessage);
        tvResubmit = findViewById(R.id.tvResubmit);
        ivSuccessCheck = findViewById(R.id.ivSuccessCheck);

        email = getIntent().getStringExtra("email");
    }

    private void loadEmailMessage() {
        if (email != null && !email.isEmpty()) {
            tvEmailMessage.setText("A password reset link has been sent to:\n" + email);
        } else {
            tvEmailMessage.setText("Please check your email to reset password.");
        }
    }

    private void playSuccessAnimation() {
        AlphaAnimation fadeIn = new AlphaAnimation(0f, 1f);
        fadeIn.setDuration(1200);
        fadeIn.setFillAfter(true);
        ivSuccessCheck.startAnimation(fadeIn);
    }

    private void setupListeners() {

        btnCreateNewPass.setOnClickListener(v -> {
            Intent intent = new Intent(SuccessActivity.this, ResetPasswordActivity.class);
            startActivity(intent);
            finish();
        });

        tvResubmit.setOnClickListener(v -> {
            Intent intent = new Intent(SuccessActivity.this, ForgotPasswordActivity.class);
            startActivity(intent);
            finish();
        });
    }

    @SuppressLint("MissingSuperCall")
    @Override
    public void onBackPressed() {
        // Prevent going back accidentally
        Intent i = new Intent(SuccessActivity.this, LoginActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(i);
        finish();
    }
}

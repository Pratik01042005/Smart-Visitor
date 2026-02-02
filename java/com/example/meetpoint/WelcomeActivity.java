package com.example.meetpoint;

import android.content.Intent;
import android.os.Bundle;
import android.view.animation.AlphaAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.meetpoint.R;
import com.example.meetpoint.activities.LoginActivity;
import com.example.meetpoint.activities.SignupActivity;

public class WelcomeActivity extends AppCompatActivity {

    ImageView ivLogo;
    TextView tvTitle, tvSubtitle;
    Button btnLogin, btnSignup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        initViews();
        animateViews();
        setupListeners();
    }

    private void initViews() {
        ivLogo = findViewById(R.id.ivLogo);
        tvTitle = findViewById(R.id.tvTitle);
        tvSubtitle = findViewById(R.id.tvSubtitle);
        btnLogin = findViewById(R.id.btnLogin);
        btnSignup = findViewById(R.id.btnSignup);
    }

    private void animateViews() {

        AlphaAnimation fadeIn = new AlphaAnimation(0f, 1f);
        fadeIn.setDuration(1200);
        fadeIn.setFillAfter(true);

        ivLogo.startAnimation(fadeIn);
        tvTitle.startAnimation(fadeIn);
        tvSubtitle.startAnimation(fadeIn);
        btnLogin.startAnimation(fadeIn);
        btnSignup.startAnimation(fadeIn);
    }

    private void setupListeners() {

        btnLogin.setOnClickListener(v ->
                startActivity(new Intent(WelcomeActivity.this, LoginActivity.class))
        );

        btnSignup.setOnClickListener(v ->
                startActivity(new Intent(WelcomeActivity.this, SignupActivity.class))
        );
    }
}

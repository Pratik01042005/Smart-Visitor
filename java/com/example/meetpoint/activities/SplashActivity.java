package com.example.meetpoint.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import androidx.appcompat.app.AppCompatActivity;

import com.example.meetpoint.AdminDashboardActivity;
import com.example.meetpoint.R;
import com.example.meetpoint.WelcomeActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        new Handler().postDelayed(() -> {

            if (FirebaseAuth.getInstance().getCurrentUser() != null) {
                goToDashboard();
            } else {
                startActivity(new Intent(this, WelcomeActivity.class));
                finish();
            }

        }, 1200);
    }

    private void goToDashboard() {

        FirebaseFirestore.getInstance()
                .collection("Users")
                .document(FirebaseAuth.getInstance().getUid())
                .get()
                .addOnSuccessListener(doc -> {

                    if (!doc.exists()) {
                        FirebaseAuth.getInstance().signOut();
                        startActivity(new Intent(this, WelcomeActivity.class));
                        finish();
                        return;
                    }

                    String role = doc.getString("role");

                    if ("admin".equals(role)) {
                        startActivity(new Intent(this, AdminDashboardActivity.class));
                    } else {
                        startActivity(new Intent(this, SuperAdminDashboardActivity.class));
                    }
                    finish();
                })
                .addOnFailureListener(e -> {
                    startActivity(new Intent(this, WelcomeActivity.class));
                    finish();
                });
    }
}

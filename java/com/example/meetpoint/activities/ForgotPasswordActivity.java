package com.example.meetpoint.activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.meetpoint.R;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

public class ForgotPasswordActivity extends AppCompatActivity {

    EditText etEmail;
    Button btnSubmit;
    ImageView ivBack;

    FirebaseAuth mAuth;
    FirebaseFirestore db;
    ProgressDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        FirebaseApp.initializeApp(this);

        initViews();
        setupListeners();
    }

    private void initViews() {
        etEmail = findViewById(R.id.etEmail);
        btnSubmit = findViewById(R.id.btnSubmit);
        ivBack = findViewById(R.id.ivBack);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        dialog = new ProgressDialog(this);
        dialog.setMessage("Checking email...");
        dialog.setCancelable(false);
    }

    private void setupListeners() {
        ivBack.setOnClickListener(v -> finish());
        btnSubmit.setOnClickListener(v -> validateEmail());
    }

    private void validateEmail() {

        String email = etEmail.getText().toString().trim();

        if (email.isEmpty()) {
            etEmail.setError("Enter email");
            etEmail.requestFocus();
            return;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            etEmail.setError("Invalid email format");
            etEmail.requestFocus();
            return;
        }

        if (!isConnected()) {
            Toast.makeText(this, "No internet connection", Toast.LENGTH_SHORT).show();
            return;
        }

        checkUserExists(email);
    }

    private void checkUserExists(String email) {

        dialog.show();
        btnSubmit.setEnabled(false);

        db.collection("Users")
                .whereEqualTo("email", email)
                .limit(1)
                .get()
                .addOnSuccessListener(query -> {

                    if (query.isEmpty()) {
                        dialog.dismiss();
                        btnSubmit.setEnabled(true);

                        Toast.makeText(this,
                                "This email is not registered. Please sign up first.",
                                Toast.LENGTH_LONG).show();
                        return;
                    }

                    sendResetEmail(email);
                })
                .addOnFailureListener(e -> {
                    dialog.dismiss();
                    btnSubmit.setEnabled(true);

                    Toast.makeText(this,
                            "Error checking email: " + e.getMessage(),
                            Toast.LENGTH_LONG).show();
                });
    }

    private void sendResetEmail(String email) {

        mAuth.sendPasswordResetEmail(email)
                .addOnCompleteListener(task -> {

                    dialog.dismiss();
                    btnSubmit.setEnabled(true);

                    if (task.isSuccessful()) {

                        Toast.makeText(this,
                                "Reset link sent! Check your email inbox.",
                                Toast.LENGTH_LONG).show();

                        Intent i = new Intent(this, SuccessActivity.class);
                        i.putExtra("email", email);
                        startActivity(i);
                        finish();

                    } else {
                        Log.e("RESET_ERROR", String.valueOf(task.getException()));

                        Toast.makeText(this,
                                "Failed: " + task.getException().getMessage(),
                                Toast.LENGTH_LONG).show();
                    }
                });
    }

    private boolean isConnected() {
        ConnectivityManager cm =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo n = cm.getActiveNetworkInfo();
        return n != null && n.isConnected();
    }
}

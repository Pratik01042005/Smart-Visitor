package com.example.meetpoint.activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.meetpoint.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class ResetPasswordActivity extends AppCompatActivity {

    EditText etNewPass, etConfirmPass;
    Button btnResetPass;

    FirebaseAuth mAuth;
    FirebaseUser user;

    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);

        initViews();
        setupListeners();
    }

    private void initViews() {
        etNewPass = findViewById(R.id.etNewPass);
        etConfirmPass = findViewById(R.id.etConfirmPass);
        btnResetPass = findViewById(R.id.btnResetPass);

        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Updating password...");
        progressDialog.setCancelable(false);
    }

    private void setupListeners() {
        btnResetPass.setOnClickListener(v -> {
            if (!btnResetPass.isEnabled()) return;
            validateInputs();
        });
    }

    private void validateInputs() {

        String newPass = etNewPass.getText().toString().trim();
        String confirmPass = etConfirmPass.getText().toString().trim();

        if (newPass.isEmpty()) {
            etNewPass.setError("Enter new password");
            etNewPass.requestFocus();
            return;
        }

        if (newPass.length() < 6) {
            etNewPass.setError("Password must be 6+ characters");
            etNewPass.requestFocus();
            return;
        }

        if (confirmPass.isEmpty()) {
            etConfirmPass.setError("Confirm password");
            etConfirmPass.requestFocus();
            return;
        }

        if (!newPass.equals(confirmPass)) {
            etConfirmPass.setError("Passwords do not match");
            etConfirmPass.requestFocus();
            return;
        }

        if (!isConnected()) {
            Toast.makeText(this, "No internet connection!", Toast.LENGTH_SHORT).show();
            return;
        }

        updatePassword(newPass);
    }

    private boolean isConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo net = cm.getActiveNetworkInfo();
        return net != null && net.isConnected();
    }

    private void updatePassword(String newPass) {

        if (user == null) {
            Toast.makeText(this, "User not logged in!", Toast.LENGTH_SHORT).show();
            return;
        }

        progressDialog.show();
        btnResetPass.setEnabled(false);

        user.updatePassword(newPass)
                .addOnSuccessListener(unused -> {
                    progressDialog.dismiss();
                    Toast.makeText(this, "Password updated successfully!", Toast.LENGTH_SHORT).show();
                    finish();
                })
                .addOnFailureListener(e -> {
                    progressDialog.dismiss();
                    btnResetPass.setEnabled(true);
                    Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                });

    }
}

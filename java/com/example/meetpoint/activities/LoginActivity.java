package com.example.meetpoint.activities;

import android.app.ProgressDialog;
import android.content.*;
import android.net.*;
import android.os.Bundle;
import android.text.method.*;
import android.util.Patterns;
import android.widget.*;

import androidx.appcompat.app.AppCompatActivity;

import com.example.meetpoint.AdminDashboardActivity;
import com.example.meetpoint.R;
import com.example.meetpoint.activities.SuperAdminDashboardActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.*;

public class LoginActivity extends AppCompatActivity {

    EditText etEmail, etPassword;
    ImageView ivTogglePassword, ivBack;
    Button btnLogin;
    TextView tvSignup, tvForgotPassword;

    FirebaseAuth mAuth;
    FirebaseFirestore db;
    ProgressDialog progressDialog;

    boolean isPasswordVisible = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        initViews();
        setupListeners();
    }

    private void initViews() {
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        ivTogglePassword = findViewById(R.id.ivTogglePassword);
        ivBack = findViewById(R.id.ivBack);

        tvSignup = findViewById(R.id.tvSignup);
        tvForgotPassword = findViewById(R.id.tvForgotPassword);
        btnLogin = findViewById(R.id.btnLogin);

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Logging in...");
        progressDialog.setCancelable(false);
    }

    private void setupListeners() {

        ivBack.setOnClickListener(v -> finish());

        ivTogglePassword.setOnClickListener(v -> togglePassword());

        tvSignup.setOnClickListener(v ->
                startActivity(new Intent(LoginActivity.this, SignupActivity.class)));

        tvForgotPassword.setOnClickListener(v ->
                startActivity(new Intent(LoginActivity.this, ForgotPasswordActivity.class)));

        btnLogin.setOnClickListener(v -> validateInputs());
    }

    // ================= PASSWORD TOGGLE =================
    private void togglePassword() {
        if (isPasswordVisible) {
            etPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
            ivTogglePassword.setImageResource(R.drawable.ic_eye_closed);
        } else {
            etPassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
            ivTogglePassword.setImageResource(R.drawable.ic_eye_open);
        }
        isPasswordVisible = !isPasswordVisible;
        etPassword.setSelection(etPassword.length());
    }

    // ================= INPUT VALIDATION =================
    private void validateInputs() {
        String email = etEmail.getText().toString().trim();
        String pass = etPassword.getText().toString().trim();

        if (email.isEmpty()) {
            etEmail.setError("Enter email");
            return;
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            etEmail.setError("Invalid email");
            return;
        }
        if (pass.length() < 6) {
            etPassword.setError("Minimum 6 characters");
            return;
        }
        if (!isConnected()) {
            Toast.makeText(this, "No internet connection", Toast.LENGTH_SHORT).show();
            return;
        }

        loginUser(email, pass);
    }

    // ================= INTERNET CHECK =================
    private boolean isConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo n = cm.getActiveNetworkInfo();
        return n != null && n.isConnected();
    }

    // ================= LOGIN =================
    private void loginUser(String email, String password) {
        btnLogin.setEnabled(false);
        progressDialog.show();

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnSuccessListener(authResult -> {
                    if (mAuth.getCurrentUser() == null) {
                        showError("Login failed, try again");
                        return;
                    }
                    fetchUserRole(mAuth.getCurrentUser().getUid());
                })
                .addOnFailureListener(e -> showError(e.getMessage()));
    }

    // ================= ROLE FETCH =================
    private void fetchUserRole(String uid) {
        db.collection("Users").document(uid)
                .get()
                .addOnSuccessListener(snap -> {

                    if (!snap.exists()) {
                        mAuth.signOut();
                        showError("Account data not found");
                        return;
                    }

                    navigateBasedOnRole(snap);
                })
                .addOnFailureListener(e -> showError(e.getMessage()));
    }

    // ================= NAVIGATION =================
    private void navigateBasedOnRole(DocumentSnapshot snap) {

        progressDialog.dismiss();
        btnLogin.setEnabled(true);

        String role = snap.getString("role");

        Intent i;
        if ("superadmin".equals(role)) {
            i = new Intent(this, SuperAdminDashboardActivity.class);
        } else {
            i = new Intent(this, AdminDashboardActivity.class);
        }

        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(i);
        finish();
    }

    // ================= ERROR =================
    private void showError(String msg) {
        progressDialog.dismiss();
        btnLogin.setEnabled(true);
        Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
    }
}

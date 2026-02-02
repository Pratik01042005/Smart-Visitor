package com.example.meetpoint;

import android.content.Intent;
import android.os.Bundle;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;

import com.example.meetpoint.activities.ResetPasswordActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

public class ProfileActivity extends AppCompatActivity {

    ImageView ivBack, ivEdit, imgProfile;
    TextView tvName, tvPhone, tvEmail, tvAddress;
    LinearLayout btnResetPassword;
    Button btnLogout;

    FirebaseAuth mAuth;
    FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        initViews();
        setupListeners();
    }

    private void initViews() {

        ivBack = findViewById(R.id.ivBack);
        ivEdit = findViewById(R.id.ivEdit);
        imgProfile = findViewById(R.id.imgProfile);

        tvName = findViewById(R.id.tvName);
        tvPhone = findViewById(R.id.tvPhone);
        tvEmail = findViewById(R.id.tvEmail);
        tvAddress = findViewById(R.id.tvAddress);

        btnResetPassword = findViewById(R.id.btnResetPassword);
        btnLogout = findViewById(R.id.btnLogout);
    }

    private void loadProfileData() {

        String uid = mAuth.getUid();
        if (uid == null) {
            logoutAndExit();
            return;
        }

        db.collection("Users")
                .document(uid)
                .get()
                .addOnSuccessListener(doc -> {
                    if (!doc.exists()) return;

                    String first = doc.getString("firstName");
                    String last = doc.getString("lastName");

                    String fullName = "";
                    if (first != null) fullName += first;
                    if (last != null) fullName += " " + last;

                    tvName.setText(fullName.trim());
                    tvPhone.setText(doc.getString("phone"));
                    tvEmail.setText(doc.getString("email"));
                    tvAddress.setText(doc.getString("address"));
                });
    }

    private void setupListeners() {

        ivBack.setOnClickListener(v -> finish());

        ivEdit.setOnClickListener(v ->
                startActivity(new Intent(ProfileActivity.this, EditUserProfileActivity.class)));

        btnResetPassword.setOnClickListener(v ->
                startActivity(new Intent(ProfileActivity.this, ResetPasswordActivity.class)));

        btnLogout.setOnClickListener(v -> logoutAndExit());

        imgProfile.setOnClickListener(v ->
                Toast.makeText(this, "Profile image feature coming soon", Toast.LENGTH_SHORT).show());
    }

    private void logoutAndExit() {
        mAuth.signOut();
        Intent i = new Intent(ProfileActivity.this, WelcomeActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(i);
        finish();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadProfileData();
    }
}

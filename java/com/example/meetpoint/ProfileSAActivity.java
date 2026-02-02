package com.example.meetpoint;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.meetpoint.activities.LoginActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

public class ProfileSAActivity extends AppCompatActivity {

    // ================= Views =================
    ImageView ivBack;
    TextView tvName, tvEmail, tvPhone;
    Button btnEditProfile, btnLogout;

    // ================= Firebase =================
    FirebaseAuth auth;
    FirebaseFirestore db;

    // ================= Lifecycle =================
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_saactivity);

        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        initViews();
        setupClicks();
    }

    // ================= INIT =================
    private void initViews() {
        ivBack = findViewById(R.id.ivBack);
        tvName = findViewById(R.id.tvName);
        tvEmail = findViewById(R.id.tvEmail);
        tvPhone = findViewById(R.id.tvPhone);
        btnEditProfile = findViewById(R.id.btnEditProfile);
        btnLogout = findViewById(R.id.btnLogout);
    }

    // ================= LOAD PROFILE =================
    private void loadProfile() {

        // 🔐 Session check
        if (auth.getCurrentUser() == null) {
            Toast.makeText(this, "Session expired. Login again.", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        }

        String uid = auth.getCurrentUser().getUid();

        db.collection("Users")
                .document(uid)
                .get()
                .addOnSuccessListener(doc -> {
                    if (!doc.exists()) {
                        Toast.makeText(this, "Profile not found", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    tvName.setText(doc.getString("name"));
                    tvEmail.setText(doc.getString("email"));
                    tvPhone.setText(doc.getString("phone"));
                })
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Load failed: " + e.getMessage(), Toast.LENGTH_SHORT).show()
                );
    }

    // ================= CLICKS =================
    private void setupClicks() {

        // 🔙 Back
        ivBack.setOnClickListener(v -> finish());

        // ✏️ Edit Profile
        btnEditProfile.setOnClickListener(v -> {
            Intent i = new Intent(this, EditSuperAdminProfile.class);
            startActivity(i);
        });

        // 🚪 Logout
        btnLogout.setOnClickListener(v -> {
            auth.signOut();
            Intent i = new Intent(this, LoginActivity.class);
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(i);
            finish();
        });
    }

    // ================= AUTO REFRESH =================
    @Override
    protected void onResume() {
        super.onResume();
        loadProfile(); // always refresh latest data
    }
}

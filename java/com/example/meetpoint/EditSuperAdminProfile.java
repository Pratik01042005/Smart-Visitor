package com.example.meetpoint;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.util.HashMap;
import java.util.Map;

public class EditSuperAdminProfile extends AppCompatActivity {

    ImageView btnBack;
    EditText etName, etEmail, etPhone;
    Button btnSave;

    FirebaseAuth auth;
    FirebaseFirestore db;
    String uid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_super_admin_profile);

        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        if (auth.getCurrentUser() == null) {
            Toast.makeText(this, "User not logged in", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        uid = auth.getCurrentUser().getUid();

        initViews();
        loadData();
        setupClicks();
    }

    // ================= INIT =================
    private void initViews() {
        btnBack = findViewById(R.id.btnBack);
        etName = findViewById(R.id.etName);
        etEmail = findViewById(R.id.etEmail);
        etPhone = findViewById(R.id.etPhone);
        btnSave = findViewById(R.id.btnSave);

        etEmail.setEnabled(false);
        etEmail.setAlpha(0.6f);
    }

    // ================= LOAD =================
    private void loadData() {
        db.collection("Users").document(uid)
                .get()
                .addOnSuccessListener(doc -> {
                    if (!doc.exists()) return;

                    etName.setText(doc.getString("name"));
                    etEmail.setText(doc.getString("email"));
                    etPhone.setText(doc.getString("phone"));
                })
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Load failed: " + e.getMessage(), Toast.LENGTH_SHORT).show()
                );
    }

    // ================= CLICKS =================
    private void setupClicks() {

        btnBack.setOnClickListener(v -> finish());

        btnSave.setOnClickListener(v -> updateProfile());
    }

    // ================= UPDATE =================
    private void updateProfile() {

        String name = etName.getText().toString().trim();
        String phone = etPhone.getText().toString().trim();

        if (name.isEmpty()) {
            etName.setError("Enter name");
            return;
        }

        if (!phone.matches("\\d{10}")) {
            etPhone.setError("Enter valid 10 digit phone");
            return;
        }

        Map<String, Object> map = new HashMap<>();
        map.put("name", name);
        map.put("phone", phone);
        map.put("updatedAt", System.currentTimeMillis());

        // 🔥 IMPORTANT FIX
        db.collection("Users")
                .document(uid)
                .set(map, SetOptions.merge())   // ✅ ALWAYS WORKS
                .addOnSuccessListener(unused -> {
                    Toast.makeText(this, "Profile updated successfully", Toast.LENGTH_SHORT).show();
                    setResult(RESULT_OK);
                    finish();
                })
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Update failed: " + e.getMessage(), Toast.LENGTH_LONG).show()
                );
    }
}

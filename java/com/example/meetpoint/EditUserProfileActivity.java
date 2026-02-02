package com.example.meetpoint;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;

public class EditUserProfileActivity extends AppCompatActivity {

    ImageView ivBack;
    EditText etName, etPhone, etAddress;
    TextView tvEmail;
    Button btnSave, btnCancel;

    FirebaseAuth mAuth;
    FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_user_profile);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        initViews();
        loadData();
        setupListeners();
    }

    private void initViews() {
        ivBack = findViewById(R.id.ivBack);
        etName = findViewById(R.id.etName);
        etPhone = findViewById(R.id.etPhone);
        etAddress = findViewById(R.id.etAddress);
        tvEmail = findViewById(R.id.tvEmail);

        btnSave = findViewById(R.id.btnSave);
        btnCancel = findViewById(R.id.btnCancel);
    }

    private void loadData() {

        String uid = mAuth.getUid();
        if (uid == null) return;

        db.collection("Users").document(uid)
                .get()
                .addOnSuccessListener(doc -> {
                    if (!doc.exists()) return;

                    String first = doc.getString("firstName");
                    String last = doc.getString("lastName");

                    String fullName = "";
                    if (first != null) fullName += first;
                    if (last != null) fullName += " " + last;

                    etName.setText(fullName.trim());
                    etPhone.setText(doc.getString("phone"));
                    tvEmail.setText(doc.getString("email"));
                    etAddress.setText(doc.getString("address"));
                });
    }

    private void setupListeners() {

        ivBack.setOnClickListener(v -> finish());
        btnCancel.setOnClickListener(v -> finish());

        btnSave.setOnClickListener(v -> updateProfile());
    }

    private void updateProfile() {

        String uid = mAuth.getUid();
        if (uid == null) return;

        String fullName = etName.getText().toString().trim();
        String phone = etPhone.getText().toString().trim();
        String address = etAddress.getText().toString().trim();

        if (TextUtils.isEmpty(fullName)) {
            etName.setError("Enter full name");
            return;
        }

        if (!phone.matches("\\d{10}")) {
            etPhone.setError("Enter valid 10 digit phone");
            return;
        }

        String firstName, lastName;
        String[] parts = fullName.split(" ", 2);

        firstName = parts[0];
        lastName = (parts.length > 1) ? parts[1] : "";

        HashMap<String, Object> map = new HashMap<>();
        map.put("firstName", firstName);
        map.put("lastName", lastName);
        map.put("phone", phone);
        map.put("address", address);

        db.collection("Users")
                .document(uid)
                .update(map)
                .addOnSuccessListener(v -> {
                    Toast.makeText(this, "Profile Updated", Toast.LENGTH_SHORT).show();
                    finish();
                })
                .addOnFailureListener(e ->
                        Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show()
                );
    }
}

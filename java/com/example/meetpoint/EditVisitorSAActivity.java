package com.example.meetpoint;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.*;

public class EditVisitorSAActivity extends AppCompatActivity {

    EditText etName, etPhone, etEmail, etAddress, etPurpose, etWhomToMeet, etDate, etTime;
    Button btnUpdate;
    ImageView ivBack;

    FirebaseFirestore db;
    String visitorId;

    String selectedDate = "";
    String selectedTime = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_visitor_saactivity);

        db = FirebaseFirestore.getInstance();

        // ✅ MUST get visitorId
        visitorId = getIntent().getStringExtra("visitorId");

        if (visitorId == null || visitorId.isEmpty()) {
            Toast.makeText(this, "Visitor ID missing!", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        initViews();
        loadVisitorData();
        setListeners();
    }

    private void initViews() {
        ivBack = findViewById(R.id.ivBack);
        etName = findViewById(R.id.etName);
        etPhone = findViewById(R.id.etPhone);
        etEmail = findViewById(R.id.etEmail);
        etAddress = findViewById(R.id.etAddress);
        etPurpose = findViewById(R.id.etPurpose);
        etWhomToMeet = findViewById(R.id.etWhomToMeet);
        etDate = findViewById(R.id.etDate);
        etTime = findViewById(R.id.etTime);
        btnUpdate = findViewById(R.id.btnUpdate);
    }

    private void setListeners() {

        ivBack.setOnClickListener(v -> finish());

        etDate.setOnClickListener(v -> showDatePicker());
        etTime.setOnClickListener(v -> showTimePicker());

        btnUpdate.setOnClickListener(v -> validate());
    }

    // ================= DATE PICKER =================
    private void showDatePicker() {
        Calendar c = Calendar.getInstance();

        new DatePickerDialog(this, (view, y, m, d) -> {
            Calendar cal = Calendar.getInstance();
            cal.set(y, m, d);
            selectedDate = new SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
                    .format(cal.getTime());
            etDate.setText(selectedDate);
        }, c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH)).show();
    }

    // ================= TIME PICKER =================
    private void showTimePicker() {
        Calendar c = Calendar.getInstance();

        new TimePickerDialog(this, (view, h, m) -> {
            selectedTime = String.format(Locale.getDefault(), "%02d:%02d", h, m);
            etTime.setText(selectedTime);
        }, c.get(Calendar.HOUR_OF_DAY), c.get(Calendar.MINUTE), true).show();
    }

    // ================= LOAD OLD DATA =================
    private void loadVisitorData() {
        db.collection("Visitors").document(visitorId)
                .get()
                .addOnSuccessListener(doc -> {
                    if (!doc.exists()) {
                        Toast.makeText(this, "Visitor not found", Toast.LENGTH_SHORT).show();
                        finish();
                        return;
                    }

                    etName.setText(doc.getString("name"));
                    etPhone.setText(doc.getString("phone"));
                    etEmail.setText(doc.getString("email"));
                    etAddress.setText(doc.getString("address"));
                    etPurpose.setText(doc.getString("purpose"));
                    etWhomToMeet.setText(doc.getString("whomToMeet"));

                    selectedDate = doc.getString("visitDate");
                    selectedTime = doc.getString("visitTime");

                    etDate.setText(selectedDate);
                    etTime.setText(selectedTime);
                })
                .addOnFailureListener(e ->
                        Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show()
                );
    }

    // ================= VALIDATION =================
    private void validate() {

        if (TextUtils.isEmpty(etName.getText())) {
            etName.setError("Required");
            return;
        }

        if (!etPhone.getText().toString().matches("\\d{10}")) {
            etPhone.setError("Enter 10 digit phone");
            return;
        }

        if (!etEmail.getText().toString().endsWith("@gmail.com")) {
            etEmail.setError("Email must end with @gmail.com");
            return;
        }

        if (TextUtils.isEmpty(selectedDate)) {
            etDate.setError("Select date");
            return;
        }

        if (TextUtils.isEmpty(selectedTime)) {
            etTime.setError("Select time");
            return;
        }

        updateVisitor();
    }

    // ================= UPDATE =================
    private void updateVisitor() {

        Map<String, Object> map = new HashMap<>();
        map.put("name", etName.getText().toString().trim());
        map.put("phone", etPhone.getText().toString().trim());
        map.put("email", etEmail.getText().toString().trim());
        map.put("address", etAddress.getText().toString().trim());
        map.put("purpose", etPurpose.getText().toString().trim());
        map.put("whomToMeet", etWhomToMeet.getText().toString().trim());
        map.put("visitDate", selectedDate);
        map.put("visitTime", selectedTime);

        db.collection("Visitors").document(visitorId)
                .update(map)
                .addOnSuccessListener(v -> {
                    Toast.makeText(this, "Visitor Updated Successfully", Toast.LENGTH_SHORT).show();
                    finish();
                })
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Update failed: " + e.getMessage(), Toast.LENGTH_LONG).show()
                );
    }
}
package com.example.meetpoint;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.TextUtils;
import android.util.Log;
import android.widget.*;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;

public class EditProfileActivity extends AppCompatActivity {

    // ================= Views =================
    EditText etName, etPhone, etEmail, etAddress,
            etDescription, etDate, etTime, etWhomToMeet;

    RadioGroup rgGender;
    Spinner spPurpose;
    Button btnSave, btnCancel;
    ImageView ivBack;

    // ================= Firebase =================
    FirebaseFirestore db;
    String visitorId = "";

    // ================= Data =================
    String selectedGender = "", selectedPurpose = "";
    String selectedDate = "", selectedTime = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        db = FirebaseFirestore.getInstance();

        // ✅ FIX 1: Safe visitorId
        if (getIntent() != null && getIntent().hasExtra("visitorId")) {
            visitorId = getIntent().getStringExtra("visitorId");
        }

        if (visitorId == null || visitorId.isEmpty()) {
            Toast.makeText(this, "Visitor ID missing!", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        initViews();
        setupSpinner();
        setupListeners();
        loadOldData();
    }

    // ================= INIT =================
    private void initViews() {

        ivBack = findViewById(R.id.ivBack);
        etName = findViewById(R.id.etName);
        etPhone = findViewById(R.id.etPhone);
        etEmail = findViewById(R.id.etEmail);
        etAddress = findViewById(R.id.etAddress);
        etDescription = findViewById(R.id.etDescription);
        etDate = findViewById(R.id.etDate);
        etTime = findViewById(R.id.etTime);
        etWhomToMeet = findViewById(R.id.etWhomToMeet);

        rgGender = findViewById(R.id.rgGender);
        spPurpose = findViewById(R.id.spPurpose);

        btnSave = findViewById(R.id.btnSave);
        btnCancel = findViewById(R.id.btnCancel);

        etPhone.setFilters(new InputFilter[]{new InputFilter.LengthFilter(10)});
    }

    // ================= SPINNER =================
    private void setupSpinner() {
        String[] purposes = {
                "Select Purpose",
                "Meeting",
                "Interview",
                "Delivery",
                "Official",
                "Personal",
                "Other"
        };

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_dropdown_item,
                purposes
        );
        spPurpose.setAdapter(adapter);
    }

    // ================= LISTENERS =================
    private void setupListeners() {

        ivBack.setOnClickListener(v -> finish());
        btnCancel.setOnClickListener(v -> finish());

        etDate.setOnClickListener(v -> showDatePicker());
        etTime.setOnClickListener(v -> showTimePicker());

        btnSave.setOnClickListener(v -> validateAndUpdate());
    }

    // ================= DATE PICKER =================
    private void showDatePicker() {
        Calendar c = Calendar.getInstance();
        new DatePickerDialog(
                this,
                (v, y, m, d) -> {
                    Calendar sel = Calendar.getInstance();
                    sel.set(y, m, d);
                    selectedDate = new SimpleDateFormat(
                            "dd MMM yyyy",
                            Locale.getDefault()
                    ).format(sel.getTime());
                    etDate.setText(selectedDate);
                },
                c.get(Calendar.YEAR),
                c.get(Calendar.MONTH),
                c.get(Calendar.DAY_OF_MONTH)
        ).show();
    }

    // ================= TIME PICKER =================
    private void showTimePicker() {
        Calendar c = Calendar.getInstance();
        new TimePickerDialog(
                this,
                (v, h, m) -> {
                    selectedTime = String.format(
                            Locale.getDefault(),
                            "%02d:%02d",
                            h, m
                    );
                    etTime.setText(selectedTime);
                },
                c.get(Calendar.HOUR_OF_DAY),
                c.get(Calendar.MINUTE),
                true
        ).show();
    }

    // ================= LOAD OLD DATA =================
    private void loadOldData() {

        db.collection("Visitors")
                .document(visitorId)
                .get()
                .addOnSuccessListener(doc -> {

                    if (!doc.exists()) return;

                    etName.setText(doc.getString("name"));
                    etPhone.setText(doc.getString("phone"));
                    etEmail.setText(doc.getString("email"));
                    etAddress.setText(doc.getString("address"));
                    etDescription.setText(doc.getString("description"));
                    etWhomToMeet.setText(doc.getString("whomToMeet"));

                    selectedDate = doc.getString("visitDate");
                    selectedTime = doc.getString("visitTime");
                    etDate.setText(selectedDate);
                    etTime.setText(selectedTime);

                    selectedGender = doc.getString("gender");
                    selectedPurpose = doc.getString("purpose");

                    if ("Male".equals(selectedGender)) ((RadioButton)findViewById(R.id.rbMale)).setChecked(true);
                    if ("Female".equals(selectedGender)) ((RadioButton)findViewById(R.id.rbFemale)).setChecked(true);
                    if ("Other".equals(selectedGender)) ((RadioButton)findViewById(R.id.rbOther)).setChecked(true);

                    for (int i = 0; i < spPurpose.getCount(); i++) {
                        if (spPurpose.getItemAtPosition(i).toString().equals(selectedPurpose)) {
                            spPurpose.setSelection(i);
                            break;
                        }
                    }
                });
    }

    private void validateAndUpdate() {

        String name = etName.getText().toString().trim();
        String phone = etPhone.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String whom = etWhomToMeet.getText().toString().trim();

        int checked = rgGender.getCheckedRadioButtonId();
        if (checked != -1)
            selectedGender = ((RadioButton) findViewById(checked)).getText().toString();

        selectedPurpose = spPurpose.getSelectedItem().toString();

        if (TextUtils.isEmpty(name)) { etName.setError("Required"); return; }
        if (!phone.matches("\\d{10}")) { etPhone.setError("10 digit only"); return; }
        if (!email.endsWith("@gmail.com")) { etEmail.setError("@gmail.com required"); return; }
        if (TextUtils.isEmpty(selectedGender)) { Toast.makeText(this,"Select gender",Toast.LENGTH_SHORT).show(); return; }
        if (selectedPurpose.equals("Select Purpose")) { Toast.makeText(this,"Select purpose",Toast.LENGTH_SHORT).show(); return; }
        if (TextUtils.isEmpty(whom)) { etWhomToMeet.setError("Enter whom to meet"); return; }
        if (TextUtils.isEmpty(selectedDate)) { etDate.setError("Select date"); return; }
        if (TextUtils.isEmpty(selectedTime)) { etTime.setError("Select time"); return; }

        HashMap<String, Object> map = new HashMap<>();
        map.put("name", name);
        map.put("phone", phone);
        map.put("email", email);
        map.put("address", etAddress.getText().toString());
        map.put("description", etDescription.getText().toString());
        map.put("whomToMeet", whom);
        map.put("gender", selectedGender);
        map.put("purpose", selectedPurpose);
        map.put("visitDate", selectedDate);
        map.put("visitTime", selectedTime);

        map.put("timestamp", System.currentTimeMillis());

        db.collection("Visitors")
                .document(visitorId)
                .update(map)
                .addOnSuccessListener(v -> {
                    Toast.makeText(this, "Updated successfully", Toast.LENGTH_SHORT).show();
                    finish();
                })
                .addOnFailureListener(e -> {
                    Log.e("UPDATE_ERROR", e.getMessage());
                    Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
                });
    }
}

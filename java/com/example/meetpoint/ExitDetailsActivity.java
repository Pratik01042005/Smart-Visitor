package com.example.meetpoint;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.*;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.*;

public class ExitDetailsActivity extends AppCompatActivity {

    EditText etExitDateTime, etRemark, etNextVisit, etMessage;
    RadioGroup rgAgainVisit;
    Spinner spStatus;
    Button btnUpdateExit;

    FirebaseFirestore db;
    String visitorId = "";

    String exitDateTime = "";
    String nextVisitDateTime = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.exit_details_layout);

        db = FirebaseFirestore.getInstance();

        visitorId = getIntent().getStringExtra("visitorId");
        if (visitorId == null || visitorId.isEmpty()) {
            Toast.makeText(this, "Visitor ID missing", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        initViews();
        setupStatusSpinner();
        setupListeners();
    }

    private void initViews() {

        etExitDateTime = findViewById(R.id.etExitDateTime);
        etRemark = findViewById(R.id.etRemark);
        etNextVisit = findViewById(R.id.etNextVisit);
        etMessage = findViewById(R.id.etMessage);
        rgAgainVisit = findViewById(R.id.rgAgainVisit);
        spStatus = findViewById(R.id.spStatus);
        btnUpdateExit = findViewById(R.id.btnUpdateExit);
    }

    private void setupStatusSpinner() {

        String[] statusList = {
                "Completed",
                "Rejected",
                "Revisit"
        };

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_dropdown_item,
                statusList
        );
        spStatus.setAdapter(adapter);
    }

    private void setupListeners() {

        etExitDateTime.setOnClickListener(v -> pickDateTime(etExitDateTime, true));
        etNextVisit.setOnClickListener(v -> pickDateTime(etNextVisit, false));

        rgAgainVisit.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.rbYes) {
                etNextVisit.setVisibility(View.VISIBLE);
            } else {
                etNextVisit.setVisibility(View.GONE);
                nextVisitDateTime = "";
            }
        });

        btnUpdateExit.setOnClickListener(v -> validateAndUpdate());
    }

    private void pickDateTime(EditText target, boolean isExit) {

        Calendar c = Calendar.getInstance();

        new DatePickerDialog(this, (view, y, m, d) -> {

            Calendar selected = Calendar.getInstance();
            selected.set(y, m, d);

            new TimePickerDialog(this, (tView, h, min) -> {

                selected.set(Calendar.HOUR_OF_DAY, h);
                selected.set(Calendar.MINUTE, min);

                String formatted = new SimpleDateFormat(
                        "dd MMM yyyy • hh:mm a",
                        Locale.getDefault()
                ).format(selected.getTime());

                target.setText(formatted);

                if (isExit) {
                    exitDateTime = formatted;
                } else {
                    nextVisitDateTime = formatted;
                }

            }, c.get(Calendar.HOUR_OF_DAY), c.get(Calendar.MINUTE), false).show();

        }, c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH)).show();
    }

    private void validateAndUpdate() {

        String remark = etRemark.getText().toString().trim();
        String message = etMessage.getText().toString().trim();
        String status = spStatus.getSelectedItem().toString();

        int againVisitChecked = rgAgainVisit.getCheckedRadioButtonId();
        boolean againVisit = againVisitChecked == R.id.rbYes;

        if (TextUtils.isEmpty(exitDateTime)) {
            etExitDateTime.setError("Select exit date & time");
            return;
        }

        if (againVisit && TextUtils.isEmpty(nextVisitDateTime)) {
            etNextVisit.setError("Select next visit date & time");
            return;
        }

        Map<String, Object> map = new HashMap<>();
        map.put("exitDateTime", exitDateTime);
        map.put("remark", remark);
        map.put("againVisit", againVisit);
        map.put("nextVisitDateTime", nextVisitDateTime);
        map.put("message", message);
        map.put("status", status);
        map.put("exitTimestamp", System.currentTimeMillis());

        db.collection("Visitors")
                .document(visitorId)
                .update(map)
                .addOnSuccessListener(v -> {
                    Toast.makeText(this, "Exit details updated", Toast.LENGTH_SHORT).show();
                    finish();
                })
                .addOnFailureListener(e ->
                        Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show()
                );
    }
}

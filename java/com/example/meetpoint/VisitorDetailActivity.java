package com.example.meetpoint;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.*;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.File;

public class VisitorDetailActivity extends AppCompatActivity {

    // ================= HEADER =================
    ImageView btnBack, btnEdit, btnDelete;

    // ================= PROFILE =================
    ImageView ivVisitorPhoto;
    TextView tvVisitorName, tvToken, tvStatus;

    // ================= TOGGLE =================
    Button btnEntry, btnExit;
    LinearLayout layoutEntry, layoutExit;

    // ================= ENTRY DETAILS =================
    TextView tvVisitDateTime, tvPurpose;
    TextView tvTempAddress, tvPermAddress;
    TextView tvPhone, tvEmail, tvWhom, tvWhomPhone, tvDescription;

    ImageView btnCall;

    // ================= VEHICLE =================
    ImageView ivVehicle;
    TextView tvVehicleNumber;

    // ================= EXIT =================
    TextView tvExitDateTime, tvRemark, tvAgainVisit, tvNextVisit;

    // ================= FIREBASE =================
    FirebaseFirestore db;
    String visitorId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_visitor_detail);

        db = FirebaseFirestore.getInstance();
        visitorId = getIntent().getStringExtra("visitorId");

        initViews();
        setupClicks();
        loadVisitor();
    }

    // ================= INIT =================
    private void initViews() {

        btnBack = findViewById(R.id.btnBack);
        btnEdit = findViewById(R.id.btnEdit);
        btnDelete = findViewById(R.id.btnDelete);

        ivVisitorPhoto = findViewById(R.id.ivVisitorPhoto);
        tvVisitorName = findViewById(R.id.tvVisitorName);
        tvToken = findViewById(R.id.tvToken);
        tvStatus = findViewById(R.id.tvStatus);

        btnEntry = findViewById(R.id.btnEntry);
        btnExit = findViewById(R.id.btnExit);
        layoutEntry = findViewById(R.id.layoutEntry);
        layoutExit = findViewById(R.id.layoutExit);

        tvVisitDateTime = findViewById(R.id.tvVisitDateTime);
        tvPurpose = findViewById(R.id.tvPurpose);

        tvTempAddress = findViewById(R.id.tvTempAddress);
        tvPermAddress = findViewById(R.id.tvPermAddress);

        tvPhone = findViewById(R.id.tvPhone);
        tvEmail = findViewById(R.id.tvEmail);

        tvWhom = findViewById(R.id.tvWhom);
        tvWhomPhone = findViewById(R.id.tvWhomPhone);
        btnCall = findViewById(R.id.btnCall);

        tvDescription = findViewById(R.id.tvDescription);

        ivVehicle = findViewById(R.id.ivVehicle);
        tvVehicleNumber = findViewById(R.id.tvVehicleNumber);

        tvExitDateTime = findViewById(R.id.tvExitDateTime);
        tvRemark = findViewById(R.id.tvRemark);
        tvAgainVisit = findViewById(R.id.tvAgainVisit);
        tvNextVisit = findViewById(R.id.tvNextVisit);
    }

    // ================= CLICKS =================
    private void setupClicks() {

        btnBack.setOnClickListener(v -> finish());

        btnEntry.setOnClickListener(v -> {
            layoutEntry.setVisibility(View.VISIBLE);
            layoutExit.setVisibility(View.GONE);
        });

        btnExit.setOnClickListener(v -> {
            layoutEntry.setVisibility(View.GONE);
            layoutExit.setVisibility(View.VISIBLE);
        });

        btnCall.setOnClickListener(v -> {
            String phone = tvWhomPhone.getText().toString();
            startActivity(new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + phone)));
        });

        btnEdit.setOnClickListener(v -> {
            Intent i = new Intent(this, AddVisitorActivity.class);
            i.putExtra("visitorId", visitorId);
            startActivity(i);
        });

        btnDelete.setOnClickListener(v -> confirmDelete());
    }

    // ================= LOAD =================
    private void loadVisitor() {

        db.collection("Visitors")
                .document(visitorId)
                .get()
                .addOnSuccessListener(this::bindData);
    }

    // ================= BIND =================
    private void bindData(DocumentSnapshot d) {

        if (!d.exists()) return;

        tvVisitorName.setText(d.getString("name"));
        tvToken.setText(d.getString("token"));

        String status = d.getString("status");
        tvStatus.setText("STATUS : " + status.toUpperCase());

        if ("approved".equalsIgnoreCase(status)) tvStatus.setTextColor(Color.GREEN);
        else if ("rejected".equalsIgnoreCase(status)) tvStatus.setTextColor(Color.RED);
        else tvStatus.setTextColor(Color.YELLOW);

        tvVisitDateTime.setText(d.getString("visitDateTime"));
        tvPurpose.setText(d.getString("purpose"));

        tvTempAddress.setText(d.getString("tempAddress"));
        tvPermAddress.setText(d.getString("permAddress"));

        tvPhone.setText(d.getString("phone"));
        tvEmail.setText(d.getString("email"));

        tvWhom.setText(d.getString("whomToMeet"));
        tvWhomPhone.setText(d.getString("whomPhone"));

        tvDescription.setText(d.getString("description"));

        tvExitDateTime.setText(d.getString("exitDateTime"));
        tvRemark.setText(d.getString("remark"));
        tvAgainVisit.setText(d.getString("againVisit"));
        tvNextVisit.setText(d.getString("nextVisit"));

        // ================= PHOTO =================
        String path = d.getString("photoPath");
        if (path != null && !path.isEmpty()) {
            Glide.with(this)
                    .load(new File(path))
                    .into(ivVisitorPhoto);
        }

        loadVehicle();
    }

    // ================= VEHICLE =================
    private void loadVehicle() {

        db.collection("Visitors")
                .document(visitorId)
                .collection("Vehicle")
                .document("vehicle")
                .get()
                .addOnSuccessListener(v -> {

                    if (!v.exists()) {
                        ivVehicle.setVisibility(View.GONE);
                        tvVehicleNumber.setVisibility(View.GONE);
                        return;
                    }

                    tvVehicleNumber.setText(v.getString("vehicleNumber"));

                    String image = v.getString("vehicleImage");
                    if (image != null && !image.isEmpty()) {
                        Glide.with(this).load(new File(image)).into(ivVehicle);
                    }
                });
    }

    // ================= DELETE =================
    private void confirmDelete() {
        new AlertDialog.Builder(this)
                .setTitle("Delete Visitor")
                .setMessage("Are you sure you want to delete this visitor?")
                .setPositiveButton("Delete", (d, w) -> {
                    db.collection("Visitors").document(visitorId).delete();
                    finish();
                })
                .setNegativeButton("Cancel", null)
                .show();
    }
}

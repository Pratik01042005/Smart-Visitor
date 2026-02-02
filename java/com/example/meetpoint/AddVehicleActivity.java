package com.example.meetpoint;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;

public class AddVehicleActivity extends AppCompatActivity {

    ImageView ivBack, imgVehicle;
    EditText etVehicleNumber;
    Button btnCapture, btnSave;
    TextView tvSkip;

    Bitmap vehiclePhoto = null;

    static final int REQ_CAMERA = 201;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_vehicle);

        initViews();
        setupClicks();
    }

    private void initViews() {
        ivBack = findViewById(R.id.ivBack);
        imgVehicle = findViewById(R.id.imgVehicle);
        etVehicleNumber = findViewById(R.id.etVehicleNumber);
        btnCapture = findViewById(R.id.btnCapture);
        btnSave = findViewById(R.id.btnSave);
        tvSkip = findViewById(R.id.tvSkip);
    }

    private void setupClicks() {

        ivBack.setOnClickListener(v -> finish());

        btnCapture.setOnClickListener(v -> openCamera());

        btnSave.setOnClickListener(v -> saveVehicle());

        tvSkip.setOnClickListener(v -> {
            setResult(RESULT_CANCELED);
            finish();
        });
    }

    // ================= CAMERA =================
    private void openCamera() {
        Intent i = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(i, REQ_CAMERA);
    }

    @Override
    protected void onActivityResult(int req, int res, Intent data) {
        super.onActivityResult(req, res, data);

        if (res == RESULT_OK && req == REQ_CAMERA && data != null) {
            vehiclePhoto = (Bitmap) data.getExtras().get("data");
            imgVehicle.setImageBitmap(vehiclePhoto);
        }
    }

    // ================= SAVE =================
    private void saveVehicle() {

        String vehicleNumber = etVehicleNumber.getText().toString().trim();

        if (TextUtils.isEmpty(vehicleNumber)) {
            etVehicleNumber.setError("Enter vehicle number");
            return;
        }

        Intent data = new Intent();
        data.putExtra("vehicleNumber", vehicleNumber);

        // (Photo saving to Firebase Storage can be added later)
        setResult(RESULT_OK, data);
        finish();
    }
}

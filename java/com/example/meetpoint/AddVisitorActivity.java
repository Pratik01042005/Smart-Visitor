package com.example.meetpoint;

import android.Manifest;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.*;
import android.provider.MediaStore;
import android.view.View;
import android.widget.*;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.*;

import com.google.firebase.firestore.FirebaseFirestore;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;

public class AddVisitorActivity extends AppCompatActivity {

    // ================= UI =================
    ImageView ivBack, ivPhoto;
    TextView tvToken;

    EditText etName, etVisitDateTime,
            etTempAddressLine, etTempCity, etTempPincode,
            etPermAddressLine, etPermCity, etPermPincode,
            etPhone, etEmail, etWhomToMeet, etPhoneWhom, etDescription,
            etExitDateTime, etRemark, etNextVisit;

    Spinner spPurpose;
    RadioGroup rgAgainVisit;
    RadioButton rbYes, rbNo;

    Button btnEntry, btnExit, btnSave, btnUpdateExit, btnAddVehicle;
    LinearLayout layoutEntry, layoutExit;

    // ================= DATA =================
    FirebaseFirestore db;
    String visitorId = "";
    String photoPath = "";
    Uri photoUri;

    // ================= CAMERA =================
    ActivityResultLauncher<Intent> cameraLauncher =
            registerForActivityResult(
                    new ActivityResultContracts.StartActivityForResult(),
                    result -> {
                        if (result.getResultCode() == RESULT_OK && photoPath != null) {
                            ivPhoto.setImageBitmap(BitmapFactory.decodeFile(photoPath));
                        }
                    });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_visitor);

        db = FirebaseFirestore.getInstance();

        initViews();
        setupPurposeSpinner();
        setupClicks();
        generateToken();
    }

    // ================= INIT =================
    private void initViews() {

        ivBack = findViewById(R.id.ivBack);
        ivPhoto = findViewById(R.id.ivPhoto);
        tvToken = findViewById(R.id.tvToken);

        etName = findViewById(R.id.etName);
        etVisitDateTime = findViewById(R.id.etVisitDateTime);

        etTempAddressLine = findViewById(R.id.etTempAddressLine);
        etTempCity = findViewById(R.id.etTempCity);
        etTempPincode = findViewById(R.id.etTempPincode);

        etPermAddressLine = findViewById(R.id.etPermAddressLine);
        etPermCity = findViewById(R.id.etPermCity);
        etPermPincode = findViewById(R.id.etPermPincode);

        etPhone = findViewById(R.id.etPhone);
        etEmail = findViewById(R.id.etEmail);
        etWhomToMeet = findViewById(R.id.etWhomToMeet);
        etPhoneWhom = findViewById(R.id.etPhoneWhom);
        etDescription = findViewById(R.id.etDescription);

        etExitDateTime = findViewById(R.id.etExitDateTime);
        etRemark = findViewById(R.id.etRemark);
        etNextVisit = findViewById(R.id.etNextVisit);

        spPurpose = findViewById(R.id.spPurpose);
        rgAgainVisit = findViewById(R.id.rgAgainVisit);
        rbYes = findViewById(R.id.rbYes);
        rbNo = findViewById(R.id.rbNo);

        btnEntry = findViewById(R.id.btnEntry);
        btnExit = findViewById(R.id.btnExit);
        btnSave = findViewById(R.id.btnSave);
        btnUpdateExit = findViewById(R.id.btnUpdateExit);
        btnAddVehicle = findViewById(R.id.btnAddVehicle);

        layoutEntry = findViewById(R.id.layoutEntry);
        layoutExit = findViewById(R.id.layoutExit);

        etNextVisit.setVisibility(View.GONE);
    }

    // ================= PURPOSE =================
    private void setupPurposeSpinner() {
        String[] list = {
                "Inquiry", "Training", "Job", "Documentation",
                "Interview", "Meeting", "Delivery",
                "Official Work", "Personal Work", "Others"
        };
        spPurpose.setAdapter(
                new ArrayAdapter<>(this,
                        android.R.layout.simple_spinner_dropdown_item, list));
    }

    // ================= CLICKS =================
    private void setupClicks() {

        ivBack.setOnClickListener(v -> finish());

        btnEntry.setOnClickListener(v -> {
            layoutEntry.setVisibility(View.VISIBLE);
            layoutExit.setVisibility(View.GONE);
        });

        btnExit.setOnClickListener(v -> {
            layoutEntry.setVisibility(View.GONE);
            layoutExit.setVisibility(View.VISIBLE);
        });

        etVisitDateTime.setOnClickListener(v -> pickDateTime(etVisitDateTime));
        etExitDateTime.setOnClickListener(v -> pickDateTime(etExitDateTime));
        etNextVisit.setOnClickListener(v -> pickDateTime(etNextVisit));

        ivPhoto.setOnClickListener(v -> checkCameraPermission());

        btnAddVehicle.setOnClickListener(v ->
                startActivity(new Intent(this, AddVehicleActivity.class)));

        btnSave.setOnClickListener(v -> saveVisitor());
        btnUpdateExit.setOnClickListener(v -> updateExit());

        rgAgainVisit.setOnCheckedChangeListener((g, id) ->
                etNextVisit.setVisibility(id == R.id.rbYes ? View.VISIBLE : View.GONE));
    }

    // ================= CAMERA =================
    private void checkCameraPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                    this, new String[]{Manifest.permission.CAMERA}, 200);
        } else openCamera();
    }

    private void openCamera() {
        try {
            File file = createImageFile();
            photoUri = FileProvider.getUriForFile(
                    this, getPackageName() + ".provider", file);

            Intent i = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            i.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
            cameraLauncher.launch(i);

        } catch (Exception e) {
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private File createImageFile() throws IOException {
        File dir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File file = File.createTempFile("VIS_", ".jpg", dir);
        photoPath = file.getAbsolutePath();
        return file;
    }

    // ================= DATE TIME =================
    private void pickDateTime(EditText target) {
        Calendar c = Calendar.getInstance();
        new DatePickerDialog(this, (d, y, m, day) ->
                new TimePickerDialog(this, (t, h, min) -> {
                    Calendar cal = Calendar.getInstance();
                    cal.set(y, m, day, h, min);
                    target.setText(new SimpleDateFormat(
                            "dd MMM yyyy • hh:mm a", Locale.getDefault())
                            .format(cal.getTime()));
                }, c.get(Calendar.HOUR_OF_DAY), c.get(Calendar.MINUTE), false).show(),
                c.get(Calendar.YEAR), c.get(Calendar.MONTH),
                c.get(Calendar.DAY_OF_MONTH)).show();
    }

    // ================= SAVE VISITOR =================
    private void saveVisitor() {

        if (etName.getText().toString().trim().isEmpty()) {
            etName.setError("Required");
            return;
        }

        visitorId = UUID.randomUUID().toString();

        Map<String, Object> map = new HashMap<>();

        map.put("id", visitorId);
        map.put("token", tvToken.getText().toString());
        map.put("name", etName.getText().toString());
        map.put("purpose", spPurpose.getSelectedItem().toString());
        map.put("visitDateTime", etVisitDateTime.getText().toString());

        map.put("tempAddress",
                etTempAddressLine.getText() + ", " +
                        etTempCity.getText() + " - " +
                        etTempPincode.getText());

        map.put("permAddress",
                etPermAddressLine.getText() + ", " +
                        etPermCity.getText() + " - " +
                        etPermPincode.getText());

        map.put("phone", etPhone.getText().toString());
        map.put("email", etEmail.getText().toString());
        map.put("whomToMeet", etWhomToMeet.getText().toString());
        map.put("whomPhone", etPhoneWhom.getText().toString());
        map.put("description", etDescription.getText().toString());
        map.put("photoPath", photoPath);

        map.put("status", "pending");
        map.put("timestamp", System.currentTimeMillis());

        db.collection("Visitors").document(visitorId).set(map)
                .addOnSuccessListener(v -> {
                    Toast.makeText(this,
                            "Visitor saved. Add exit details now.",
                            Toast.LENGTH_LONG).show();
                    layoutEntry.setVisibility(View.GONE);
                    layoutExit.setVisibility(View.VISIBLE);
                });
    }

    // ================= EXIT =================
    private void updateExit() {

        Map<String, Object> map = new HashMap<>();
        map.put("exitDateTime", etExitDateTime.getText().toString());
        map.put("remark", etRemark.getText().toString());
        map.put("againVisit", rbYes.isChecked() ? "Yes" : "No");
        map.put("nextVisit", etNextVisit.getText().toString());
        map.put("status", "exited");

        db.collection("Visitors").document(visitorId).update(map)
                .addOnSuccessListener(v -> {
                    Toast.makeText(this, "Exit updated", Toast.LENGTH_SHORT).show();
                    finish();
                });
    }

    // ================= TOKEN =================
    private void generateToken() {
        tvToken.setText("Token: T" + (1000 + new Random().nextInt(9000)));
    }
}

package com.example.meetpoint.activities;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.animation.AnimationUtils;
import android.widget.*;

import androidx.appcompat.app.AppCompatActivity;

import com.example.meetpoint.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;

public class SignupActivity extends AppCompatActivity {

    EditText etFirstName, etLastName, etPhone, etEmail, etAddress, etPassword, etConfirmPassword;
    RadioGroup roleGroup;
    RadioButton rbAdmin, rbSuperAdmin;
    Button btnSignup;
    ImageView ivBack, ivEyePassword, ivEyeConfirm;
    TextView tvStrength;

    FirebaseAuth mAuth;
    FirebaseFirestore db;
    ProgressDialog dialog;

    boolean showPass = false, showConfirm = false;
    AlertDialog rulesDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        init();
        setupEyeButtons();
        setupStrength();
        setupRulesPopup();

        btnSignup.setOnClickListener(v -> validate());
    }

    private void init() {
        etFirstName = findViewById(R.id.etFirstName);
        etLastName = findViewById(R.id.etLastName);
        etPhone = findViewById(R.id.etPhone);
        etEmail = findViewById(R.id.etEmail);
        etAddress = findViewById(R.id.etAddress);
        etPassword = findViewById(R.id.etPassword);
        etConfirmPassword = findViewById(R.id.etConfirmPassword);

        roleGroup = findViewById(R.id.roleGroup);
        rbAdmin = findViewById(R.id.rbAdmin);
        rbSuperAdmin = findViewById(R.id.rbSuperAdmin);

        btnSignup = findViewById(R.id.btnSignup);
        ivBack = findViewById(R.id.ivBack);
        ivEyePassword = findViewById(R.id.ivEyePassword);
        ivEyeConfirm = findViewById(R.id.ivEyeConfirm);
        tvStrength = findViewById(R.id.tvStrength);

        dialog = new ProgressDialog(this);
        dialog.setMessage("Creating account...");
        dialog.setCancelable(false);

        ivBack.setOnClickListener(v -> finish());
    }

    // ================= EYE BUTTON =================
    private void setupEyeButtons() {
        ivEyePassword.setOnClickListener(v -> toggleEye(etPassword, ivEyePassword, true));
        ivEyeConfirm.setOnClickListener(v -> toggleEye(etConfirmPassword, ivEyeConfirm, false));
    }

    private void toggleEye(EditText et, ImageView iv, boolean main) {
        iv.startAnimation(AnimationUtils.loadAnimation(this, R.anim.eye_bounce));
        boolean visible = main ? showPass : showConfirm;

        et.setInputType(visible ? 129 : 1);
        iv.setImageResource(visible ? R.drawable.ic_eye_closed : R.drawable.ic_eye_open);

        if (main) showPass = !showPass;
        else showConfirm = !showConfirm;

        et.setSelection(et.length());
    }

    // ================= PASSWORD STRENGTH =================
    private void setupStrength() {
        etPassword.addTextChangedListener(new TextWatcher() {
            public void beforeTextChanged(CharSequence s, int a, int b, int c) {}
            public void afterTextChanged(Editable s) {}

            public void onTextChanged(CharSequence s, int a, int b, int c) {
                String p = s.toString();
                if (p.length() < 6) {
                    tvStrength.setText("Weak");
                    tvStrength.setTextColor(getColor(android.R.color.holo_red_light));
                } else if (p.matches(".*[A-Z].*") && p.matches(".*[0-9].*") && p.matches(".*[@#$%^&+=!].*")) {
                    tvStrength.setText("Strong");
                    tvStrength.setTextColor(getColor(android.R.color.holo_green_light));
                } else {
                    tvStrength.setText("Medium");
                    tvStrength.setTextColor(getColor(android.R.color.holo_orange_light));
                }
            }
        });
    }

    // ================= PASSWORD RULES POPUP =================
    private void setupRulesPopup() {
        etPassword.setOnFocusChangeListener((v, has) -> {
            if (has) showRules();
            else if (rulesDialog != null) rulesDialog.dismiss();
        });
    }

    private void showRules() {
        if (rulesDialog != null && rulesDialog.isShowing()) return;
        rulesDialog = new AlertDialog.Builder(this)
                .setView(LayoutInflater.from(this).inflate(R.layout.dialog_password_rules, null))
                .create();
        rulesDialog.show();
    }

    private void validate() {
        String phone = etPhone.getText().toString().trim();
        String email = etEmail.getText().toString().trim();

        if (phone.length() != 10) {
            etPhone.setError("Phone must be 10 digits");
            return;
        }
        if (!email.endsWith("@gmail.com")) {
            etEmail.setError("Only @gmail.com allowed");
            return;
        }
        if (!etPassword.getText().toString().equals(etConfirmPassword.getText().toString())) {
            etConfirmPassword.setError("Passwords do not match");
            return;
        }
        if (roleGroup.getCheckedRadioButtonId() == -1) {
            Toast.makeText(this, "Select role", Toast.LENGTH_SHORT).show();
            return;
        }
        if (!isConnected()) {
            Toast.makeText(this, "No internet connection", Toast.LENGTH_SHORT).show();
            return;
        }

        register();
    }

    private boolean isConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo n = cm.getActiveNetworkInfo();
        return n != null && n.isConnected();
    }

    private void register() {
        dialog.show();
        String email = etEmail.getText().toString().trim();
        String pass = etPassword.getText().toString().trim();

        mAuth.createUserWithEmailAndPassword(email, pass)
                .addOnSuccessListener(auth -> {

                    String uid = mAuth.getUid();
                    String role = rbAdmin.isChecked() ? "admin" : "superadmin";

                    HashMap<String, Object> map = new HashMap<>();
                    map.put("uid", uid);
                    map.put("firstName", etFirstName.getText().toString());
                    map.put("lastName", etLastName.getText().toString());
                    map.put("phone", etPhone.getText().toString());
                    map.put("email", email);
                    map.put("address", etAddress.getText().toString());
                    map.put("role", role);
                    map.put("createdAt", System.currentTimeMillis());

                    db.collection("Users").document(uid).set(map)
                            .addOnSuccessListener(u -> {
                                dialog.dismiss();
                                startActivity(new Intent(this, LoginActivity.class));
                                finish();
                            });
                })
                .addOnFailureListener(e -> {
                    dialog.dismiss();
                    Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
                });
    }
}

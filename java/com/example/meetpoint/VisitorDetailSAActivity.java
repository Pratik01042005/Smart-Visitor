package com.example.meetpoint;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.meetpoint.models.VisitorModel;
import com.google.firebase.firestore.FirebaseFirestore;

public class VisitorDetailSAActivity extends AppCompatActivity {

    // ================= UI =================
    ImageView ivBack;
    TextView tvName, tvPhone, tvEmail, tvStatus;

    // ================= FIREBASE =================
    FirebaseFirestore db;
    String visitorId;
    VisitorModel visitor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_visitor_detail_saactivity);

        db = FirebaseFirestore.getInstance();
        visitorId = getIntent().getStringExtra("visitorId");

        initViews();
        setupClicks();
        loadVisitor();
    }

    // ================= INIT =================
    private void initViews() {
        ivBack = findViewById(R.id.ivBack);

        tvName = findViewById(R.id.tvName);
        tvPhone = findViewById(R.id.tvPhone);
        tvEmail = findViewById(R.id.tvEmail);
        tvStatus = findViewById(R.id.tvStatus);
    }

    // ================= CLICKS =================
    private void setupClicks() {
        ivBack.setOnClickListener(v -> finish());
    }

    // ================= LOAD VISITOR =================
    private void loadVisitor() {
        if (visitorId == null) return;

        db.collection("Visitors")
                .document(visitorId)
                .get()
                .addOnSuccessListener(doc -> {

                    if (!doc.exists()) return;

                    visitor = doc.toObject(VisitorModel.class);
                    if (visitor == null) return;

                    visitor.setId(doc.getId());

                    tvName.setText(visitor.getName());
                    tvPhone.setText(visitor.getPhone());
                    tvEmail.setText(visitor.getEmail());
                    tvStatus.setText(visitor.getStatus().toUpperCase());
                });
    }
}

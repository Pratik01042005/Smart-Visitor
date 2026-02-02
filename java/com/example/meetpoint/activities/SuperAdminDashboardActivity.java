package com.example.meetpoint.activities;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.meetpoint.AddVisitorActivity;
import com.example.meetpoint.ProfileSAActivity;
import com.example.meetpoint.R;
import com.example.meetpoint.adapters.VisitorAdapterSA;
import com.example.meetpoint.models.VisitorModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.*;

import java.util.ArrayList;
import java.util.Calendar;

public class SuperAdminDashboardActivity extends AppCompatActivity {

    ImageView imgProfile, btnMenu, btnCalendar, btnAdd;
    EditText etSearch;
    RecyclerView recyclerView;
    TextView tvAdminName;
    FirebaseFirestore db;
    FirebaseAuth auth;
    ArrayList<VisitorModel> allList = new ArrayList<>();
    ArrayList<VisitorModel> filteredList = new ArrayList<>();
    VisitorAdapterSA adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_super_admin_dashboard);

        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();

        initViews();
        setupRecycler();
        loadAdminName();
        loadVisitors();
        setupSearch();
        setupClicks();
    }

    private void initViews() {
        imgProfile = findViewById(R.id.imgProfile);
        btnMenu = findViewById(R.id.btnMenu);
        btnCalendar = findViewById(R.id.btnCalendar);
        btnAdd = findViewById(R.id.btnAddVisitor);
        etSearch = findViewById(R.id.etSearch);
        recyclerView = findViewById(R.id.recyclerVisitors);
        tvAdminName = findViewById(R.id.tvAdminName);
    }

    private void setupRecycler() {
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new VisitorAdapterSA(this, filteredList);
        recyclerView.setAdapter(adapter);
    }

    private void loadAdminName() {
        String uid = auth.getUid();
        if (uid == null) return;

        db.collection("Users").document(uid)
                .get()
                .addOnSuccessListener(snapshot -> {
                    if (snapshot.exists()) {
                        tvAdminName.setText(snapshot.getString("name"));
                    }
                });
    }

    private void loadVisitors() {
        db.collection("Visitors")
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .addSnapshotListener((snapshots, e) -> {
                    if (e != null || snapshots == null) return;

                    allList.clear();
                    for (DocumentSnapshot doc : snapshots) {
                        VisitorModel v = doc.toObject(VisitorModel.class);
                        if (v != null) {
                            v.setId(doc.getId());
                            allList.add(v);
                        }
                    }

                    filteredList.clear();
                    filteredList.addAll(allList);
                    adapter.notifyDataSetChanged();
                });
    }

    private void setupSearch() {
        etSearch.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void afterTextChanged(Editable s) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filteredList.clear();
                for (VisitorModel v : allList) {
                    if (v.getName() != null &&
                            v.getName().toLowerCase().contains(s.toString().toLowerCase())) {
                        filteredList.add(v);
                    }
                }
                adapter.notifyDataSetChanged();
            }
        });
    }

    private void setupClicks() {

        imgProfile.setOnClickListener(v ->
                startActivity(new Intent(this, ProfileSAActivity.class)));

        btnAdd.setOnClickListener(v ->
                startActivity(new Intent(this, AddVisitorActivity.class)));

        btnCalendar.setOnClickListener(v -> openDatePicker());

        btnMenu.setOnClickListener(v -> {
            PopupMenu menu = new PopupMenu(this, v);
            menu.getMenuInflater().inflate(R.menu.popup_menu_superadmin, menu.getMenu());
            menu.setOnMenuItemClickListener(item -> {
                if (item.getItemId() == R.id.menuLogout) {
                    FirebaseAuth.getInstance().signOut();
                    startActivity(new Intent(this, LoginActivity.class));
                    finish();
                }
                return true;
            });
            menu.show();
        });
    }

    private void openDatePicker() {
        Calendar c = Calendar.getInstance();

        DatePickerDialog dialog = new DatePickerDialog(
                this,
                (view, y, m, d) -> {

                    Calendar start = Calendar.getInstance();
                    start.set(y, m, d, 0, 0, 0);

                    Calendar end = Calendar.getInstance();
                    end.set(y, m, d, 23, 59, 59);

                    filterByTimestamp(start.getTimeInMillis(), end.getTimeInMillis());
                },
                c.get(Calendar.YEAR),
                c.get(Calendar.MONTH),
                c.get(Calendar.DAY_OF_MONTH)
        );
        dialog.show();
    }

    private void filterByTimestamp(long start, long end) {

        db.collection("Visitors")
                .whereGreaterThanOrEqualTo("timestamp", start)
                .whereLessThanOrEqualTo("timestamp", end)
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .get()
                .addOnSuccessListener(query -> {

                    filteredList.clear();
                    for (DocumentSnapshot doc : query) {
                        VisitorModel v = doc.toObject(VisitorModel.class);
                        if (v != null) {
                            v.setId(doc.getId());
                            filteredList.add(v);
                        }
                    }
                    adapter.notifyDataSetChanged();
                })
                .addOnFailureListener(e ->
                        Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show());
    }
}

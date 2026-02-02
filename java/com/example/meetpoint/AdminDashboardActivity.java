package com.example.meetpoint;

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

import com.example.meetpoint.activities.LoginActivity;
import com.example.meetpoint.adapters.VisitorAdapter;
import com.example.meetpoint.models.VisitorModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.*;

import java.util.ArrayList;

public class AdminDashboardActivity extends AppCompatActivity {

    ImageView imgProfile, btnCalendar, btnMenu, btnAddVisitor;
    TextView txtWelcome;
    EditText etSearch;
    RecyclerView recyclerVisitors;

    FirebaseAuth mAuth;
    FirebaseFirestore db;

    ArrayList<VisitorModel> visitorList = new ArrayList<>();
    ArrayList<VisitorModel> filteredList = new ArrayList<>();
    VisitorAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_dashboard);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        initViews();
        setupRecycler();
        loadUserName();
        loadVisitors();
        setupSearch();
        setupClicks();
    }

    private void initViews() {
        imgProfile = findViewById(R.id.imgProfile);
        btnCalendar = findViewById(R.id.btnCalendar);
        btnMenu = findViewById(R.id.btnMenu);
        btnAddVisitor = findViewById(R.id.btnAddVisitor);
        txtWelcome = findViewById(R.id.txtWelcome);
        etSearch = findViewById(R.id.etSearch);
        recyclerVisitors = findViewById(R.id.recyclerVisitors);
    }

    private void setupRecycler() {
        recyclerVisitors.setLayoutManager(new LinearLayoutManager(this));
        adapter = new VisitorAdapter(this, filteredList);
        recyclerVisitors.setAdapter(adapter);
    }

    private void loadUserName() {
        if (mAuth.getCurrentUser() == null) return;

        db.collection("Users").document(mAuth.getUid())
                .get()
                .addOnSuccessListener(doc -> {
                    if (doc.exists()) {
                        txtWelcome.setText("Hello, " + doc.getString("firstName"));
                    }
                });
    }

    private void loadVisitors() {
        db.collection("Visitors")
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .addSnapshotListener((snapshots, e) -> {

                    if (e != null || snapshots == null) return;

                    visitorList.clear();

                    for (DocumentSnapshot doc : snapshots) {
                        VisitorModel model = doc.toObject(VisitorModel.class);
                        if (model != null) {
                            model.setId(doc.getId());
                            visitorList.add(model);
                        }
                    }

                    filteredList.clear();
                    filteredList.addAll(visitorList);
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
                for (VisitorModel v : visitorList) {
                    if (v.getName().toLowerCase().contains(s.toString().toLowerCase())) {
                        filteredList.add(v);
                    }
                }
                adapter.notifyDataSetChanged();
            }
        });
    }

    private void setupClicks() {

        if (imgProfile != null)
            imgProfile.setOnClickListener(v ->
                    startActivity(new Intent(this, ProfileActivity.class)));

        if (btnAddVisitor != null)
            btnAddVisitor.setOnClickListener(v ->
                    startActivity(new Intent(this, AddVisitorActivity.class)));

        if (btnCalendar != null)
            btnCalendar.setOnClickListener(v ->
                    Toast.makeText(this, "Calendar coming soon", Toast.LENGTH_SHORT).show());

        if (btnMenu != null)
            btnMenu.setOnClickListener(v -> showMenu());
    }

    private void showMenu() {
        PopupMenu popup = new PopupMenu(this, btnMenu);
        popup.getMenuInflater().inflate(R.menu.popup_menu_admin, popup.getMenu());

        popup.setOnMenuItemClickListener(item -> {
            if (item.getItemId() == R.id.menuLogout) {
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(this, LoginActivity.class));
                finish();
            }
            return true;
        });
        popup.show();
    }
}

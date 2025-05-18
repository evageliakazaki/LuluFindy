package com.example.lulufindy;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast; // Για απλά μηνύματα
import androidx.appcompat.widget.Toolbar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.navigation.NavigationView;

import android.app.AlertDialog;
import android.util.Log;

import android.content.Intent;
import android.widget.Button;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;


public class AdminMainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private ActionBarDrawerToggle toggle;

    private Button searchBtn;

    private Button startBtn,walletbtn;

    FirebaseAuth auth;
    FirebaseUser user;

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_main);

        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        // ✅ Συνδέουμε το Toolbar
        Toolbar toolbar = findViewById(R.id.toolbar_admin);
        setSupportActionBar(toolbar);

        drawerLayout = findViewById(R.id.drawer_layout_admin);
        navigationView = findViewById(R.id.navigation_view_admin);

        // ✅ Δημιουργούμε τον toggle με το toolbar
        toggle = new ActionBarDrawerToggle(
                this, drawerLayout, toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);

        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        navigationView.setNavigationItemSelectedListener(this);

        startBtn=findViewById(R.id.btnAdminButton2);
        startBtn.setOnClickListener(v-> {
            Intent intent = new Intent(AdminMainActivity.this, StartParking.class);
            intent.putExtra("origin", "admin");
            startActivity(intent);
            finish();
        });
        searchBtn=findViewById(R.id.btnSearchParkingAdmin);
        searchBtn.setOnClickListener(v -> showParkingTypeList());

        walletbtn = findViewById(R.id.btnSearchAdmin);
        walletbtn.setOnClickListener(v-> {
            Intent intent = new Intent(AdminMainActivity.this, WalletManagerActivity.class);
            intent.putExtra("origin", "admin");
            startActivity(intent);
            finish();
        });
    }
    private void showParkingTypeList() {
        String[] parkingOptions = {"Κανονική Θέση", "Ηλεκτρική Θέση", "Θέση Αναπήρων"};

        new AlertDialog.Builder(this)
                .setTitle("Επιλέξτε τύπο θέσης στάθμευσης")
                .setItems(parkingOptions, (dialog, which) -> {
                    switch (which) {
                        case 0:
                            startActivity(new Intent(AdminMainActivity.this, ClassicParking.class));
                            break;
                        case 1:
                            startActivity(new Intent(AdminMainActivity.this, ElectricParking.class));
                            break;
                        case 2:
                            startActivity(new Intent(AdminMainActivity.this, DisabledParking.class));
                            break;
                        default:
                            Toast.makeText(this, "Άγνωστη επιλογή", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Άκυρο", null)
                .show();
    }


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (toggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // Χειρισμός των κλικ στα στοιχεία του μενού του admin εδώ
        int id = item.getItemId();

        if (id == R.id.nav_admin_manage_parking) {
            Toast.makeText(this, " Διαχείριση Χώρων Στάθμευσης", Toast.LENGTH_SHORT).show();
            // Πρόσθεσε εδώ την λογική για την επιλογή 1

        } else if (id == R.id.nav_parking_admin) {
            Intent intent = new Intent(getApplicationContext(),StartParking.class);
            startActivity(intent);
            finish();
        } else if (id == R.id.nav_search_admin) {
            showParkingTypeList();
        } else if (id == R.id.nav_wallet_admin) {
            Intent intent = new Intent(getApplicationContext(),WalletManagerActivity.class);
            startActivity(intent);
            finish();
        } else if (id == R.id.nav_logout_admin) {
            FirebaseAuth.getInstance().signOut();
            Intent intent = new Intent(getApplicationContext(),Sing_In.class);
            startActivity(intent);
            finish();

        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout_admin);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }
}
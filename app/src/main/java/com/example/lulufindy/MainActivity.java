package com.example.lulufindy;


import android.app.AlertDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import com.google.android.material.navigation.NavigationView;
import android.content.Intent;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;


public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle toggle;
    private Button searchBtn;
    private Button startBtn;
    private Button btnCharts , btnWallet;

    FirebaseAuth auth;
    FirebaseUser user;

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private TextView Walletbalance;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ImageButton btnCalendar = findViewById(R.id.btnCalendar);
        btnCalendar.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, Calendar.class);
            startActivity(intent);
        });

        ImageButton btnProfile = findViewById(R.id.btnProfile);
        btnProfile.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, ProfileActivity.class);
            startActivity(intent);
        });


        drawerLayout = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.navigation_view);

        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        Walletbalance = findViewById(R.id.tvWalletAmount);

        // Ρύθμιση του ActionBarDrawerToggle για το άνοιγμα/κλείσιμο του μενού
        toggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        // Εμφάνιση του κουμπιού του μενού στην ActionBar
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        // Ορισμός του listener για τις επιλογές του μενού
        navigationView.setNavigationItemSelectedListener(this);

        startBtn=findViewById(R.id.btnStartParking);
        startBtn.setOnClickListener(v-> {
            Intent intent = new Intent(MainActivity.this, StartParking.class);
            intent.putExtra("origin", "start");
            startActivity(intent);
            finish();
        });
        searchBtn=findViewById(R.id.btnSearch);
        searchBtn.setOnClickListener(v -> showParkingTypeList());

        btnCharts = findViewById(R.id.btnCharts);
        btnCharts.setOnClickListener(v-> {
            Intent intent = new Intent(MainActivity.this, Charts.class);
            startActivity(intent);
        });

        btnWallet = findViewById(R.id.btnWallet);
        btnWallet.setOnClickListener(v-> {
            Intent intent = new Intent(MainActivity.this, WalletManagerActivity.class);
            intent.putExtra("origin", "start");
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
                            startActivity(new Intent(MainActivity.this, ClassicParking.class));
                            break;
                        case 1:
                            startActivity(new Intent(MainActivity.this, ElectricParking.class));
                            break;
                        case 2:
                            startActivity(new Intent(MainActivity.this, DisabledParking.class));
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

        int id = item.getItemId();

        if (id == R.id.nav_parking){
            Intent intent = new Intent(MainActivity.this, StartParking.class);
            intent.putExtra("origin", "start");
            startActivity(intent);
            finish();
        }

        else if (id == R.id.nav_wallet) {
            Intent intent = new Intent(MainActivity.this, WalletManagerActivity.class);
            intent.putExtra("origin", "start");
            startActivity(intent);
            finish();
        }

        else if (id == R.id.nav_charts) {
            Intent intent = new Intent(getApplicationContext(), Charts.class);
            startActivity(intent);
        }
        else if (id == R.id.nav_search) {
             showParkingTypeList();

        } else if (id == R.id.nav_logout) {
            FirebaseAuth.getInstance().signOut();
            Intent intent = new Intent(getApplicationContext(),Sing_In.class);
            startActivity(intent);
            finish();
        }

        // Κλείσιμο του πλάγιου μενού μετά την επιλογή
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    // Μέθοδος για να χειριστεί το πάτημα του "back" κουμπιού όταν το μενού είναι ανοιχτό
    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadWalletBalance();
    }

    private void loadWalletBalance() {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            FirebaseFirestore.getInstance().collection("wallets")
                    .document(currentUser.getUid())
                    .get()
                    .addOnSuccessListener(documentSnapshot -> {
                        if (documentSnapshot.exists()) {
                            Double balance = documentSnapshot.getDouble("balance");
                            if (balance != null) {
                                Walletbalance.setText(String.format("%.2f €", balance));
                            } else {
                                Walletbalance.setText("0.00 €");
                            }
                        } else {
                            Walletbalance.setText("0.00 €");
                        }
                    })
                    .addOnFailureListener(e -> {
                        Log.e("MainActivity", "Failed to load wallet balance", e);
                    });
        }
    }


}
package com.example.lulufindy;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle toggle;
    private Button searchBtn;

    private FirebaseAuth mAuth;
    private FirebaseUser user;

    private FirebaseAuth auth;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        drawerLayout = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.navigation_view);
        searchBtn = findViewById(R.id.btnSearch);

        mAuth = FirebaseAuth.getInstance();

        // Ρύθμιση του μενού
        toggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        navigationView.setNavigationItemSelectedListener(this);

        // Κουμπί αναζήτησης
        searchBtn.setOnClickListener(v -> showParkingTypeList());
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
         // Χειρισμός των επιλογών του μενού εδώpackage com.example.lulufindy;
        //
        //import androidx.core.app.ComponentActivity;
        //import androidx.fragment.app.FragmentActivity;
        //
        //import android.os.Bundle;
        //import android.view.MenuItem;
        //import androidx.annotation.NonNull;
        //import androidx.appcompat.app.ActionBarDrawerToggle;
        //import androidx.appcompat.app.AppCompatActivity;
        //import androidx.core.view.GravityCompat;
        //import androidx.drawerlayout.widget.DrawerLayout;
        //import com.google.android.material.navigation.NavigationView;
        //import android.content.Intent;
        //import com.google.firebase.auth.FirebaseAuth;
        //import com.google.firebase.auth.FirebaseUser;
        //
        //
        //public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
        //
        //    private DrawerLayout drawerLayout;
        //    private ActionBarDrawerToggle toggle;
        //
        //    FirebaseAuth auth;
        //    FirebaseUser user;
        //
        //    @Override
        //    protected void onCreate(Bundle savedInstanceState) {
        //        super.onCreate(savedInstanceState);
        //        setContentView(R.layout.activity_main);
        //
        //        drawerLayout = findViewById(R.id.drawer_layout);
        //        NavigationView navigationView = findViewById(R.id.navigation_view);
        //
        //        auth = FirebaseAuth.getInstance();
        //        user = auth.getCurrentUser();
        //
        //        // Ρύθμιση του ActionBarDrawerToggle για το άνοιγμα/κλείσιμο του μενού
        //        toggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        //        drawerLayout.addDrawerListener(toggle);
        //        toggle.syncState();
        //
        //        // Εμφάνιση του κουμπιού του μενού στην ActionBar
        //        if (getSupportActionBar() != null) {
        //            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        //        }
        //
        //        // Ορισμός του listener για τις επιλογές του μενού
        //        navigationView.setNavigationItemSelectedListener(this);
        //    }
        //
        //    @Override
        //    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        //        if (toggle.onOptionsItemSelected(item)) {
        //            return true;
        //        }
        //        return super.onOptionsItemSelected(item);
        //    }
        //
        //    @Override
        //    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        //        // Χειρισμός των επιλογών του μενού εδώ
        //        int id = item.getItemId();
        //
        //        /*if (id == R.id.nav_start_parking) {
        //            // Κώδικας για την επιλογή "Έναρξη Στάθμευσης"
        //            // Θα προσθέσουμε λειτουργικότητα σε επόμενο βήμα
        //        } else if (id == R.id.nav_end_parking) {
        //            // Κώδικας για την επιλογή "Ολοκλήρωση / Πληρωμή"
        //            // Θα προσθέσουμε λειτουργικότητα σε επόμενο βήμα
        //        } else if (id == R.id.nav_search) {
        //            // Κώδικας για την επιλογή "Αναζήτηση"
        //            // Θα προσθέσουμε λειτουργικότητα σε επόμενο βήμα
        //        } else if (id == R.id.nav_wallet) {
        //            // Κώδικας για την επιλογή "Πορτοφόλι"
        //            // Θα προσθέσουμε λειτουργικότητα σε επόμενο βήμα*/
        //        if (id == R.id.nav_logout) {
        //            FirebaseAuth.getInstance().signOut();
        //            Intent intent =new Intent(getApplicationContext(),Sing_In.class);
        //            startActivity(intent);
        //            finish();
        //        }
        //
        //        // Κλείσιμο του πλάγιου μενού μετά την επιλογή
        //        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        //        drawer.closeDrawer(GravityCompat.START);
        //        return true;
        //    }
        //
        //    // Μέθοδος για να χειριστεί το πάτημα του "back" κουμπιού όταν το μενού είναι ανοιχτό
        //    @Override
        //    public void onBackPressed() {
        //        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
        //            drawerLayout.closeDrawer(GravityCompat.START);
        //        } else {
        //            super.onBackPressed();
        //        }
        //    }
        int id = item.getItemId();

        /*if (id == R.id.nav_start_parking) {
            // Κώδικας για την επιλογή "Έναρξη Στάθμευσης"
            // Θα προσθέσουμε λειτουργικότητα σε επόμενο βήμα
        } else if (id == R.id.nav_end_parking) {
            // Κώδικας για την επιλογή "Ολοκλήρωση / Πληρωμή"
            // Θα προσθέσουμε λειτουργικότητα σε επόμενο βήμα
            } else if (id == R.id.nav_wallet) {
            // Κώδικας για την επιλογή "Πορτοφόλι"
            // Θα προσθέσουμε λειτουργικότητα σε επόμενο βήμα*/

        if (id == R.id.nav_search) {
            showParkingTypeList();
        } else if (id == R.id.nav_logout) {
            FirebaseAuth.getInstance().signOut();
            startActivity(new Intent(getApplicationContext(), Sing_In.class));
            finish();
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawerLayout.closeDrawer(GravityCompat.START);
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
}

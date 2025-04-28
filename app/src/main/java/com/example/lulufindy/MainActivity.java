package com.example.lulufindy;


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
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;


public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle toggle;
    private Button searchBtn;

    FirebaseAuth auth;
    FirebaseUser user;

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        drawerLayout = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.navigation_view);

        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();


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

        searchBtn = findViewById(R.id.btnSearch);
        searchBtn.setOnClickListener(v -> {
            FirebaseUser currentUser = mAuth.getCurrentUser();
            if (currentUser != null) {
                // Αν υπάρχει χρήστης, παίρνουμε τα δεδομένα του από τη Firestore
                String userId = currentUser.getUid();
                db.collection("users").document(userId).get()
                        .addOnSuccessListener(documentSnapshot -> {
                            if (documentSnapshot.exists()) {
                                // Παίρνουμε την τιμή του parking_type από το Firestore
                                String parkingType = documentSnapshot.getString("parking_type");

                                // Ελέγχουμε και εκτυπώνουμε την τιμή του parking_type
                                Log.d("Firestore", "Parking Type: " + parkingType);

                                if (parkingType != null) {
                                    // Αν η τιμή του parkingType είναι σωστή, προχωράμε στην αντίστοιχη Activity
                                    switch (parkingType) {
                                        case "Ηλεκτρική Θέση":
                                            startActivity(new Intent(MainActivity.this, ElectricParking.class));
                                            break;
                                        case "Θέση Αναπήρων":
                                            startActivity(new Intent(MainActivity.this, DisabledParking.class));
                                            break;
                                        case "Κανονική Θέση":
                                            startActivity(new Intent(MainActivity.this, ClassicParking.class));
                                            break;
                                        default:
                                            Toast.makeText(MainActivity.this, "Άγνωστος τύπος θέσης", Toast.LENGTH_SHORT).show();
                                    }
                                } else {
                                    Log.d("Firestore", "Parking Type is null or not available");
                                }
                            } else {
                                Log.d("Firestore", "Document does not exist");
                            }
                        })
                        .addOnFailureListener(e -> {
                            Log.d("Firestore", "Error getting document", e);
                            Toast.makeText(MainActivity.this, "Σφάλμα κατά την λήψη δεδομένων.", Toast.LENGTH_SHORT).show();
                        });
            } else {
                // Αν δεν υπάρχει χρήστης, κάνουμε log out και γυρνάμε στο SignIn
                Toast.makeText(MainActivity.this, "Δεν είστε συνδεδεμένοι", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(MainActivity.this, Sing_In.class);
                startActivity(intent);
                finish();
            }
        });
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
            Intent intent = new Intent(getApplicationContext(),ClassicParking.class);
            startActivity(intent);
            finish();

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
}
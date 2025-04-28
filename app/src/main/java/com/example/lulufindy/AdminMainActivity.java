package com.example.lulufindy;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast; // Για απλά μηνύματα

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.navigation.NavigationView;

public class AdminMainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private ActionBarDrawerToggle toggle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_main);

        drawerLayout = findViewById(R.id.drawer_layout_admin);
        navigationView = findViewById(R.id.navigation_view_admin);

        toggle = new ActionBarDrawerToggle(
                this, drawerLayout, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        navigationView.setNavigationItemSelectedListener(this);
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
            Toast.makeText(this, "Στάθμευση", Toast.LENGTH_SHORT).show();
            // Πρόσθεσε εδώ την λογική για την στάθμευση (admin)
        } else if (id == R.id.nav_payment_admin) {
            Toast.makeText(this, "Πληρωμή", Toast.LENGTH_SHORT).show();
            // Πρόσθεσε εδώ την λογική για την πληρωμή (admin)
        } else if (id == R.id.nav_search_admin) {
            Toast.makeText(this, "Αναζήτηση", Toast.LENGTH_SHORT).show();
            // Πρόσθεσε εδώ την λογική για την αναζήτηση (admin)
        } else if (id == R.id.nav_wallet_admin) {
            Toast.makeText(this, "Πορτοφόλι", Toast.LENGTH_SHORT).show();
            // Πρόσθεσε εδώ την λογική για το πορτοφόλι (admin)
        } else if (id == R.id.nav_logout_admin) {
            Toast.makeText(this, "Αποσύνδεση", Toast.LENGTH_SHORT).show();

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
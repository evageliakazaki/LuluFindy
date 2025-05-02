package com.example.lulufindy;


import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;

import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

import android.annotation.SuppressLint;
import android.os.Looper;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.os.Handler;
import android.widget.Toast;


import android.view.MenuItem;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import com.google.android.material.navigation.NavigationView;
import android.content.Intent;



public class StartParking extends AppCompatActivity {

    private AutoCompleteTextView parkingTypeView, parkingTypeInput;
    private TextInputEditText carPlateInput;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    FirebaseAuth auth;
    FirebaseUser user;

    private EditText vehicleNumberEditText;
    private Button startButton, areabtn,backBtn;
    private TextView timerTextView, costTextView;

    private ParkingSession currentSession;
    private final double costPerMinute = 0.05;

    private final Handler handler = new Handler(Looper.getMainLooper());

    private final Runnable updateTimerRunnable = new Runnable() {
        @Override
        public void run() {
            if (currentSession != null && currentSession.isActive()) {
                timerTextView.setText("Χρόνος: " + currentSession.getFormattedElapsedTime());
                double cost = currentSession.calculateCost(costPerMinute);
                costTextView.setText(String.format("Χρέωση: %.2f€", cost));
                handler.postDelayed(this, 1000);
            }
        }
    };



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_parking);
        parkingTypeView = findViewById(R.id.parking_type);
        db = FirebaseFirestore.getInstance();
        startButton = findViewById(R.id.startButton);
        timerTextView = findViewById(R.id.timerTextView);
        costTextView = findViewById(R.id.costTextView);
        areabtn=findViewById(R.id.areaCodeButton);
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        carPlateInput = findViewById(R.id.car_license_plate);
        parkingTypeInput = findViewById(R.id.parking_type);

        backBtn = findViewById(R.id.back2);
        backBtn.setOnClickListener(v -> {
            Intent intent = new Intent(StartParking.this, MainActivity.class);
            startActivity(intent);
            finish();
        });


        startButton.setOnClickListener(v -> {
            String vehicleNumber = carPlateInput.getText().toString().trim();

            if (vehicleNumber.isEmpty()) {
                Toast.makeText(StartParking.this, "Συμπλήρωσε όλα τα πεδία", Toast.LENGTH_SHORT).show();
                return;
            }

            long startTimeMillis = System.currentTimeMillis();
            currentSession = new ParkingSession( vehicleNumber, startTimeMillis);

            handler.post(updateTimerRunnable);

            Toast.makeText(StartParking.this, "Η στάθμευση ξεκίνησε!", Toast.LENGTH_SHORT).show();
        });

        String[] parkingTypes = {"Ηλεκτρική Θέση", "Θέση Αναπήρων", "Κανονική Θέση"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_dropdown_item_1line,
                parkingTypes
        );

        parkingTypeView.setAdapter(adapter);
        parkingTypeView.setOnClickListener(v -> {
            parkingTypeView.showDropDown();  // αναγκαστική εμφάνιση της λίστας
        });




        areabtn.setOnClickListener(v -> {
            FirebaseUser currentUser = user;
            String carPlate = carPlateInput.getText().toString().trim();
            String parkingType = parkingTypeInput.getText().toString().trim();

            if (currentUser != null) {
                String userId = currentUser.getUid(); // Το ID του χρήστη στο Authentication

                Map<String, Object> update = new HashMap<>();
                update.put("car_plate", carPlate);
                update.put("parking_type", parkingType);

                db.collection("users").document(userId).update(update)
                        .addOnSuccessListener(aVoid -> {
                            // Επιτυχία
                        })
                        .addOnFailureListener(e -> {
                            // Αποτυχία
                        });
            }
            if (currentUser != null) {
                // Αν υπάρχει χρήστης, παίρνουμε τα δεδομένα του από τη Firestore
                String userId = currentUser.getUid();
                db.collection("users").document(userId).get()
                        .addOnSuccessListener(documentSnapshot -> {
                            if (documentSnapshot.exists()) {
                                // Παίρνουμε την τιμή του parking_type από το Firestore
                                //String parkingType = documentSnapshot.getString("parking_type");

                                // Ελέγχουμε και εκτυπώνουμε την τιμή του parking_type
                                Log.d("Firestore", "Parking Type: " + parkingType);

                                if (parkingType != null) {
                                    // Αν η τιμή του parkingType είναι σωστή, προχωράμε στην αντίστοιχη Activity
                                    switch (parkingType) {
                                        case "Ηλεκτρική Θέση":
                                            startActivity(new Intent(StartParking.this, ElectricParking.class));
                                            break;
                                        case "Θέση Αναπήρων":
                                            startActivity(new Intent(StartParking.this, DisabledParking.class));
                                            break;
                                        case "Κανονική Θέση":
                                            startActivity(new Intent(StartParking.this, ClassicParking.class));
                                            break;
                                        default:
                                            Toast.makeText(StartParking.this, "Άγνωστος τύπος θέσης", Toast.LENGTH_SHORT).show();
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
                            Toast.makeText(StartParking.this, "Σφάλμα κατά την λήψη δεδομένων.", Toast.LENGTH_SHORT).show();
                        });
            } else {
                // Αν δεν υπάρχει χρήστης, κάνουμε log out και γυρνάμε στο SignIn
                Toast.makeText(StartParking.this, "Δεν είστε συνδεδεμένοι", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(StartParking.this, Sing_In.class);
                startActivity(intent);
                finish();
            }
        });
    }

    private void clearInputs() {
        carPlateInput.setText("");
        parkingTypeInput.setText("");
    }
}
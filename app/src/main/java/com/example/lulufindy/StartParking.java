package com.example.lulufindy;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;

import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.HashMap;
import java.util.Map;

import android.os.Looper;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.os.Handler;
import android.widget.Toast;
import android.app.AlertDialog;

import androidx.annotation.NonNull;

public class StartParking extends AppCompatActivity {

    private AutoCompleteTextView parkingTypeView, parkingTypeInput;
    private TextInputEditText carPlateInput;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    FirebaseAuth auth;
    FirebaseUser user;

    private EditText vehicleNumberEditText;
    private Button startButton, areabtn, backBtn, stopBtn;
    private TextView timerTextView, costTextView;

    private ParkingSession currentSession;
    private final double costPerMinute = 0.05;

    private final Handler handler = new Handler(Looper.getMainLooper());

    private final Runnable updateTimerRunnable = new Runnable() {
        @Override
        public void run() {
            if (currentSession != null && currentSession.isActive()) {
                timerTextView.setText("Χρόνος: " + currentSession.getFormattedElapsedTime());
                double runningCost = currentSession.calculateCost(costPerMinute);
                double totalCost = 0.50 + runningCost;
                costTextView.setText(String.format("Χρέωση: %.2f€", totalCost));
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
        areabtn = findViewById(R.id.areaCodeButton);
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        carPlateInput = findViewById(R.id.car_license_plate);
        parkingTypeInput = findViewById(R.id.parking_type);

        backBtn = findViewById(R.id.back2);
        backBtn.setOnClickListener(v -> {
            String origin = getIntent().getStringExtra("origin");
            Intent intent;
            if ("main".equals(origin)) {
                intent = new Intent(StartParking.this, MainActivity.class);
            } else {
                intent = new Intent(StartParking.this, AdminMainActivity.class);
            }
            startActivity(intent);
            finish();
        });

        TextView parkingNameTextView = findViewById(R.id.parking_name);
        final String[] parkingNameHolder = new String[1];
        parkingNameHolder[0] = getIntent().getStringExtra("parking_name");

        if (parkingNameHolder[0] != null && !parkingNameHolder[0].isEmpty()) {
            parkingNameTextView.setVisibility(View.VISIBLE);
            parkingNameTextView.setText("Επιλεγμένη θέση: " + parkingNameHolder[0]);
        } else {
            parkingNameTextView.setVisibility(View.GONE);
        }

        if (savedInstanceState != null) {
            String savedPlate = savedInstanceState.getString("car_plate");
            String savedType = savedInstanceState.getString("parking_type");
            if (savedPlate != null) carPlateInput.setText(savedPlate);
            if (savedType != null) parkingTypeInput.setText(savedType);
        }

        startButton.setOnClickListener(v -> {
            String vehicleNumber = carPlateInput.getText().toString().trim();
            if (vehicleNumber.isEmpty()) {
                Toast.makeText(StartParking.this, "Συμπλήρωσε όλα τα πεδία", Toast.LENGTH_SHORT).show();
                return;
            }
            long startTimeMillis = System.currentTimeMillis();
            currentSession = new ParkingSession(vehicleNumber, startTimeMillis);
            handler.post(updateTimerRunnable);
            Toast.makeText(StartParking.this, "Η στάθμευση ξεκίνησε!", Toast.LENGTH_SHORT).show();
        });

        stopBtn = findViewById(R.id.stopButton);
        stopBtn.setOnClickListener(v -> {
            if (currentSession != null && currentSession.isActive()) {
                new AlertDialog.Builder(StartParking.this)
                        .setTitle("Ολοκλήρωση Στάθμευσης")
                        .setMessage("Τελειώσατε τη διαδρομή σας;")
                        .setPositiveButton("Ναι", (dialog, which) -> {
                            currentSession.stopSession();
                            handler.removeCallbacks(updateTimerRunnable);

                            final String parkingName = parkingNameTextView.getText().toString().replace("Επιλεγμένη θέση: ", "");
                            final String parkingType = parkingTypeInput.getText().toString().trim();


                            if (!parkingName.isEmpty() && !parkingType.isEmpty()) {
                                String collectionName = "";
                                switch (parkingType) {
                                    case "Κανονική Θέση":
                                        collectionName = "ClassicParkings";
                                        break;
                                    case "Ηλεκτρική Θέση":
                                        collectionName = "ElectricParkings";
                                        break;
                                    case "Θέση Αναπήρων":
                                        collectionName = "DisabledParkings";
                                        break;
                                    default:
                                        Toast.makeText(StartParking.this, "Άγνωστος τύπος θέσης.", Toast.LENGTH_SHORT).show();
                                        return;
                                }

                                String finalCollectionName = collectionName;
                                db.collection(collectionName)
                                        .whereEqualTo("name", parkingName)
                                        .get()
                                        .addOnSuccessListener(queryDocumentSnapshots -> {
                                            if (!queryDocumentSnapshots.isEmpty()) {
                                                for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                                                    db.collection(finalCollectionName).document(doc.getId())
                                                            .update("taken", false)
                                                            .addOnSuccessListener(aVoid -> Log.d("Firestore", "Parking freed"))
                                                            .addOnFailureListener(e -> Log.e("Firestore", "Error freeing parking", e));
                                                }
                                            } else {
                                                Log.e("Firestore", "No document found with name: " + parkingName);
                                            }
                                        })
                                        .addOnFailureListener(e -> Log.e("Firestore", "Failed to find parking document", e));
                            }

                            Toast.makeText(StartParking.this, "Η στάθμευση τερματίστηκε!", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(StartParking.this, Payment.class);
                            startActivity(intent);
                        })
                        .setNegativeButton("Όχι", null)
                        .show();
            } else {
                Toast.makeText(StartParking.this, "Δεν υπάρχει ενεργή στάθμευση.", Toast.LENGTH_SHORT).show();
            }
        });



        String[] parkingTypes = {"Ηλεκτρική Θέση", "Θέση Αναπήρων", "Κανονική Θέση"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_dropdown_item_1line,
                parkingTypes
        );
        parkingTypeView.setAdapter(adapter);
        parkingTypeView.setOnClickListener(v -> parkingTypeView.showDropDown());

        areabtn.setOnClickListener(v -> {
            FirebaseUser currentUser = user;
            String carPlate = carPlateInput.getText().toString().trim();
            String parkingType = parkingTypeInput.getText().toString().trim();

            if (currentUser != null) {
                String userId = currentUser.getUid();

                Map<String, Object> update = new HashMap<>();
                update.put("car_plate", carPlate);
                update.put("parking_type", parkingType);

                db.collection("users").document(userId).update(update)
                        .addOnSuccessListener(aVoid -> {})
                        .addOnFailureListener(e -> {});

                db.collection("users").document(userId).get()
                        .addOnSuccessListener(documentSnapshot -> {
                            if (documentSnapshot.exists()) {
                                Log.d("Firestore", "Parking Type: " + parkingType);
                                if (parkingType != null) {
                                    switch (parkingType) {
                                        case "Ηλεκτρική Θέση":
                                            Intent intent = new Intent(StartParking.this, StartElectricParking.class);
                                            intent.putExtra("origin", "start");
                                            startActivityForResult(intent, 1);
                                            break;
                                        case "Θέση Αναπήρων":
                                            Intent intent1 = new Intent(StartParking.this, StartDisabledParking.class);
                                            intent1.putExtra("origin", "start");
                                            startActivityForResult(intent1, 1);
                                            break;
                                        case "Κανονική Θέση":
                                            Intent intent2 = new Intent(StartParking.this, StartClassicParking.class);
                                            intent2.putExtra("origin", "start");
                                            startActivityForResult(intent2, 1);
                                            break;
                                        default:
                                            Toast.makeText(StartParking.this, "Δώσε Αριθμό Κυκλοφορίας και Τύπο Parking", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            }
                        })
                        .addOnFailureListener(e -> {
                            Log.d("Firestore", "Error getting document", e);
                            Toast.makeText(StartParking.this, "Σφάλμα κατά την λήψη δεδομένων.", Toast.LENGTH_SHORT).show();
                        });
            } else {
                Toast.makeText(StartParking.this, "Δεν είστε συνδεδεμένοι", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(StartParking.this, Sing_In.class);
                startActivity(intent);
                finish();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && data != null) {
            String from = data.getStringExtra("return_from");
            if (from != null && (from.equals("startclassic") || from.equals("disabled") || from.equals("electric"))) {
                String parkingName = data.getStringExtra("parking_name");
                if (parkingName != null) {
                    TextView parkingNameTextView = findViewById(R.id.parking_name);
                    parkingNameTextView.setText("Επιλεγμένη θέση: " + parkingName);
                    parkingNameTextView.setVisibility(View.VISIBLE);
                }
                TextView warningTextView = findViewById(R.id.warning);
                warningTextView.setVisibility(View.VISIBLE);
                return;
            }
        }
        clearInputs();
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("car_plate", carPlateInput.getText().toString());
        outState.putString("parking_type", parkingTypeInput.getText().toString());
    }

    private void clearInputs() {
        carPlateInput.setText("");
        parkingTypeInput.setText("");
    }
}

package com.example.lulufindy;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;

public class ElectricParking extends AppCompatActivity {

    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;

    private MapView map;
    private FusedLocationProviderClient fusedLocationClient;
    private Marker userMarker;
    private ProgressDialog progressDialog;
    private Button backBtn;
    private LocationCallback locationCallback;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Configuration.getInstance().load(getApplicationContext(),
                PreferenceManager.getDefaultSharedPreferences(getApplicationContext()));
        setContentView(R.layout.activity_electric_parking);

        map = findViewById(R.id.map);
        map.setTileSource(TileSourceFactory.MAPNIK);
        map.getTileProvider().getTileRequestCompleteHandlers().clear(); // Καθαρίζει τυχόν παλιά handlers


        backBtn = findViewById(R.id.back);
        backBtn.setOnClickListener(v -> {
            Intent intent = new Intent(ElectricParking.this, MainActivity.class);
            startActivity(intent);
            finish();
        });

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Φόρτωση τοποθεσίας...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    LOCATION_PERMISSION_REQUEST_CODE);
        } else {
            getLocation();
        }
    }

    private void getLocation() {
        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(10000);
        locationRequest.setFastestInterval(5000);

        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(@NonNull LocationResult locationResult) {
                if (locationResult == null) {
                    Toast.makeText(ElectricParking.this, "Δεν βρέθηκε τοποθεσία.", Toast.LENGTH_SHORT).show();
                    return;
                }

                android.location.Location location = locationResult.getLastLocation();
                if (location != null) {
                    if (progressDialog != null && progressDialog.isShowing()) {
                        progressDialog.dismiss();
                    }
                    updateUserLocation(location.getLatitude(), location.getLongitude());
                    fusedLocationClient.removeLocationUpdates(locationCallback);
                }
            }
        };

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, getMainLooper());
        }
    }

    private void updateUserLocation(double latitude, double longitude) {
        GeoPoint userLocation = new GeoPoint(latitude, longitude);

        // Ασφαλές centering με redraw
        map.post(() -> {
            map.getController().setZoom(15.0);
            map.getController().setCenter(userLocation);
            map.invalidate();
        });

        if (userMarker == null) {
            userMarker = new Marker(map);
            userMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
            userMarker.setTitle("Βρίσκεσαι εδώ!");
            userMarker.setIcon(ResourcesCompat.getDrawable(getResources(), org.osmdroid.library.R.drawable.marker_default, null));
            map.getOverlays().add(userMarker);
        }
        userMarker.setPosition(userLocation);

        loadParkingSpots();
    }

    private void loadParkingSpots() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("ElectricParkings")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            com.google.firebase.firestore.GeoPoint location = document.getGeoPoint("pin");
                            if (location != null) {
                                addParkingMarker(location.getLatitude(), location.getLongitude());
                            }
                        }
                    } else {
                        Toast.makeText(ElectricParking.this, "Σφάλμα κατά την ανάκτηση θέσεων.", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(ElectricParking.this, "Απέτυχε η σύνδεση με τη βάση.", Toast.LENGTH_SHORT).show();
                });
    }

    private void addParkingMarker(double latitude, double longitude) {
        GeoPoint parkingLocation = new GeoPoint(latitude, longitude);
        Marker parkingMarker = new Marker(map);
        parkingMarker.setPosition(parkingLocation);
        parkingMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
        parkingMarker.setIcon(ResourcesCompat.getDrawable(getResources(), R.drawable.electric_car, null));
        parkingMarker.setTitle("Σταθμός φόρτισης");

        parkingMarker.setOnMarkerClickListener((marker, mapView) -> {
            if (userMarker != null) {
                openGoogleMaps(userMarker.getPosition(), parkingLocation);
            }
            return true;
        });

        map.getOverlays().add(parkingMarker);
    }

    private void openGoogleMaps(GeoPoint from, GeoPoint to) {
        String uri = "https://www.google.com/maps/dir/?api=1&origin=" + from.getLatitude() + "," + from.getLongitude()
                + "&destination=" + to.getLatitude() + "," + to.getLongitude() + "&travelmode=driving";

        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
        intent.setPackage("com.google.android.apps.maps");

        try {
            startActivity(intent);
        } catch (Exception e) {
            Toast.makeText(this, "Δεν βρέθηκε το Google Maps.", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getLocation();
            } else {
                Toast.makeText(this, "Η άδεια τοποθεσίας είναι απαραίτητη.", Toast.LENGTH_SHORT).show();
                if (progressDialog != null && progressDialog.isShowing()) {
                    progressDialog.dismiss();
                }
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    protected void onResume() {
        super.onResume();
        map.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        map.onPause();
    }
}
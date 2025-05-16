package com.example.lulufindy;

import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.HashMap;

public class Charts extends AppCompatActivity {

    private RecyclerView recyclerView;
    private TextView emptyView;
    private ProgressBar loading;
    private TextView averageDurationView;
    private TextView averageClassic, averageElectric, averageDisabled;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_charts);

        recyclerView = findViewById(R.id.reservation_list);
        averageDurationView = findViewById(R.id.average_duration);
        /*averageClassic = findViewById(R.id.average_duration);
        averageElectric = findViewById(R.id.average_electric);
        averageDisabled = findViewById(R.id.average_disabled);*/

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        fetchHistory();
    }

    private void fetchHistory() {
        loading.setVisibility(View.VISIBLE);
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        FirebaseFirestore.getInstance().collection("users")
                .document(userId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    loading.setVisibility(View.GONE);
                    if (documentSnapshot.exists() && documentSnapshot.contains("reservedHistory")) {
                        List<Map<String, Object>> history = (List<Map<String, Object>>) documentSnapshot.get("reservedHistory");

                        if (history != null && !history.isEmpty()) {
                            List<String> lines = new ArrayList<>();

                            long totalDuration = 0;
                            int totalCount = 0;

                            long classicDuration = 0, electricDuration = 0, disabledDuration = 0;
                            int classicCount = 0, electricCount = 0, disabledCount = 0;

                            for (Map<String, Object> item : history) {
                                String name = (String) item.get("parkingName");
                                String type = (String) item.get("parkingType");
                                long timestamp = (long) item.get("timestamp");
                                Long endTimestamp = (Long) item.get("endTimestamp");

                                String date = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()).format(new Date(timestamp));
                                lines.add(name + " (" + type + ")\n" + date);

                                if (endTimestamp != null) {
                                    long duration = endTimestamp - timestamp;
                                    totalDuration += duration;
                                    totalCount++;

                                    switch (type.toLowerCase()) {
                                        case "classic":
                                            classicDuration += duration;
                                            classicCount++;
                                            break;
                                        case "electric":
                                            electricDuration += duration;
                                            electricCount++;
                                            break;
                                        case "disabled":
                                            disabledDuration += duration;
                                            disabledCount++;
                                            break;
                                    }
                                }
                            }

                            if (totalCount > 0) {
                                averageDurationView.setText("Μέση διάρκεια: " + formatMillis(totalDuration / totalCount));
                                averageDurationView.setVisibility(View.VISIBLE);
                            }

                            if (classicCount > 0)
                                averageClassic.setText("Classic: " + formatMillis(classicDuration / classicCount));
                            else
                                averageClassic.setText("Classic: καμία εγγραφή");

                            if (electricCount > 0)
                                averageElectric.setText("Electric: " + formatMillis(electricDuration / electricCount));
                            else
                                averageElectric.setText("Electric: καμία εγγραφή");

                            if (disabledCount > 0)
                                averageDisabled.setText("Disabled: " + formatMillis(disabledDuration / disabledCount));
                            else
                                averageDisabled.setText("Disabled: καμία εγγραφή");

                            /*recyclerView.setAdapter(new ReservationListAdapter(lines));*/
                            recyclerView.setVisibility(View.VISIBLE);
                            emptyView.setVisibility(View.GONE);
                        } else {
                            showEmpty();
                        }
                    } else {
                        showEmpty();
                    }
                })
                .addOnFailureListener(e -> {
                    loading.setVisibility(View.GONE);
                    showEmpty();
                });
    }

    private void showEmpty() {
        recyclerView.setVisibility(View.GONE);
        emptyView.setVisibility(View.VISIBLE);
        averageDurationView.setVisibility(View.GONE);
        averageClassic.setText("Classic: -");
        averageElectric.setText("Electric: -");
        averageDisabled.setText("Disabled: -");
    }

    private String formatMillis(long millis) {
        long seconds = (millis / 1000) % 60;
        long minutes = (millis / (1000 * 60)) % 60;
        long hours = millis / (1000 * 60 * 60);
        return String.format(Locale.getDefault(), "%02d:%02d:%02d", hours, minutes, seconds);
    }
}

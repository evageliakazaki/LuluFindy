package com.example.lulufindy;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.util.HashMap;
import java.util.Map;

public class Payment extends AppCompatActivity {

    private CardView applePayCard, googlePayCard, cardCard, walletCard;
    private TextView durationText, chargeText;
    private View payButton;
    private String selectedMethod = null;
    private double totalAmount = 0.00;
    private int initialBackgroundColor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);

        // Initialize Views
        durationText = findViewById(R.id.duration_text);
        chargeText = findViewById(R.id.charge_text);

        applePayCard = findViewById(R.id.apple_pay_card);
        googlePayCard = findViewById(R.id.google_pay_card);
        cardCard = findViewById(R.id.card_card);
        walletCard = findViewById(R.id.wallet_card);
        payButton = findViewById(R.id.payment_btn);

        initialBackgroundColor = Color.parseColor("#FFF5A2BE");

        // Get intent extras
        Intent intent = getIntent();
        String totalTime = intent.getStringExtra("total_time");
        totalAmount = intent.getDoubleExtra("total_cost", 0.00);

        // Display values
        durationText.setText("Χρόνος: " + totalTime);
        chargeText.setText(String.format("Κόστος: %.2f€", totalAmount));

        // Card click listeners
        googlePayCard.setOnClickListener(v -> {
            selectedMethod = "Google Pay";
            highlightSelected(googlePayCard);
        });

        applePayCard.setOnClickListener(v -> {
            selectedMethod = "Apple Pay";
            highlightSelected(applePayCard);
        });

        cardCard.setOnClickListener(v -> {
            selectedMethod = "Κάρτα";
            highlightSelected(cardCard);
        });

        walletCard.setOnClickListener(v -> {
            selectedMethod = "Πορτοφόλι";
            highlightSelected(walletCard);
        });

        // Payment button action
        payButton.setOnClickListener(v -> {
            if (selectedMethod == null) {
                Toast.makeText(this, "Επίλεξε τρόπο πληρωμής", Toast.LENGTH_SHORT).show();
                return;
            }

            Intent intent1 = null;

            switch (selectedMethod) {
                case "Google Pay":
                    intent1 = new Intent(this, GooglePayActivity.class);
                    break;
                case "Apple Pay":
                    intent1 = new Intent(this, ApplePayActivity.class);
                    break;
                case "Κάρτα":
                    intent1 = new Intent(this, CardPaymentActivity.class);
                    break;
                case "Πορτοφόλι":
                    intent1 = new Intent(this, WalletPayActivity.class);
                    break;
            }

            // Save only for supported payment methods
            if (!selectedMethod.equals("Πορτοφόλι")) {
                savePaymentToHistory(selectedMethod, totalAmount);
            }

            if (intent1 != null) {
                intent1.putExtra("amount", totalAmount);
                startActivity(intent1);
            }
        });
    }

    private void highlightSelected(CardView selectedCard) {
        applePayCard.setCardBackgroundColor(initialBackgroundColor);
        googlePayCard.setCardBackgroundColor(initialBackgroundColor);
        cardCard.setCardBackgroundColor(initialBackgroundColor);
        walletCard.setCardBackgroundColor(initialBackgroundColor);

        selectedCard.setCardBackgroundColor(Color.parseColor("#FFEF6E99"));
    }

    private void savePaymentToHistory(String method, double amount) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) return;

        String userId = user.getUid();

        // Extract clean duration string (remove "Χρόνος: ")
        String duration = durationText.getText().toString().replace("Χρόνος: ", "").trim();

        Map<String, Object> paymentEntry = new HashMap<>();
        paymentEntry.put("method", method);
        paymentEntry.put("amount", amount);
        paymentEntry.put("duration", duration);
        paymentEntry.put("timestamp", System.currentTimeMillis());

        Map<String, Object> update = new HashMap<>();
        update.put("paymentHistory", FieldValue.arrayUnion(paymentEntry));

        FirebaseFirestore.getInstance()
                .collection("users")
                .document(userId)
                .set(update, SetOptions.merge());
    }
}

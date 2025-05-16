package com.example.lulufindy;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

public class Payment extends AppCompatActivity {

    private CardView applePayCard, googlePayCard, cardCard, walletCard;
    private TextView totalAmountText;
    private View payButton;
    private String selectedMethod = null;
    private double totalAmount = 25.00;
    private int initialBackgroundColor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);

        applePayCard = findViewById(R.id.apple_pay_card);
        googlePayCard = findViewById(R.id.google_pay_card);
        cardCard = findViewById(R.id.card_card);
        walletCard = findViewById(R.id.wallet_card);
        payButton = findViewById(R.id.payment_btn);
        totalAmountText = findViewById(R.id.total_amount_text);

        initialBackgroundColor = Color.parseColor("#FFF5A2BE");
        totalAmountText.setText(String.format("%.2f", totalAmount) + "€");

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

        payButton.setOnClickListener(v -> {
            if (selectedMethod == null) {
                Toast.makeText(this, "Επίλεξε τρόπο πληρωμής", Toast.LENGTH_SHORT).show();
                return;
            }

            Intent intent = null;

            switch (selectedMethod) {
                case "Google Pay":
                    intent = new Intent(this, GooglePayActivity.class);
                    break;
                case "Apple Pay":
                    intent = new Intent(this, ApplePayActivity.class);
                    break;
                case "Κάρτα":
                    intent = new Intent(this, CardPaymentActivity.class);

                    break;
                case "Πορτοφόλι":
                    intent = new Intent(this, WalletPayActivity.class);
                    break;
            }

            if (intent != null) {
                intent.putExtra("amount", totalAmount);
                startActivity(intent);
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
}

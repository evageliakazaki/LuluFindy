package com.example.lulufindy;

public class WalletManager {

    private static double walletBalance = 0.0;

    public static void addToWallet(double amount) {
        walletBalance += amount;
    }

    public static boolean deductFromWallet(double amount) {
        if (amount <= walletBalance) {
            walletBalance -= amount;
            return true;
        } else {
            return false;
        }
    }

    public static double getWalletBalance() {
        return walletBalance;
    }

    public static void setWalletBalance(double amount) {
        walletBalance = amount;
    }
}

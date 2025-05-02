package com.example.lulufindy;

public class ParkingSession {

    private String vehicleNumber;
    private long startTimeMillis;
    private boolean active;

    public ParkingSession( String vehicleNumber, long startTimeMillis) {

        this.vehicleNumber = vehicleNumber;
        this.startTimeMillis = startTimeMillis;
        this.active = true;
    }

    public boolean isActive() {
        return active;
    }

    public void endSession() {
        active = false;
    }

    public String getFormattedElapsedTime() {
        long elapsedMillis = System.currentTimeMillis() - startTimeMillis;
        long seconds = (elapsedMillis / 1000) % 60;
        long minutes = (elapsedMillis / (1000 * 60)) % 60;
        long hours = (elapsedMillis / (1000 * 60 * 60));
        return String.format("%02d:%02d:%02d", hours, minutes, seconds);
    }

    public double calculateCost(double costPerMinute) {
        long elapsedMillis = System.currentTimeMillis() - startTimeMillis;
        long totalMinutes = elapsedMillis / (1000 * 60);
        return totalMinutes * costPerMinute;
    }
}




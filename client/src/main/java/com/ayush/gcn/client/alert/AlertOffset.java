package com.ayush.gcn.client.alert;

public record AlertOffset(int minutesBefore) {

    public String message() {
        return minutesBefore == 0 ? "Starting now" : minutesBefore + " minutes";
    }
}

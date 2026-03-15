package com.ayush.gcn.dto;

public record AlertOffset(int minutesBefore) {

    public String message() {
        return minutesBefore == 0 ? "Starting now" : minutesBefore + " minutes";
    }
}

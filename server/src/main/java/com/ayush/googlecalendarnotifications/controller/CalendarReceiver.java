package com.ayush.googlecalendarnotifications.controller;

import com.ayush.googlecalendarnotifications.dto.SyncRequest;
import com.ayush.googlecalendarnotifications.service.AlertStorage;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class CalendarReceiver {

    private final AlertStorage storage;

    public CalendarReceiver(AlertStorage storage) {
        this.storage = storage;
    }

    @PostMapping("/sync")
    public ResponseEntity<String> sync(@RequestBody SyncRequest request) {
        storage.rebuildSchedule(request);
        return ResponseEntity.ok("Synced " + request.getMeetings().size() + " meetings for key: " + request.getKey());
    }
}

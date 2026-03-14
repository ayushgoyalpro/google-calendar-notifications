package com.ayush.googlecalendarnotifications.controller;

import com.ayush.googlecalendarnotifications.dto.Alert;
import com.ayush.googlecalendarnotifications.dto.AlertOffset;
import com.ayush.googlecalendarnotifications.service.AlertDispatcher;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class Tester {
    private final AlertDispatcher alertDispatcher;

    public Tester(AlertDispatcher alertDispatcher) {this.alertDispatcher = alertDispatcher;}

    @PostMapping("/test/trigger")
    public String trigger(@RequestParam String key) {
        alertDispatcher.wsNotify(new Alert(key, "Test Alert", new AlertOffset(0)));
        return "Triggered alert dispatch!";
    }
}

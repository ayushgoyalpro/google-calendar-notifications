package com.ayush.gcn.server.controller;

import com.ayush.gcn.server.dto.Alert;
import com.ayush.gcn.server.dto.AlertOffset;
import com.ayush.gcn.server.dto.Channel;
import com.ayush.gcn.server.dto.Meeting;
import com.ayush.gcn.server.service.AlertDispatcher;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.EnumSet;

@RestController
@RequestMapping("/tester")
public class Tester {

    private final AlertDispatcher alertDispatcher;

    public Tester(AlertDispatcher alertDispatcher) {
        this.alertDispatcher = alertDispatcher;
    }

    @PostMapping("/trigger")
    public String trigger(@RequestBody TriggerRequest request) {
        alertDispatcher.fire(new Alert(request.key(),
                                       request.meeting(),
                                       new AlertOffset(request.offset),
                                       EnumSet.of(request.channel())));
        return "Triggered " + request.channel() + " for: " + request.meeting().getTitle();
    }

    public record TriggerRequest(String key, Meeting meeting, Channel channel, Integer offset) {
    }
}

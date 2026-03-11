package com.ayush.googlecalendarnotifications.service;

import com.ayush.googlecalendarnotifications.dto.Alert;
import com.ayush.googlecalendarnotifications.dto.AlertType;
import com.ayush.googlecalendarnotifications.dto.Meeting;
import com.ayush.googlecalendarnotifications.dto.SyncRequest;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class AlertStorage {
    // Outer key: user key, Inner key: epoch minute, Value: alerts for that minute
    private final Map<String, Map<Long, List<Alert>>> schedule = new ConcurrentHashMap<>();

    public void rebuildSchedule(SyncRequest request) {
        String key = request.getKey();
        Map<Long, List<Alert>> userSchedule = new ConcurrentHashMap<>();
        for (Meeting meeting : request.getMeetings()) {
            Instant start = Instant.parse(meeting.getStartTime());
            createAlert(key, userSchedule, meeting, start.minus(15, ChronoUnit.MINUTES), AlertType.FIFTEEN_MINUTES_BEFORE);
            createAlert(key, userSchedule, meeting, start.minus(5, ChronoUnit.MINUTES), AlertType.FIVE_MINUTES_BEFORE);
            createAlert(key, userSchedule, meeting, start, AlertType.STARTING_NOW);
        }
        schedule.put(key, userSchedule);
    }

    private void createAlert(String key, Map<Long, List<Alert>> userSchedule, Meeting meeting, Instant alertTime, AlertType type) {
        if (alertTime.isAfter(Instant.now())) {
            long minuteEpoch = alertTime.truncatedTo(ChronoUnit.MINUTES).getEpochSecond() / 60;
            userSchedule.computeIfAbsent(minuteEpoch, k -> Collections.synchronizedList(new ArrayList<>()))
                .add(new Alert(key, meeting.getTitle(), type));
        }
    }

    public List<Alert> getAlertsForMinute(long minuteEpoch) {
        return schedule.values().stream()
            .flatMap(userSchedule -> {
                List<Alert> alerts = userSchedule.getOrDefault(minuteEpoch, Collections.emptyList());
                return alerts.stream();
            })
            .toList();
    }
}

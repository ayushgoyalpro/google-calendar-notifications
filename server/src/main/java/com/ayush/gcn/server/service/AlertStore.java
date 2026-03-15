package com.ayush.gcn.server.service;

import com.ayush.gcn.server.dto.Alert;
import com.ayush.gcn.server.dto.AlertOffset;
import com.ayush.gcn.server.dto.Meeting;
import com.ayush.gcn.server.dto.SyncRequest;
import com.ayush.gcn.server.dto.UserPreferences;
import com.ayush.gcn.server.dto.UserSchedule;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

@Component
@RequiredArgsConstructor
public class AlertStore {

    private final ConcurrentHashMap<String, UserSchedule> schedules = new ConcurrentHashMap<>();
    private final UserPreferenceStore preferenceStore;

    public void rebuildSchedule(SyncRequest request) {
        String key = request.getKey();
        UserPreferences prefs = preferenceStore.get(key);
        UserSchedule schedule = new UserSchedule();

        for (Meeting meeting : request.getMeetings()) {
            Instant start = Instant.parse(meeting.getStartTime());
            for (int offsetMinutes : prefs.getAlertOffsetMinutes()) {
                Instant alertTime = start.minus(offsetMinutes, ChronoUnit.MINUTES);
                if (alertTime.isAfter(Instant.now())) {
                    long minuteEpoch = alertTime.truncatedTo(ChronoUnit.MINUTES).getEpochSecond() / 60;
                    schedule.add(minuteEpoch, new Alert(key, meeting, new AlertOffset(offsetMinutes), prefs.getChannelsForOffset(offsetMinutes)));
                }
            }
        }

        schedules.put(key, schedule);
    }

    public List<Alert> getAlertsForMinute(long minuteEpoch) {
        return schedules.values().stream()
                .flatMap(schedule -> schedule.getAlertsForMinute(minuteEpoch).stream())
                .toList();
    }
}

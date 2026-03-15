package com.ayush.gcn.server.dto;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Holds the scheduled alerts for a single user, keyed by epoch-minute.
 */
public class UserSchedule {

    private final Map<Long, List<Alert>> byMinute = new ConcurrentHashMap<>();

    public void add(long minuteEpoch, Alert alert) {
        byMinute.computeIfAbsent(minuteEpoch, k -> Collections.synchronizedList(new ArrayList<>())).add(alert);
    }

    public List<Alert> getAlertsForMinute(long minuteEpoch) {
        return byMinute.getOrDefault(minuteEpoch, Collections.emptyList());
    }
}

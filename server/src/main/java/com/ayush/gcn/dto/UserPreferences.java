package com.ayush.gcn.dto;

import java.util.EnumSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

public class UserPreferences {

    /**
    Key: minutes before event. Value: channels to fire on that offset.
    Default: 15m and 5m on WebSocket only; 0m on all channels.
    */
    private final Map<Integer, Set<Channel>> schedule;

    public UserPreferences() {
        schedule = new LinkedHashMap<>();
        schedule.put(15, EnumSet.of(Channel.WEBSOCKET));
        schedule.put(5,  EnumSet.of(Channel.WEBSOCKET));
        schedule.put(0,  EnumSet.allOf(Channel.class));
    }

    public UserPreferences(Map<Integer, Set<Channel>> schedule) {
        this.schedule = new LinkedHashMap<>();
        schedule.forEach((offset, channels) ->
                this.schedule.put(offset, EnumSet.copyOf(channels)));
    }

    /** The offsets (minutes before) that have at least one channel configured. */
    public Set<Integer> getAlertOffsetMinutes() {
        return schedule.keySet();
    }

    /** The channels to fire for a given offset. Empty set if the offset is not configured. */
    public Set<Channel> getChannelsForOffset(int minutesBefore) {
        return schedule.getOrDefault(minutesBefore, EnumSet.noneOf(Channel.class));
    }
}

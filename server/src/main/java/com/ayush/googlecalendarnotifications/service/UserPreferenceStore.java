package com.ayush.googlecalendarnotifications.service;

import com.ayush.googlecalendarnotifications.dto.UserPreferences;
import org.springframework.stereotype.Component;

import java.util.concurrent.ConcurrentHashMap;

@Component
public class UserPreferenceStore {

    private final ConcurrentHashMap<String, UserPreferences> store = new ConcurrentHashMap<>();

    /** Returns the user's preferences, creating a default entry if none exists yet. */
    public UserPreferences get(String userKey) {
        return store.computeIfAbsent(userKey, k -> new UserPreferences());
    }

    public void put(String userKey, UserPreferences preferences) {
        store.put(userKey, preferences);
    }
}

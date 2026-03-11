package com.ayush.googlecalendarnotifications.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
public class SyncRequest {
    private String key;
    private List<Meeting> meetings;
}

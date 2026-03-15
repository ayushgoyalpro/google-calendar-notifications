package com.ayush.gcn.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Set;

@Data
@AllArgsConstructor
public class Alert {
    private String key;
    private Meeting meeting;
    private AlertOffset offset;
    private Set<Channel> channels;
}

package com.ayush.gcn.service;

import com.ayush.gcn.dto.Alert;
import com.ayush.gcn.dto.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Set;

@Slf4j
@Service
@EnableScheduling
public class AlertDispatcher {

    private final AlertStore storage;
    private final SimpMessagingTemplate messagingTemplate;

    public AlertDispatcher(AlertStore storage, SimpMessagingTemplate messagingTemplate) {
        this.storage = storage;
        this.messagingTemplate = messagingTemplate;
    }

    @Scheduled(cron = "0 * * * * *")
    public void dispatch() {
        long currentMinuteEpoch = Instant.now().truncatedTo(ChronoUnit.MINUTES).getEpochSecond() / 60;
        List<Alert> alerts = storage.getAlertsForMinute(currentMinuteEpoch);
        alerts.forEach(alert -> {
            log.info("[{}] {}m before: {}",
                     Instant.now(),
                     alert.getOffset().minutesBefore(),
                     alert.getMeeting().getTitle());
            fire(alert);
        });
    }

    public void fire(Alert alert) {
        Set<Channel> channels = alert.getChannels();
        if (channels.contains(Channel.NTFY)) ntfy(alert);
        if (channels.contains(Channel.WEBSOCKET)) wsNotify(alert);
    }

    private void wsNotify(Alert alert) {
        messagingTemplate.convertAndSend("/topic/" + alert.getKey(), alert.getMeeting());
    }

    private void ntfy(Alert alert) {
        String message = alert.getMeeting().getTitle() + " - " + alert.getOffset().message();
        try (HttpClient client = HttpClient.newHttpClient()) {
            HttpRequest request = HttpRequest.newBuilder()
                                             .uri(URI.create("https://ntfy.sh/" + alert.getKey()))
                                             .POST(HttpRequest.BodyPublishers.ofString(message))
                                             .header("Title", "Calendar Alert")
                                             .header("Priority", "5")
                                             .header("Tags", "calendar,bell")
                                             .build();
            client.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (Exception e) {
            log.error("Failed to send notification: {}", e.getMessage(), e);
        }
    }
}

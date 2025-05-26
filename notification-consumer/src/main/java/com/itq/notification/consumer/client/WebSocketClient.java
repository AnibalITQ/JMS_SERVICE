package com.itq.notification.consumer.client;

import org.springframework.beans.factory.annotation.Value; // Changed from model to util.model
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import com.itq.notification.util.model.NotificationMessage;

@Component
public class WebSocketClient {

    @Value("${websocket.service.url}")
    private String websocketServiceUrl; // e.g. http://localhost:8083/api/notify

    private final RestTemplate restTemplate;

    public WebSocketClient() {
        this.restTemplate = new RestTemplate();
    }

    public void sendNotification(NotificationMessage message) {
        String url = websocketServiceUrl + "/api/notify";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<NotificationMessage> request = new HttpEntity<>(message, headers);

        try {
            ResponseEntity<Void> response = restTemplate.postForEntity(url, request, Void.class);

            if (!response.getStatusCode().is2xxSuccessful()) {
                System.err.println("Error al enviar notificación al WebSocket: " + response.getStatusCode());
            }
        } catch (Exception e) {
            System.err.println("Excepción al llamar al WebSocket Service: " + e.getMessage());
        }
    }
}

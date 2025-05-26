package com.itq.notification.consumer.client;

import com.itq.notification.model.NotificationMessage;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

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

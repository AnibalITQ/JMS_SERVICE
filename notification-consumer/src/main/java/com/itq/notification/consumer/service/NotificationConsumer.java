package com.itq.notification.consumer.service;

import org.slf4j.Logger; // Changed from model to util.model
import org.slf4j.LoggerFactory; // Fixed import
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.itq.notification.util.model.NotificationMessage;

import jakarta.jms.Message;
import jakarta.jms.TextMessage;

@Service
public class NotificationConsumer {

    private static final Logger logger = LoggerFactory.getLogger(NotificationConsumer.class);

    private ObjectMapper objectMapper;
    private final RestTemplate restTemplate = new RestTemplate();

    public NotificationConsumer(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }
    @Value("${websocket.service-url:http://localhost:8083}")
    private String websocketServiceUrl;

    @JmsListener(destination = "${jms.queue.notification.in:notification.queue.in}")
    public void processNotificationIn(Message message) {
        processMessage(message, "IN");
    }

    @JmsListener(destination = "${jms.queue.notification.out:notification.queue.out}")
    public void processNotificationOut(Message message) {
        processMessage(message, "OUT");
    }

    @JmsListener(destination = "${jms.queue.priority:notification.queue.priority}")
    public void processPriorityNotification(Message message) {
        processMessage(message, "PRIORITY");
    }

    @JmsListener(destination = "${jms.queue.guard:notification.queue.guard}")
    public void processGuardNotification(Message message) {
        processMessage(message, "GUARD");
    }

    private void processMessage(Message message, String queueType) {
        try {
            if (message instanceof TextMessage) {
                TextMessage textMessage = (TextMessage) message;
                String jsonContent = textMessage.getText();

                NotificationMessage notification = objectMapper.readValue(jsonContent, NotificationMessage.class);
                String userId = notification.getToUserId();
                System.out.println("UserId destino: " + userId);

                logger.info("Procesando mensaje de cola {}: {} - Prioridad: {}",
                        queueType, notification.getMessageId(), notification.getPriority());

                while(notification.getAttemptsToSend() > 0) {
                    // Distribuir via WebSocket
                    String url = websocketServiceUrl + "/api/notify";
                    HttpEntity<NotificationMessage> request = new HttpEntity<>(notification);
                    ResponseEntity<Void> response = restTemplate.postForEntity(url, request, Void.class);
                    logger.info("Mensaje enviado al websocket service con status: {}", response.getStatusCode());
                    logger.info("Enviando mensaje: {}", notification.getMessageId());
                    if (!response.getStatusCode().is2xxSuccessful()) {
                        logger.error("Error al enviar mensaje al WebSocket: {}", response.getStatusCode());
                        notification.setAttemptsToSend(notification.getAttemptsToSend() - 1);
                    } else {
                        logger.info("Mensaje enviado exitosamente: {}", notification.getMessageId());
                        break; // Salir del bucle si el envío fue exitoso
                    }
                }
            }
        } catch (Exception e) {
            logger.error("Error procesando mensaje de cola {}: {}", queueType, e.getMessage(), e);
        }
    }
}

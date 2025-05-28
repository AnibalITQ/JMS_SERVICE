package com.itq.notification.consumer.service;

import org.slf4j.Logger; // Changed from model to util.model
import org.slf4j.LoggerFactory; // Fixed import
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.jms.core.JmsTemplate;
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
    private final JmsTemplate jmsTemplate;

    public NotificationConsumer(ObjectMapper objectMapper, JmsTemplate jmsTemplate) {
        this.objectMapper = objectMapper;
        this.jmsTemplate = jmsTemplate;
    }
    @Value("${websocket.service-url:http://localhost:8083}")
    private String websocketServiceUrl;

    @JmsListener(destination = "${jms.queue.notification.in:notification.queue.in}")
    public void processNotificationIn(Message message) {
        processMessage(message, "IN");
    }

    @JmsListener(destination = "${jms.queue.priority:notification.queue.priority}")
    public void processPriorityNotification(Message message) {
        processMessage(message, "PRIORITY");
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

                // Consultar si el usuario está activo antes de enviar la notificación
                String isActiveUrl = websocketServiceUrl + "/api/notify/isActive/" + userId;
                Boolean isActive = false;
                try {
                    ResponseEntity<Boolean> activeResponse = restTemplate.getForEntity(isActiveUrl, Boolean.class);
                    isActive = Boolean.TRUE.equals(activeResponse.getBody());
                } catch (Exception ex) {
                    logger.warn("No se pudo consultar el estado de conexión del usuario {}: {}", userId, ex.getMessage());
                }

                if (isActive) {
                    // Distribuir via WebSocket solo si el usuario está activo
                    String url = websocketServiceUrl + "/api/notify";
                    HttpEntity<NotificationMessage> request = new HttpEntity<>(notification);
                    ResponseEntity<Void> response = restTemplate.postForEntity(url, request, Void.class);

                    logger.info("Mensaje enviado al websocket service con status: {}", response.getStatusCode());
                } else {
                    logger.info("Usuario {} no está activo. Reenviando mensaje al final de la cola.", userId);
                    // Reenviar el mensaje a la misma cola
                    String queueName = "IN".equals(queueType)
                            ? "${jms.queue.notification.in:notification.queue.in}"
                            : "${jms.queue.priority:notification.queue.priority}";
                    jmsTemplate.convertAndSend(queueName, jsonContent);
                }
            }
        } catch (Exception e) {
            logger.error("Error procesando mensaje de cola {}: {}", queueType, e.getMessage(), e);
        }
    }
}

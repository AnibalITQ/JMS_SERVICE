package com.itq.notification.consumer.service;

import com.itq.notification.util.model.NotificationMessage; // Changed from model to util.model
import com.itq.notification.websocket.service.WebSocketNotificationService; // Fixed import
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jakarta.jms.Message;
import jakarta.jms.TextMessage;

@Service
public class NotificationConsumer {
    
    private static final Logger logger = LoggerFactory.getLogger(NotificationConsumer.class);
    
    @Autowired
    private ObjectMapper objectMapper;
    
    @Autowired
    private WebSocketNotificationService webSocketService;
    
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
                
                logger.info("Procesando mensaje de cola {}: {} - Prioridad: {}", 
                           queueType, notification.getMessageId(), notification.getPriority());
                
                // Distribuir via WebSocket
                webSocketService.distributeNotification(notification, queueType);
                
            }
        } catch (Exception e) {
            logger.error("Error procesando mensaje de cola {}: {}", queueType, e.getMessage(), e);
        }
    }
}

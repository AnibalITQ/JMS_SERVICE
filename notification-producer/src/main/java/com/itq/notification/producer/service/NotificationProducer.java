package com.itq.notification.producer.service;

import com.itq.notification.util.model.NotificationMessage;
import com.itq.notification.util.config.JMSConfig;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jms.Message;
import java.util.HashMap;
import java.util.Map;

@Service
public class NotificationProducer {
    
    private static final Logger logger = LoggerFactory.getLogger(NotificationProducer.class);
    
    @Autowired
    private JmsTemplate jmsTemplate;
    
    @Autowired
    private ObjectMapper objectMapper;
    
    public void sendNotification(NotificationMessage notification) {
        try {
            String destination = determineDestination(notification);
            
            jmsTemplate.send(destination, session -> {
                Message message = session.createTextMessage(objectMapper.writeValueAsString(notification));
                
                // Headers personalizados
                message.setStringProperty("messageId", notification.getMessageId());
                message.setStringProperty("messageType", notification.getMessageType().name());
                message.setStringProperty("parkingLotId", notification.getParkingLotId());
                message.setStringProperty("category", notification.getCategory().name());
                message.setIntProperty("priority", notification.getPriority());
                message.setBooleanProperty("anonymous", notification.isAnonymous());
                
                // Propiedades del usuario
                if (notification.getToUserId() != null) {
                    message.setStringProperty("targetUserId", notification.getToUserId());
                }
                if (notification.getRecipientRole() != null) {
                    message.setStringProperty("userRole", notification.getRecipientRole().name());
                }
                
                // Establecer prioridad JMS (0-9, donde 9 es la más alta)
                message.setJMSPriority(Math.min(notification.getPriority() + 4, 9));
                
                return message;
            });
            
            logger.info("Mensaje enviado a {}: {}", destination, notification.getMessageId());
            
        } catch (Exception e) {
            logger.error("Error enviando mensaje: {}", e.getMessage(), e);
            throw new RuntimeException("Error enviando notificación", e);
        }
    }
    
    private String determineDestination(NotificationMessage notification) {
        switch (notification.getMessageType()) {
            case REPORT_TO_GUARD:
                return JMSConfig.GUARD_QUEUE;
            case PRIORITY_ALERT:
                return JMSConfig.PRIORITY_QUEUE;
            case REPORT_TO_USER:
            case SYSTEM_NOTIFICATION:
            default:
                return notification.getPriority() > 3 ? 
                       JMSConfig.PRIORITY_QUEUE : 
                       JMSConfig.NOTIFICATION_QUEUE_IN;
        }
    }
}
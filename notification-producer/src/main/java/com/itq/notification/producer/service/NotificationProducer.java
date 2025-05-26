package com.itq.notification.producer.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.itq.notification.util.model.NotificationMessage;

import jakarta.jms.TextMessage;

@Service
public class NotificationProducer {
    private static final Logger logger = LoggerFactory.getLogger(NotificationProducer.class);

    @Autowired
    private JmsTemplate jmsTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    public void sendNotification(NotificationMessage notification) {
        try {
            String json = objectMapper.writeValueAsString(notification);
            
            jmsTemplate.send(session -> {
                TextMessage message = session.createTextMessage(json);
                message.setStringProperty("messageType", notification.getMessageType().name());
                return message;
            });

            logger.info("Sent notification: {}", notification.getMessageId());
        } catch (Exception e) {
            logger.error("Error sending notification: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to send notification", e);
        }
    }
}
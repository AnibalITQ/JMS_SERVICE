package com.itq.notification.websocket.service;

import org.springframework.stereotype.Service;

import com.itq.notification.util.model.NotificationMessage;
import com.itq.notification.websocket.handler.NotificationWebSocketHandler;

@Service
public class WebSocketNotificationService {

    private final NotificationWebSocketHandler webSocketHandler;

    public WebSocketNotificationService(NotificationWebSocketHandler handler) {
        this.webSocketHandler = handler;
    }


    public void sendToUser(String userId, NotificationMessage notification) {
        webSocketHandler.sendNotificationToUser(userId, notification);
    }
}

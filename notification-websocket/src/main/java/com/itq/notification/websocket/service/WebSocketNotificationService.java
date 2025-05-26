package com.itq.notification.websocket.service;

import com.itq.notification.util.model.NotificationMessage;
import com.itq.notification.websocket.handler.NotificationWebSocketHandler;
import org.springframework.stereotype.Service;

@Service
public class WebSocketNotificationService {

    private final NotificationWebSocketHandler webSocketHandler;

    public WebSocketNotificationService(NotificationWebSocketHandler handler) {
        this.webSocketHandler = handler;
    }

    public void broadcast(NotificationMessage notification) {
        webSocketHandler.sendNotification(notification);
    }
}

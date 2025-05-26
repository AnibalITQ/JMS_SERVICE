package com.itq.notification.websocket.controller;

import com.itq.notification.util.model.NotificationMessage;
import com.itq.notification.websocket.service.WebSocketNotificationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/notify")
public class WebSocketController {

    private final WebSocketNotificationService webSocketNotificationService;

    public WebSocketController(WebSocketNotificationService service) {
        this.webSocketNotificationService = service;
    }

    @PostMapping
    public ResponseEntity<Void> sendNotification(@RequestBody NotificationMessage message) {
        webSocketNotificationService.broadcast(message);
        return ResponseEntity.ok().build();
    }
}

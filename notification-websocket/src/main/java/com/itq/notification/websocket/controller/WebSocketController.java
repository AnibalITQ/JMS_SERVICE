package com.itq.notification.websocket.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.itq.notification.util.model.NotificationMessage;
import com.itq.notification.websocket.service.WebSocketNotificationService;

@RestController
@RequestMapping("/api/notify")
public class WebSocketController {

    private final WebSocketNotificationService webSocketNotificationService;

    public WebSocketController(WebSocketNotificationService service) {
        this.webSocketNotificationService = service;
    }

    @PostMapping
    public ResponseEntity<Void> sendNotification(@RequestBody NotificationMessage message) {
        webSocketNotificationService.sendToUser(message.getToUserId(), message);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/isActive/{userId}")
    public ResponseEntity<Boolean> isUserActive(@PathVariable String userId) {
        boolean isActive = webSocketNotificationService.isUserActive(userId);
        return ResponseEntity.ok(isActive);
    }

}

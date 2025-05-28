package com.itq.notification.websocket.handler;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.WebSocketMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.util.UriTemplate;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.itq.notification.util.model.NotificationMessage;

@Component
public class NotificationWebSocketHandler implements WebSocketHandler {

    private final Map<String, WebSocketSession> userSessions = new ConcurrentHashMap<>();
    private final ObjectMapper objectMapper;

    @Autowired
    public NotificationWebSocketHandler(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
        this.objectMapper.registerModule(new JavaTimeModule());
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        // Extrae el userId del path
        String path = session.getUri().getPath();
        String userId = new UriTemplate("/ws/notifications/{userId}").match(path).get("userId");
        userSessions.put(userId, session);
    }

    @Override
    public void handleMessage(WebSocketSession session, WebSocketMessage<?> message) throws Exception {
        // No se espera recibir mensajes del cliente, se puede dejar vacío
    }

    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {
        userSessions.values().remove(session);
        session.close(CloseStatus.SERVER_ERROR);
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus closeStatus) throws Exception {
        // Remueve la sesión del usuario
        userSessions.values().remove(session);
    }

    @Override
    public boolean supportsPartialMessages() {
        return false;
    }

    public void sendNotificationToUser(String userId, NotificationMessage notification) {
        // Enviar al usuario individual
        WebSocketSession session = userSessions.get(userId);
        if (session != null && session.isOpen()) {
            try {
                String payload = objectMapper.writeValueAsString(notification);
                session.sendMessage(new TextMessage(payload));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        // Enviar también a la sesión universal "guardia" si existe y no es el mismo usuario
        if (!"guardia".equals(userId)) {
            WebSocketSession guardSession = userSessions.get("guardia");
            if (guardSession != null && guardSession.isOpen()) {
                try {
                    String payload = objectMapper.writeValueAsString(notification);
                    guardSession.sendMessage(new TextMessage(payload));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public Boolean isUserActive(String userId)
    {
        return userSessions.containsKey(userId) && userSessions.get(userId).isOpen();
    }
}

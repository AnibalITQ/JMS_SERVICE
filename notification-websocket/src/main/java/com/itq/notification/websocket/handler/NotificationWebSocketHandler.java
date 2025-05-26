package com.itq.notification.websocket.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.itq.notification.model.NotificationMessage;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.*;

import java.io.IOException;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

@Component
public class NotificationWebSocketHandler implements WebSocketHandler {

    private final Set<WebSocketSession> sessions = Collections.synchronizedSet(new HashSet<>());
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        sessions.add(session);
    }

    @Override
    public void handleMessage(WebSocketSession session, WebSocketMessage<?> message) throws Exception {
        // No se espera recibir mensajes del cliente, se puede dejar vacío
    }

    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {
        sessions.remove(session);
        session.close(CloseStatus.SERVER_ERROR);
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus closeStatus) throws Exception {
        sessions.remove(session);
    }

    @Override
    public boolean supportsPartialMessages() {
        return false;
    }

    public void sendNotification(NotificationMessage notification) {
        String payload;
        try {
            payload = objectMapper.writeValueAsString(notification);
        } catch (IOException e) {
            throw new RuntimeException("Error serializando notificación", e);
        }

        synchronized (sessions) {
            for (WebSocketSession session : sessions) {
                if (session.isOpen()) {
                    try {
                        session.sendMessage(new TextMessage(payload));
                    } catch (IOException e) {
                        // Loguear error y continuar con otras sesiones
                        e.printStackTrace();
                    }
                }
            }
        }
    }
}

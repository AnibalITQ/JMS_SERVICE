// ============================================
// 2. MODELOS DE DATOS
// ============================================



// ============================================
// 3. CONFIGURACIÓN JMS
// ============================================


// ============================================
// 4. PRODUCTOR DE MENSAJES
// ============================================



// ============================================
// 5. CONSUMIDOR UNIVERSAL
// ============================================

// ============================================
// 6. SERVICIO WEBSOCKET PRINCIPAL
// ============================================

package com.itq.notification.service;

import com.itq.notification.model.NotificationMessage;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;

@Service
public class WebSocketNotificationService {
    
    private static final Logger logger = LoggerFactory.getLogger(WebSocketNotificationService.class);
    
    @Autowired
    private ObjectMapper objectMapper;
    
    // Mapa de sesiones por usuario
    private final Map<String, CopyOnWriteArraySet<WebSocketSession>> userSessions = new ConcurrentHashMap<>();
    
    // Mapa de sesiones por parking lot
    private final Map<String, CopyOnWriteArraySet<WebSocketSession>> parkingSessions = new ConcurrentHashMap<>();
    
    // Sesiones de guardias
    private final CopyOnWriteArraySet<WebSocketSession> guardSessions = new CopyOnWriteArraySet<>();
    
    public void addUserSession(String userId, String parkingLotId, WebSocketSession session) {
        // Agregar por usuario
        userSessions.computeIfAbsent(userId, k -> new CopyOnWriteArraySet<>()).add(session);
        
        // Agregar por parking lot
        if (parkingLotId != null) {
            parkingSessions.computeIfAbsent(parkingLotId, k -> new CopyOnWriteArraySet<>()).add(session);
        }
        
        logger.info("Sesión WebSocket agregada para usuario: {} en parking: {}", userId, parkingLotId);
    }
    
    public void addGuardSession(WebSocketSession session) {
        guardSessions.add(session);
        logger.info("Sesión de guardia agregada");
    }
    
    public void removeSession(WebSocketSession session) {
        // Remover de todas las colecciones
        userSessions.values().forEach(sessions -> sessions.remove(session));
        parkingSessions.values().forEach(sessions -> sessions.remove(session));
        guardSessions.remove(session);
    }
    
    public void distributeNotification(NotificationMessage notification, String queueType) {
        try {
            String jsonMessage = objectMapper.writeValueAsString(notification);
            
            switch (notification.getMessageType()) {
                case REPORT_TO_GUARD:
                    sendToGuards(jsonMessage);
                    break;
                case REPORT_TO_USER:
                    sendToUser(notification.getToUserId(), jsonMessage);
                    break;
                case SYSTEM_NOTIFICATION:
                    sendToParkingLot(notification.getParkingLotId(), jsonMessage);
                    break;
                case PRIORITY_ALERT:
                    sendPriorityAlert(notification, jsonMessage);
                    break;
            }
            
        } catch (Exception e) {
            logger.error("Error distribuyendo notificación: {}", e.getMessage(), e);
        }
    }
    
    private void sendToGuards(String message) {
        guardSessions.forEach(session -> sendToSession(session, message));
    }
    
    private void sendToUser(String userId, String message) {
        CopyOnWriteArraySet<WebSocketSession> sessions = userSessions.get(userId);
        if (sessions != null) {
            sessions.forEach(session -> sendToSession(session, message));
        }
    }
    
    private void sendToParkingLot(String parkingLotId, String message) {
        CopyOnWriteArraySet<WebSocketSession> sessions = parkingSessions.get(parkingLotId);
        if (sessions != null) {
            sessions.forEach(session -> sendToSession(session, message));
        }
    }
    
    private void sendPriorityAlert(NotificationMessage notification, String message) {
        // Enviar a usuario específico y guardias
        sendToUser(notification.getToUserId(), message);
        sendToGuards(message);
    }
    
    private void sendToSession(WebSocketSession session, String message) {
        try {
            if (session.isOpen()) {
                session.sendMessage(new TextMessage(message));
            }
        } catch (Exception e) {
            logger.error("Error enviando mensaje a sesión WebSocket: {}", e.getMessage());
            removeSession(session);
        }
    }
}

// ============================================
// 7. API REST CONTROLLER
// ============================================


// ============================================
// 8. DTOs DE REQUEST
// ============================================

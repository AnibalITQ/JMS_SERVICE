package com.itq.notification.producer.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.itq.notification.producer.service.NotificationProducer;
import com.itq.notification.util.dto.ReportRequest;
import com.itq.notification.util.dto.SystemNotificationRequest;
import com.itq.notification.util.enums.MessageType;
import com.itq.notification.util.enums.ReportCategory;
import com.itq.notification.util.model.NotificationMessage;

@RestController
@RequestMapping("/api/notifications")
@CrossOrigin(origins = "*")
public class NotificationController {
    
    @Autowired
    private NotificationProducer notificationProducer;
    
    @PostMapping("/report")
    public ResponseEntity<?> sendReport(@RequestBody ReportRequest request) {
        try {
            // Crear mensaje para el usuario reportado
            NotificationMessage userMessage = createUserMessage(request);
            notificationProducer.sendNotification(userMessage, "notification.queue.in");
            
            // Crear mensaje para el guardia
            NotificationMessage guardMessage = createGuardMessage(request);
            notificationProducer.sendNotification( guardMessage, "guard.notification");
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("userMessageId", userMessage.getMessageId());
            response.put("guardMessageId", guardMessage.getMessageId());
            response.put("message", "Reporte enviado exitosamente");
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }
    
    @PostMapping("/system")
    public ResponseEntity<?> sendSystemNotification(@RequestBody SystemNotificationRequest request) {
        try {
            NotificationMessage message = new NotificationMessage();
            message.setMessageType(MessageType.SYSTEM_NOTIFICATION);
            message.setParkingLotId(request.getParkingLotId());
            message.setContent(request.getMessage());
            message.setCategory(ReportCategory.GENERAL);
            message.setPriority(request.getPriority() != null ? request.getPriority() : 1);
            //message.setIntProperty("intProperty", request.getAttemptsToSend());
            notificationProducer.sendNotification(message, "system.notification");
            
            return ResponseEntity.ok(Map.of("success", true, "messageId", message.getMessageId()));
            
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "error", e.getMessage()));
        }
    }
    
    private NotificationMessage createUserMessage(ReportRequest request) {
        NotificationMessage message = new NotificationMessage();
        message.setMessageType(MessageType.REPORT_TO_USER);
        message.setToUserId(request.getReportedUserId());
        message.setParkingLotId(request.getParkingLotId());
        message.setCategory(request.getCategory());
        message.setRecipientRole(request.getReportedUserRole());
        message.setContent(request.getMessage());
        message.setAnonymous(true); // Siempre anónimo
        
        // Metadata adicional
        Map<String, Object> metadata = new HashMap<>();
        metadata.put("vehiclePlate", request.getVehiclePlate());
        metadata.put("location", request.getLocation());
        message.setMetadata(metadata);
        
        return message;
    }
    
    private NotificationMessage createGuardMessage(ReportRequest request) {
        NotificationMessage message = new NotificationMessage();
        message.setMessageType(MessageType.REPORT_TO_GUARD);
        message.setParkingLotId(request.getParkingLotId());
        message.setCategory(request.getCategory());
        message.setContent(String.format("Reporte: %s - Vehículo: %s - Ubicación: %s", 
                                        request.getMessage(), 
                                        request.getVehiclePlate(), 
                                        request.getLocation()));
        message.setAnonymous(false); // Los guardias ven detalles completos
        
        // Metadata para guardias
        Map<String, Object> metadata = new HashMap<>();
        metadata.put("reporterUserId", request.getReporterUserId());
        metadata.put("reportedUserId", request.getReportedUserId());
        metadata.put("vehiclePlate", request.getVehiclePlate());
        metadata.put("location", request.getLocation());
        metadata.put("reportedUserRole", request.getReportedUserRole().name());
        message.setMetadata(metadata);
        
        return message;
    }
}
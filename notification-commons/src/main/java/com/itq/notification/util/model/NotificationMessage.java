package com.itq.notification.util.model;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

import com.itq.notification.util.enums.MessageType;
import com.itq.notification.util.enums.ReportCategory;
import com.itq.notification.util.enums.UserRole;

public class NotificationMessage {
    private String messageId;
    private String fromUserId;
    private String toUserId;
    private String parkingLotId;
    private MessageType messageType;
    private ReportCategory category;
    private UserRole senderRole;
    private UserRole recipientRole;
    private String content;
    private Map<String, Object> metadata;
    private LocalDateTime timestamp;
    private int priority;
    private boolean anonymous;
    private int attemptsToSend;
    
    // Constructor
    public NotificationMessage() {
        this.messageId = UUID.randomUUID().toString();
        this.timestamp = LocalDateTime.now();
        this.attemptsToSend = 3;
    }
    
    // Método para calcular prioridad final
    public int calculateFinalPriority() {
        int categoryPriority = category != null ? category.getPriority() : 1;
        int rolePriority = recipientRole != null ? recipientRole.getPriority() : 1;
        return Math.max(categoryPriority, rolePriority);
    }
    
    // Getters y Setters
    public String getMessageId() { return messageId; }
    public void setMessageId(String messageId) { this.messageId = messageId; }
    
    public String getFromUserId() { return fromUserId; }
    public void setFromUserId(String fromUserId) { this.fromUserId = fromUserId; }
    
    public String getToUserId() { return toUserId; }
    public void setToUserId(String toUserId) { this.toUserId = toUserId; }
    
    public String getParkingLotId() { return parkingLotId; }
    public void setParkingLotId(String parkingLotId) { this.parkingLotId = parkingLotId; }
    
    public MessageType getMessageType() { return messageType; }
    public void setMessageType(MessageType messageType) { this.messageType = messageType; }
    public int getAttemptsToSend() { return attemptsToSend; }
    public void setAttemptsToSend(int attemptsToSend) { this.attemptsToSend = attemptsToSend; }
    
    public ReportCategory getCategory() { return category; }
    public void setCategory(ReportCategory category) { 
        this.category = category;
        this.priority = calculateFinalPriority();
    }
    
    public UserRole getSenderRole() { return senderRole; }
    public void setSenderRole(UserRole senderRole) { this.senderRole = senderRole; }
    
    public UserRole getRecipientRole() { return recipientRole; }
    public void setRecipientRole(UserRole recipientRole) { 
        this.recipientRole = recipientRole;
        this.priority = calculateFinalPriority();
    }
    
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
    
    public Map<String, Object> getMetadata() { return metadata; }
    public void setMetadata(Map<String, Object> metadata) { this.metadata = metadata; }
    
    public LocalDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }
    
    public int getPriority() { return priority; }
    public void setPriority(int priority) { this.priority = priority; }
    
    public boolean isAnonymous() { return anonymous; }
    public void setAnonymous(boolean anonymous) { this.anonymous = anonymous; }
}
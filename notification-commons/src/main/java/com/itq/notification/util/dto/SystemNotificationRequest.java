package com.itq.notification.util.dto;

public class SystemNotificationRequest {
    private String parkingLotId;
    private String message;
    private Integer priority;
    
    // Getters y Setters
    public String getParkingLotId() { return parkingLotId; }
    public void setParkingLotId(String parkingLotId) { this.parkingLotId = parkingLotId; }
    
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
    
    public Integer getPriority() { return priority; }
    public void setPriority(Integer priority) { this.priority = priority; }
}
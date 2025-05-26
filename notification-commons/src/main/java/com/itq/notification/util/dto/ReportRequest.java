package com.itq.notification.util.dto;

import com.itq.notification.util.enums.ReportCategory;
import com.itq.notification.util.enums.UserRole;

public class ReportRequest {
    private String reporterUserId;
    private String reportedUserId;
    private UserRole reportedUserRole;
    private String parkingLotId;
    private String vehiclePlate;
    private String location;
    private ReportCategory category;
    private String message;
    
    // Getters y Setters
    public String getReporterUserId() { return reporterUserId; }
    public void setReporterUserId(String reporterUserId) { this.reporterUserId = reporterUserId; }
    
    public String getReportedUserId() { return reportedUserId; }
    public void setReportedUserId(String reportedUserId) { this.reportedUserId = reportedUserId; }
    
    public UserRole getReportedUserRole() { return reportedUserRole; }
    public void setReportedUserRole(UserRole reportedUserRole) { this.reportedUserRole = reportedUserRole; }
    
    public String getParkingLotId() { return parkingLotId; }
    public void setParkingLotId(String parkingLotId) { this.parkingLotId = parkingLotId; }
    
    public String getVehiclePlate() { return vehiclePlate; }
    public void setVehiclePlate(String vehiclePlate) { this.vehiclePlate = vehiclePlate; }
    
    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }
    
    public ReportCategory getCategory() { return category; }
    public void setCategory(ReportCategory category) { this.category = category; }
    
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
}

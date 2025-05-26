package com.itq.notification.util.enums;

public enum ReportCategory {
    EMERGENCY(5, "Emergencia"),
    BLOCKING_EXIT(4, "Bloqueo de salida"),
    ACCIDENT(4, "Accidente"),
    IMPROPER_PARKING(3, "Estacionamiento inadecuado"),
    LIGHTS_ON(2, "Luces encendidas"),
    GENERAL(1, "General");
    
    private final int priority;
    private final String description;
    
    ReportCategory(int priority, String description) {
        this.priority = priority;
        this.description = description;
    }
    
    public int getPriority() { return priority; }
    public String getDescription() { return description; }
}
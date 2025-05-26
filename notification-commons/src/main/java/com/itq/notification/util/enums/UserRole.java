package com.itq.notification.util.enums;

public enum UserRole {
    DIRECTOR(5, "Director"),
    PROFESOR(4, "Profesor"),
    REGULAR_USER(2, "Usuario Habitual"),
    VISITOR(1, "Visitante");
    
    private final int priority;
    private final String description;
    
    UserRole(int priority, String description) {
        this.priority = priority;
        this.description = description;
    }
    
    public int getPriority() { return priority; }
    public String getDescription() { return description; }
}


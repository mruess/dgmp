package com.company.validation;

/**
 * Individual validation message
 */
public class ValidationMessage {
    private final String severity;
    private final String location;
    private final String message;

    public ValidationMessage(String severity, String location, String message) {
        this.severity = severity;
        this.location = location;
        this.message = message;
    }

    public String getSeverity() {
        return severity;
    }

    public String getLocation() {
        return location;
    }

    public String getMessage() {
        return message;
    }

    @Override
    public String toString() {
        return String.format("[%s] %s: %s",
                severity,
                location.isEmpty() ? "root" : location,
                message);
    }
}

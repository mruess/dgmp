package com.company.validation;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Response object for validation results
 */
public class ValidationResponse {
    private final boolean valid;
    private final List<ValidationMessage> messages;

    public ValidationResponse(boolean valid, List<ValidationMessage> messages) {
        this.valid = valid;
        this.messages = messages;
    }

    public static ValidationResponse error(String errorMessage) {
        return new ValidationResponse(
                false,
                List.of(new ValidationMessage("ERROR", "", errorMessage))
        );
    }

    public boolean isValid() {
        return valid;
    }

    public List<ValidationMessage> getMessages() {
        return messages;
    }

    public List<ValidationMessage> getErrors() {
        return messages.stream()
                .filter(m -> "ERROR".equals(m.getSeverity()) || "FATAL".equals(m.getSeverity()))
                .collect(Collectors.toList());
    }

    public List<ValidationMessage> getWarnings() {
        return messages.stream()
                .filter(m -> "WARNING".equals(m.getSeverity()))
                .collect(Collectors.toList());
    }

    public List<ValidationMessage> getInformation() {
        return messages.stream()
                .filter(m -> "INFORMATION".equals(m.getSeverity()))
                .collect(Collectors.toList());
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Validation ").append(valid ? "SUCCESSFUL" : "FAILED").append("\n");
        sb.append("Total messages: ").append(messages.size()).append("\n");

        List<ValidationMessage> errors = getErrors();
        if (!errors.isEmpty()) {
            sb.append("\nERRORS (").append(errors.size()).append("):\n");
            errors.forEach(e -> sb.append("  - ").append(e).append("\n"));
        }

        List<ValidationMessage> warnings = getWarnings();
        if (!warnings.isEmpty()) {
            sb.append("\nWARNINGS (").append(warnings.size()).append("):\n");
            warnings.forEach(w -> sb.append("  - ").append(w).append("\n"));
        }

        return sb.toString();
    }
}

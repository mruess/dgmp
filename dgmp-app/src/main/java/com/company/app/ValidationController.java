package com.company.app;

import com.company.fhir.SimpleMedicationDocumentService;
import com.company.validation.EpaMedicationValidationService;
import com.company.validation.ValidationResponse;
import com.company.validation.ValidationMessage;
import org.hl7.fhir.r4.model.Bundle;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import ca.uhn.fhir.parser.IParser;

/**
 * REST Controller for EPA Medication validation endpoints
 */
@RestController
@RequestMapping("/api/validation")
public class ValidationController {

    private final EpaMedicationValidationService validationService;
    private final SimpleMedicationDocumentService medicationService;
    private final IParser jsonParser;

    public ValidationController(
            EpaMedicationValidationService validationService,
            SimpleMedicationDocumentService medicationService,
            IParser jsonParser) {
        this.validationService = validationService;
        this.medicationService = medicationService;
        this.jsonParser = jsonParser;
    }

    /**
     * Validates a generated medication document
     */
    @GetMapping("/test-generated")
    public ResponseEntity<ValidationResultDto> validateGeneratedDocument() {
        String json = medicationService.createMedicationDocument();
        ValidationResponse response = validationService.validateJson(json);
        return ResponseEntity.ok(toDto(response, json));
    }

    /**
     * Validates FHIR JSON from request body
     */
    @PostMapping(value = "/validate", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ValidationResultDto> validateJson(@RequestBody String jsonContent) {
        ValidationResponse response = validationService.validateJson(jsonContent);

        if (response.isValid()) {
            return ResponseEntity.ok(toDto(response, null));
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(toDto(response, null));
        }
    }

    /**
     * Validates FHIR JSON and returns detailed results
     */
    @PostMapping(value = "/validate-detailed", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<DetailedValidationResultDto> validateJsonDetailed(@RequestBody String jsonContent) {
        ValidationResponse response = validationService.validateJson(jsonContent);

        DetailedValidationResultDto dto = new DetailedValidationResultDto();
        dto.valid = response.isValid();
        dto.totalMessages = response.getMessages().size();
        dto.errorCount = response.getErrors().size();
        dto.warningCount = response.getWarnings().size();
        dto.informationCount = response.getInformation().size();

        dto.errors = response.getErrors().stream()
                .map(this::toMessageDto)
                .toList();
        dto.warnings = response.getWarnings().stream()
                .map(this::toMessageDto)
                .toList();
        dto.information = response.getInformation().stream()
                .map(this::toMessageDto)
                .toList();

        if (response.isValid()) {
            return ResponseEntity.ok(dto);
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(dto);
        }
    }

    /**
     * Simple health check for validation service
     */
    @GetMapping("/health")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("EPA Medication Validation Service is running");
    }

    private ValidationResultDto toDto(ValidationResponse response, String validatedJson) {
        ValidationResultDto dto = new ValidationResultDto();
        dto.valid = response.isValid();
        dto.messageCount = response.getMessages().size();
        dto.summary = response.toString();
        dto.validatedJson = validatedJson;
        return dto;
    }

    private MessageDto toMessageDto(ValidationMessage msg) {
        MessageDto dto = new MessageDto();
        dto.severity = msg.getSeverity();
        dto.location = msg.getLocation();
        dto.message = msg.getMessage();
        return dto;
    }

    // DTOs for JSON responses

    public static class ValidationResultDto {
        public boolean valid;
        public int messageCount;
        public String summary;
        public String validatedJson;
    }

    public static class DetailedValidationResultDto {
        public boolean valid;
        public int totalMessages;
        public int errorCount;
        public int warningCount;
        public int informationCount;
        public java.util.List<MessageDto> errors;
        public java.util.List<MessageDto> warnings;
        public java.util.List<MessageDto> information;
    }

    public static class MessageDto {
        public String severity;
        public String location;
        public String message;
    }
}

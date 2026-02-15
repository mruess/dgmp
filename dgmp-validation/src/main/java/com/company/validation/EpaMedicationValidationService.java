package com.company.validation;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.parser.IParser;
import ca.uhn.fhir.validation.FhirValidator;
import ca.uhn.fhir.validation.ResultSeverityEnum;
import ca.uhn.fhir.validation.SingleValidationMessage;
import ca.uhn.fhir.validation.ValidationResult;
import org.hl7.fhir.instance.model.api.IBaseResource;
import org.hl7.fhir.r4.model.Bundle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Service for validating FHIR resources against EPA Medication profiles
 */
@Service
public class EpaMedicationValidationService {

    private static final Logger log = LoggerFactory.getLogger(EpaMedicationValidationService.class);

    private final FhirValidator fhirValidator;
    private final FhirContext fhirContext;
    private final IParser jsonParser;

    public EpaMedicationValidationService(
            FhirValidator fhirValidator,
            FhirContext fhirContext,
            IParser jsonParser) {
        this.fhirValidator = fhirValidator;
        this.fhirContext = fhirContext;
        this.jsonParser = jsonParser;
    }

    /**
     * Validates a FHIR Bundle against EPA Medication profiles
     *
     * @param bundle The Bundle to validate
     * @return ValidationResponse with results
     */
    public ValidationResponse validateBundle(Bundle bundle) {
        log.info("Validating Bundle: {}", bundle.getId());
        return validate(bundle);
    }

    /**
     * Validates a FHIR resource from JSON string
     *
     * @param jsonContent The JSON content
     * @return ValidationResponse with results
     */
    public ValidationResponse validateJson(String jsonContent) {
        log.info("Parsing and validating JSON content");
        try {
            IBaseResource resource = jsonParser.parseResource(jsonContent);
            return validate(resource);
        } catch (Exception e) {
            log.error("Failed to parse JSON: {}", e.getMessage());
            return ValidationResponse.error("Failed to parse JSON: " + e.getMessage());
        }
    }

    /**
     * Validates any FHIR resource
     *
     * @param resource The resource to validate
     * @return ValidationResponse with results
     */
    public ValidationResponse validate(IBaseResource resource) {
        try {
            ValidationResult result = fhirValidator.validateWithResult(resource);

            List<ValidationMessage> messages = result.getMessages().stream()
                    .map(this::convertMessage)
                    .collect(Collectors.toList());

            boolean isValid = result.isSuccessful();

            if (isValid) {
                log.info("Validation successful");
            } else {
                log.warn("Validation failed with {} issues", messages.size());
            }

            return new ValidationResponse(isValid, messages);

        } catch (Exception e) {
            log.error("Validation error: {}", e.getMessage(), e);
            return ValidationResponse.error("Validation error: " + e.getMessage());
        }
    }

    private ValidationMessage convertMessage(SingleValidationMessage msg) {
        return new ValidationMessage(
                msg.getSeverity().name(),
                msg.getLocationString(),
                msg.getMessage()
        );
    }
}

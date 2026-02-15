package com.company.app;

import com.company.fhir.SimpleFhirService;
import com.company.fhir.SimpleMedicationDocumentService;

import ca.uhn.fhir.context.FhirContext;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import com.company.fhir.SimpleDocumentBundleService;

import ca.uhn.fhir.validation.FhirValidator;
import ca.uhn.fhir.validation.ValidationResult;



@RestController
public class HealthController {

    private final FhirContext context;
    private final SimpleFhirService fhirService;
    private final SimpleDocumentBundleService bundleService;
    private final SimpleMedicationDocumentService medicationService;
    private final FhirValidator validator;


    public HealthController(FhirContext context,
                            SimpleFhirService fhirService,
                            SimpleDocumentBundleService bundleService,
                            SimpleMedicationDocumentService medicationService,
                            FhirValidator validator) {
        this.context = context;
        this.fhirService = fhirService;
        this.bundleService = bundleService;
        this.medicationService = medicationService;
        this.validator = validator;
    }

    @GetMapping("/health")
    public String health() {
        return "FHIR version: " + context.getVersion().getVersion();
    }

    @GetMapping("/sample")
    public String sample() {
        return fhirService.createSamplePatientJson();
    }

    @GetMapping("/document")
    public String document() {
        return bundleService.createSimpleDocumentBundle();
    }

    @GetMapping("/medication-document")
    public String medicationDocument() {
        return medicationService.createMedicationDocument();
    }

    @GetMapping("/validate")
    public String validate() {
        String json = medicationService.createMedicationDocument();
        ValidationResult result = validator.validateWithResult(json);
        return result.toString();
    }

    @GetMapping("/test")
    public String test() {
        return "Test";
    }
 

}

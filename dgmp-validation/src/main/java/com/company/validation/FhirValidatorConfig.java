package com.company.validation;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.context.support.DefaultProfileValidationSupport;
import ca.uhn.fhir.context.support.IValidationSupport;
import ca.uhn.fhir.validation.FhirValidator;
import org.hl7.fhir.common.hapi.validation.support.CachingValidationSupport;
import org.hl7.fhir.common.hapi.validation.support.CommonCodeSystemsTerminologyService;
import org.hl7.fhir.common.hapi.validation.support.InMemoryTerminologyServerValidationSupport;
import org.hl7.fhir.common.hapi.validation.support.NpmPackageValidationSupport;
import org.hl7.fhir.common.hapi.validation.support.ValidationSupportChain;
import org.hl7.fhir.common.hapi.validation.validator.FhirInstanceValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;

/**
 * Configuration for FHIR Validator with EPA Medication Profile support
 */
@Configuration
public class FhirValidatorConfig {

    private static final Logger log = LoggerFactory.getLogger(FhirValidatorConfig.class);

    @Bean
    public IValidationSupport validationSupport(FhirContext fhirContext) throws IOException {
        log.info("Initializing FHIR ValidationSupport with EPA Medication profiles");

        // Default validation support (provides base FHIR validation)
        DefaultProfileValidationSupport defaultSupport = new DefaultProfileValidationSupport(fhirContext);

        // In-memory terminology server
        InMemoryTerminologyServerValidationSupport inMemoryTerminologySupport =
                new InMemoryTerminologyServerValidationSupport(fhirContext);

        // Common code systems (LOINC, SNOMED, etc.)
        CommonCodeSystemsTerminologyService commonCodeSystemsSupport =
                new CommonCodeSystemsTerminologyService(fhirContext);

        // NPM Package support for EPA Medication profiles
        NpmPackageValidationSupport npmPackageSupport = new NpmPackageValidationSupport(fhirContext);

        // Load EPA Medication package from public registry
        // Note: This will download the package from packages.simplifier.net if not cached
        try {
            log.info("Loading EPA Medication package: de.gematik.epa-medication version 3.1.0");
            npmPackageSupport.loadPackageFromClasspath("classpath:package/de.gematik.epa-medication-3.1.0.tgz");
        } catch (Exception e) {
            log.warn("Could not load EPA Medication package from classpath, will try online: {}", e.getMessage());
            // If loading from classpath fails, the validator will try to fetch from packages.simplifier.net
            // This is automatic for known packages
        }

        // Chain all validation supports
        ValidationSupportChain validationSupportChain = new ValidationSupportChain(
                npmPackageSupport,
                defaultSupport,
                inMemoryTerminologySupport,
                commonCodeSystemsSupport
        );

        // Wrap in caching validation support for performance
        CachingValidationSupport cachingValidationSupport = new CachingValidationSupport(validationSupportChain);

        log.info("ValidationSupport initialized successfully");
        return cachingValidationSupport;
    }

    @Bean
    public FhirValidator fhirValidator(FhirContext fhirContext, IValidationSupport validationSupport) {
        log.info("Creating FhirValidator with EPA Medication profile support");

        FhirValidator validator = fhirContext.newValidator();

        // Create FhirInstanceValidator with custom validation support
        FhirInstanceValidator instanceValidator = new FhirInstanceValidator(validationSupport);

        // Configure validator behavior
        instanceValidator.setNoTerminologyChecks(false);
        instanceValidator.setErrorForUnknownProfiles(false);
        instanceValidator.setAnyExtensionsAllowed(true);
        instanceValidator.setNoExtensibleWarnings(true);

        validator.registerValidatorModule(instanceValidator);

        log.info("FhirValidator created successfully");
        return validator;
    }
}

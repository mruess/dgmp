package com.company.validation;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.validation.FhirValidator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FhirValidatorConfig {

    @Bean
    public FhirValidator fhirValidator(FhirContext context) {
        return context.newValidator();
    }
}

package com.company.fhir;
import ca.uhn.fhir.context.FhirContext;
public class FhirConfig {
    public static FhirContext context() {
        return FhirContext.forR4();
    }
}
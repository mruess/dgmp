package com.company.fhir;

import ca.uhn.fhir.context.FhirContext;
import org.hl7.fhir.r4.model.Patient;
import org.springframework.stereotype.Service;

@Service
public class SimpleFhirService {

    private final FhirContext context;

    public SimpleFhirService(FhirContext context) {
        this.context = context;
    }

    public String createSamplePatientJson() {

        Patient patient = new Patient();
        patient.addName()
                .setFamily("Mustermann")
                .addGiven("Max");

        return context.newJsonParser()
                .setPrettyPrint(true)
                .encodeResourceToString(patient);
    }
}

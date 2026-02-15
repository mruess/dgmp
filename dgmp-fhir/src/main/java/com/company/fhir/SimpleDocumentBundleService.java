package com.company.fhir;

import ca.uhn.fhir.context.FhirContext;
import org.hl7.fhir.r4.model.*;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.UUID;

@Service
public class SimpleDocumentBundleService {

    private final FhirContext context;

    public SimpleDocumentBundleService(FhirContext context) {
        this.context = context;
    }

public String createSimpleDocumentBundle() {

    Bundle bundle = new Bundle();
    bundle.setType(Bundle.BundleType.DOCUMENT);
    bundle.setTimestamp(new Date());

    // Bundle Identifier (wichtig für DOCUMENT Bundles)
    bundle.setIdentifier(
            new Identifier()
                    .setSystem("urn:ietf:rfc:3986")
                    .setValue("urn:uuid:" + UUID.randomUUID())
    );

    // UUIDs
    String compositionId = UUID.randomUUID().toString();
    String patientId = UUID.randomUUID().toString();
    String organizationId = UUID.randomUUID().toString();
    String observationId = UUID.randomUUID().toString();

    String compositionUrn = "urn:uuid:" + compositionId;
    String patientUrn = "urn:uuid:" + patientId;
    String organizationUrn = "urn:uuid:" + organizationId;
    String observationUrn = "urn:uuid:" + observationId;

    // ======================
    // Patient
    // ======================

    Patient patient = new Patient();
    patient.setId(patientId);
    patient.addName()
            .setFamily("Mustermann")
            .addGiven("Max");

    bundle.addEntry()
            .setFullUrl(patientUrn)
            .setResource(patient);

    // ======================
    // Organization (Author + Custodian)
    // ======================

    Organization org = new Organization();
    org.setId(organizationId);
    org.setName("Praxis Musterarzt");

    bundle.addEntry()
            .setFullUrl(organizationUrn)
            .setResource(org);

    // ======================
    // Observation (als Beispielinhalt)
    // ======================

    Observation observation = new Observation();
    observation.setId(observationId);
    observation.setStatus(Observation.ObservationStatus.FINAL);
    observation.setSubject(new Reference(patientUrn));
    observation.setCode(new CodeableConcept()
            .addCoding(new Coding()
                    .setSystem("http://loinc.org")
                    .setCode("8310-5")
                    .setDisplay("Body temperature")));
    observation.setValue(new Quantity()
            .setValue(36.6)
            .setUnit("°C"));

    bundle.addEntry()
            .setFullUrl(observationUrn)
            .setResource(observation);

    // ======================
    // Composition
    // ======================

    Composition composition = new Composition();
    composition.setId(compositionId);
    composition.setStatus(Composition.CompositionStatus.FINAL);
    composition.setDate(new Date());
    composition.setTitle("Simple Clinical Document");
    composition.setSubject(new Reference(patientUrn));

    // LOINC Type (sehr wichtig in realen Systemen)
    composition.setType(new CodeableConcept()
            .addCoding(new Coding()
                    .setSystem("http://loinc.org")
                    .setCode("34133-9")
                    .setDisplay("Summarization of Episode Note")));

    // Author
    composition.addAuthor(new Reference(organizationUrn));

    // Custodian
    composition.setCustodian(new Reference(organizationUrn));

    // Section mit referenziertem Inhalt
    Composition.SectionComponent section =
            new Composition.SectionComponent();
    section.setTitle("Observations");
    section.addEntry(new Reference(observationUrn));

    composition.addSection(section);

    // Composition MUSS erstes Entry sein
    bundle.getEntry().add(0,
            new Bundle.BundleEntryComponent()
                    .setFullUrl(compositionUrn)
                    .setResource(composition)
    );

    return context.newJsonParser()
            .setPrettyPrint(true)
            .encodeResourceToString(bundle);
    }
}

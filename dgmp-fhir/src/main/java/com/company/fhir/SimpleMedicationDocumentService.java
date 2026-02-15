package com.company.fhir;

import java.util.Date;
import java.util.UUID;

import org.hl7.fhir.r4.model.Bundle;
import org.hl7.fhir.r4.model.CodeableConcept;
import org.hl7.fhir.r4.model.Coding;
import org.hl7.fhir.r4.model.Composition;
import org.hl7.fhir.r4.model.Dosage;
import org.hl7.fhir.r4.model.Identifier;
import org.hl7.fhir.r4.model.Medication;
import org.hl7.fhir.r4.model.MedicationStatement;
import org.hl7.fhir.r4.model.Organization;
import org.hl7.fhir.r4.model.Patient;
import org.hl7.fhir.r4.model.Reference;
import org.springframework.stereotype.Service;

import ca.uhn.fhir.context.FhirContext;

@Service
public class SimpleMedicationDocumentService {

    private final FhirContext context;

    public SimpleMedicationDocumentService(FhirContext context) {
        this.context = context;
    }

    public String createMedicationDocument() {

        Bundle bundle = new Bundle();
        bundle.getMeta().addProfile(EpaProfiles.EPA_MEDICATION_BUNDLE);
        bundle.setType(Bundle.BundleType.DOCUMENT);
        bundle.setTimestamp(new Date());

        bundle.setIdentifier(
                new Identifier()
                        .setSystem("urn:ietf:rfc:3986")
                        .setValue("urn:uuid:" + UUID.randomUUID())
        );

        bundle.getMeta().addSecurity(
        new Coding()
                .setSystem("http://terminology.hl7.org/CodeSystem/v3-Confidentiality")
                .setCode("N")
                .setDisplay("normal")
        );


        // UUIDs
        String compositionId = UUID.randomUUID().toString();
        String patientId = UUID.randomUUID().toString();
        String organizationId = UUID.randomUUID().toString();
        String medicationId = UUID.randomUUID().toString();
        String statementId = UUID.randomUUID().toString();

        String compositionUrn = "urn:uuid:" + compositionId;
        String patientUrn = "urn:uuid:" + patientId;
        String organizationUrn = "urn:uuid:" + organizationId;
        String medicationUrn = "urn:uuid:" + medicationId;
        String statementUrn = "urn:uuid:" + statementId;

        // ======================
        // Patient
        // ======================

        Patient patient = new Patient();
        patient.setId(patientId);

        patient.addIdentifier()
            .setSystem("http://fhir.de/sid/gkv/kvid-10")
            .setValue("X123456789");

        patient.addName()
                .setFamily("Mustermann")
                .addGiven("Max");

        bundle.addEntry()
                .setFullUrl(patientUrn)
                .setResource(patient);

        // ======================
        // Organization
        // ======================

        Organization org = new Organization();
        org.setId(organizationId);
        org.setName("Praxis Musterarzt");

        bundle.addEntry()
                .setFullUrl(organizationUrn)
                .setResource(org);

        // ======================
        // Medication
        // ======================

        Medication medication = new Medication();
        medication.setId(medicationId);

        medication.addIdentifier()
            .setSystem("http://fhir.de/sid/pzn")
            .setValue("12345678");        

        medication.setCode(new CodeableConcept()
            .addCoding(new Coding()
                .setSystem("http://fhir.de/CodeSystem/ifa/pzn")
                .setCode("12345678")
                .setDisplay("Ibuprofen 400mg")));
                
        bundle.addEntry()
                .setFullUrl(medicationUrn)
                .setResource(medication);

        // ======================
        // MedicationStatement
        // ======================

        MedicationStatement statement = new MedicationStatement();
        statement.setId(statementId);
        statement.getMeta().addProfile(EpaProfiles.EPA_MEDICATION_STATEMENT);
        statement.getMeta().addSecurity(
            new Coding()
                .setSystem("http://terminology.hl7.org/CodeSystem/v3-Confidentiality")
                .setCode("N")
                .setDisplay("normal")
        );

       CodeableConcept category = new CodeableConcept();
                category.addCoding(new Coding()
                .setSystem("http://terminology.hl7.org/CodeSystem/medication-statement-category")
                .setCode("community")
                .setDisplay("Community"));

        statement.setCategory(category);




        statement.setStatus(MedicationStatement.MedicationStatementStatus.ACTIVE);
        statement.setSubject(new Reference(patientUrn));
        statement.setMedication(new Reference(medicationUrn));
        statement.setDateAsserted(new Date());

        statement.addDosage(new Dosage().setText("1-0-1"));

        bundle.addEntry()
                .setFullUrl(statementUrn)
                .setResource(statement);

        // ======================
        // Composition
        // ======================

        Composition composition = new Composition();
        composition.setId(compositionId);
        composition.setStatus(Composition.CompositionStatus.FINAL);
        composition.setDate(new Date());
        composition.setTitle("Medication Document");
        composition.setSubject(new Reference(patientUrn));

        composition.setType(new CodeableConcept()
                .addCoding(new Coding()
                        .setSystem("http://loinc.org")
                        .setCode("56445-0")
                        .setDisplay("Medication summary Document")));

        composition.addAuthor(new Reference(organizationUrn));
        composition.setCustodian(new Reference(organizationUrn));

        Composition.SectionComponent section =
                new Composition.SectionComponent();

        section.setTitle("Medications");
        section.setCode(new CodeableConcept()
                .addCoding(new Coding()
                        .setSystem("http://loinc.org")
                        .setCode("10160-0")
                        .setDisplay("History of Medication use")));

        section.addEntry(new Reference(statementUrn));

        composition.addSection(section);

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

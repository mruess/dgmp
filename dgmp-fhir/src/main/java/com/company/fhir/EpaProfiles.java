package com.company.fhir;

/**
 * EPA Medication Profile URLs for version 3.1.0 based on EPA 3.1.3
 */
public final class EpaProfiles {

    private EpaProfiles() {}

    // Base URL for EPA Medication profiles
    public static final String BASE_URL = "https://gematik.de/fhir/epa-medication/StructureDefinition/";

    // Package identifier
    public static final String PACKAGE_ID = "de.gematik.epa-medication";
    public static final String PACKAGE_VERSION = "3.1.0";

    // Document Bundle
    public static final String EPA_MEDICATION_BUNDLE =
            BASE_URL + "epa-medication-bundle";

    // MedicationStatement
    public static final String EPA_MEDICATION_STATEMENT =
            BASE_URL + "epa-medication-statement";

    // Medication
    public static final String EPA_MEDICATION =
            BASE_URL + "epa-medication";

    // MedicationRequest (Verordnung)
    public static final String EPA_MEDICATION_REQUEST =
            BASE_URL + "epa-medication-request";

    // MedicationDispense (Abgabe)
    public static final String EPA_MEDICATION_DISPENSE =
            BASE_URL + "epa-medication-dispense";

    // Composition
    public static final String EPA_MEDICATION_COMPOSITION =
            BASE_URL + "epa-medication-composition";

    // Organization
    public static final String EPA_MEDICATION_ORGANIZATION =
            BASE_URL + "epa-medication-organization";

    // Patient
    public static final String EPA_MEDICATION_PATIENT =
            BASE_URL + "epa-medication-patient";

    // Practitioner
    public static final String EPA_MEDICATION_PRACTITIONER =
            BASE_URL + "epa-medication-practitioner";

    // PractitionerRole
    public static final String EPA_MEDICATION_PRACTITIONER_ROLE =
            BASE_URL + "epa-medication-practitionerrole";
}

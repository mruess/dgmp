# DGMP EPA - Digitaler Medikamentenplan EPA Validation

[![Java](https://img.shields.io/badge/Java-21-orange.svg)](https://openjdk.org/projects/jdk/21/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.3.2-brightgreen.svg)](https://spring.io/projects/spring-boot)
[![HAPI FHIR](https://img.shields.io/badge/HAPI%20FHIR-6.10.0-blue.svg)](https://hapifhir.io/)
[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](https://opensource.org/licenses/Apache-2.0)

Implementierung eines digitalen Medikamentenplans (DGMP) mit FHIR-Validierung gegen die gematik EPA Medication Profile 3.1.0 basierend auf EPA 3.1.3.

## 📋 Übersicht

Dieses Projekt stellt eine vollständige Spring Boot-Anwendung bereit, die FHIR-Ressourcen (insbesondere Medication Documents) gegen die offiziellen gematik EPA Medication Profile validiert. Es nutzt HAPI FHIR 6.10.0 mit NPM Package Support für die automatische Validierung.

## ✨ Features

- **FHIR R4 Validierung** mit HAPI FHIR 6.10.0
- **EPA Medication Profile 3.1.0** Unterstützung
- **REST API** für Validierung mit detailliertem Feedback
- **Multi-Modul Maven Projekt** mit klarer Architektur
- **Automatische Profil-Erkennung** aus `meta.profile`
- **Detailliertes Feedback** mit Severity-Levels (Errors, Warnings, Info)
- **Spring Boot 3.3.2** mit Java 21
- **Sample Documents** für Tests

## 🚀 Quick Start

### Voraussetzungen

- Java 21 oder höher
- Maven 3.8+
- Git

### Installation

```bash
# Repository klonen
git clone https://github.com/mruess/dgmp.git
cd dgmp

# Projekt bauen
mvn clean install

# Anwendung starten
mvn spring-boot:run -pl dgmp-app
```

Die Anwendung läuft nun auf: http://localhost:8080

## 📚 API Dokumentation

### Endpoints

#### Health Check
```bash
GET /health
```
Gibt die FHIR-Version zurück.

#### Validation Service Health
```bash
GET /api/validation/health
```
Prüft, ob der Validation Service läuft.

#### Generiertes Document validieren
```bash
GET /api/validation/test-generated
```
Erstellt ein Medication Document und validiert es sofort.

**Response:**
```json
{
  "valid": true,
  "messageCount": 0,
  "summary": "Validation SUCCESSFUL\nTotal messages: 0\n",
  "validatedJson": "{ ... }"
}
```

#### Eigenes FHIR JSON validieren
```bash
POST /api/validation/validate
Content-Type: application/json

{
  "resourceType": "Bundle",
  ...
}
```

#### Detaillierte Validierung
```bash
POST /api/validation/validate-detailed
Content-Type: application/json

{
  "resourceType": "Bundle",
  ...
}
```

**Response:**
```json
{
  "valid": true,
  "totalMessages": 0,
  "errorCount": 0,
  "warningCount": 0,
  "informationCount": 0,
  "errors": [],
  "warnings": [],
  "information": []
}
```

### Beispiel-Aufruf

```bash
# Mit curl
curl -X POST http://localhost:8080/api/validation/validate-detailed \
  -H "Content-Type: application/json" \
  -d @sampleMedicationDocument.json

# Mit jq für formatierte Ausgabe
curl -X POST http://localhost:8080/api/validation/validate-detailed \
  -H "Content-Type: application/json" \
  -d @sampleMedicationDocument.json | jq '.'
```

## 🏗️ Projektstruktur

```
dgmp-epa-enterprise/
├── dgmp-domain/          # Domain-Modelle
│   └── PatientRef.java
├── dgmp-fhir/            # FHIR-Services & Profile
│   ├── EpaProfiles.java
│   ├── FhirConfiguration.java
│   ├── SimpleMedicationDocumentService.java
│   └── ...
├── dgmp-validation/      # HAPI FHIR Validierung
│   ├── FhirValidatorConfig.java
│   ├── EpaMedicationValidationService.java
│   ├── ValidationResponse.java
│   └── ValidationMessage.java
├── dgmp-app/             # Spring Boot Application
│   ├── Application.java
│   ├── ValidationController.java
│   └── HealthController.java
├── sample*.json          # Beispiel-Dokumente
└── pom.xml               # Parent POM
```

## 🔧 Technologien

- **Java 21** - Programming Language
- **Spring Boot 3.3.2** - Application Framework
- **HAPI FHIR 6.10.0** - FHIR Validation Engine
- **Maven** - Build Tool
- **Tomcat** - Embedded Web Server

### HAPI FHIR Components

- `hapi-fhir-validation` - Validation Engine
- `hapi-fhir-structures-r4` - R4 Structures
- `org.hl7.fhir.validation` - Core Validation
- `NpmPackageValidationSupport` - EPA Profile Loading

## 📖 EPA Medication Profile

Das Projekt unterstützt die folgenden gematik EPA Medication Profile (Version 3.1.0):

- `epa-medication-bundle` - Document Bundle
- `epa-medication-statement` - Medication Statement
- `epa-medication` - Medication
- `epa-medication-request` - Medication Request
- `epa-medication-dispense` - Medication Dispense
- `epa-medication-composition` - Composition
- `epa-medication-organization` - Organization
- `epa-medication-patient` - Patient
- `epa-medication-practitioner` - Practitioner
- `epa-medication-practitionerrole` - PractitionerRole

Profil-URLs sind in `EpaProfiles.java` definiert.

## 🧪 Validierung

Die Validierung erfolgt in mehreren Schritten:

1. **Parsing** - JSON wird in FHIR-Ressource geparst
2. **Structure Validation** - Prüfung der FHIR-Struktur
3. **Profile Validation** - Prüfung gegen EPA Profile
4. **Terminology Validation** - CodeSystem/ValueSet Validierung
5. **Business Rules** - Constraints und Invarianten

### Validation Support Chain

```
CachingValidationSupport
├── NpmPackageValidationSupport (EPA Profiles)
├── DefaultProfileValidationSupport (Base FHIR)
├── InMemoryTerminologyServerValidationSupport
└── CommonCodeSystemsTerminologyService
```

## 🛠️ Entwicklung

### Build

```bash
# Kompilieren
mvn clean compile

# Tests (wenn vorhanden)
mvn test

# Package
mvn clean package

# Install in local repo
mvn clean install
```

### Run

```bash
# Mit Maven
mvn spring-boot:run -pl dgmp-app

# Mit JAR
java -jar dgmp-app/target/dgmp-app-1.0.0-SNAPSHOT.jar

# Mit spezifischem Port
java -jar dgmp-app/target/dgmp-app-1.0.0-SNAPSHOT.jar --server.port=9090
```

### Konfiguration

Die Anwendung kann über `application.yml` konfiguriert werden:

```yaml
server:
  port: 8080

logging:
  level:
    com.company: DEBUG
    ca.uhn.fhir: INFO
```

## 📝 Sample Documents

Das Projekt enthält mehrere Beispiel-Dokumente:

- `sampleMedicationDocument.json` - Basis Medication Document
- `sampleMedicationDocumentProfile.json` - Mit Profil-Referenz
- `sampleMedicationDocumentProfileSecurity.json` - Mit Security Labels
- `sampleMedikationMitCategory.json` - Mit Category
- `testDocument2.json` - Test Document
- `testDocumentBundle.json` - Test Bundle

## 🔒 Sicherheit

Das Projekt validiert medizinische Daten. Beachten Sie:

- Verwenden Sie HTTPS in Produktion
- Implementieren Sie Authentifizierung/Autorisierung
- Loggen Sie keine Patientendaten
- Beachten Sie DSGVO/Datenschutz-Anforderungen

## 🤝 Mitwirken

Contributions sind willkommen! Bitte:

1. Forken Sie das Repository
2. Erstellen Sie einen Feature Branch (`git checkout -b feature/AmazingFeature`)
3. Committen Sie Ihre Änderungen (`git commit -m 'Add AmazingFeature'`)
4. Pushen Sie zum Branch (`git push origin feature/AmazingFeature`)
5. Öffnen Sie einen Pull Request

## 📄 Lizenz

Dieses Projekt steht unter der Apache License 2.0 - siehe LICENSE Datei für Details.

## 🔗 Links

- [gematik](https://www.gematik.de/)
- [gematik FHIR Simplifier](https://simplifier.net/epa-medication)
- [HAPI FHIR](https://hapifhir.io/)
- [HL7 FHIR R4](https://hl7.org/fhir/R4/)
- [Spring Boot](https://spring.io/projects/spring-boot)

## 📧 Kontakt

Bei Fragen oder Problemen öffnen Sie bitte ein Issue auf GitHub.

---

**Hinweis:** Dieses Projekt ist eine Implementierung für Entwicklungs- und Testzwecke. Für den produktiven Einsatz im Gesundheitswesen sind zusätzliche Zertifizierungen und Tests erforderlich.

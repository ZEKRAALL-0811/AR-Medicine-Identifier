# DECISIONS.md — Permanent Project Decisions and Rationale

> **Purpose:** Preserve durable project choices so Anti-Gravity does not silently reverse them in later sessions.
>
> **Ownership:** Anti-Gravity should maintain this file when an explicit project decision is made or when a decision is formally changed. Do not invent decisions, claim that an option was tested when it was not, or rewrite history to make the current implementation appear intentional.
>
> **Relationship to MEMORY.md:** `MEMORY.md` tracks current implementation state and next steps. `DECISIONS.md` tracks durable choices and why they were made. Update them for different purposes; do not use one as a substitute for the other.

## 1. Rules for Anti-Gravity

1. Read this file before making architectural, framework, dependency, storage, privacy, navigation, or scope decisions.
2. Treat decisions marked **Accepted** as the current project baseline.
3. Do not reverse or replace an accepted decision just because another library or architecture is fashionable, easier to generate, or suggested by an AI tool.
4. If a decision no longer works, first gather evidence from the repository, build output, dependency compatibility, or target-device testing. Explain the specific problem and propose the smallest viable change.
5. Do not silently change a decision. Record the proposed change, reason, impact, and migration steps in the **Decision Change Log**. Ask the user when the change would alter scope, user-visible behaviour, data handling, or a previously explicit preference.
6. Update this file when the user makes a new durable decision or explicitly approves a proposed change. Routine implementation details do not need to become permanent decisions.
7. Keep the rationale factual. Distinguish user requirements, technical constraints, and unverified assumptions.
8. When a decision is superseded, mark it **Superseded** and link to the replacement. Do not delete the old rationale.
9. At the beginning of a session, compare the actual repository against the decisions below. Report mismatches instead of silently rewriting the decisions to match accidental code.
10. This file is a decision record, not proof that features have been implemented or tested.

## 2. Project Context

- **Project:** SLAM-Based AR Medicine Identifier with Voice-Guided Accessibility Mode.
- **Purpose:** Academic proof of concept that reads printed medicine packaging using on-device OCR, compares extracted fields against a small local dataset, and presents accessible text/speech output with an optional simple AR overlay.
- **Project type:** Working academic prototype, not production-ready software or a clinically validated medical product.
- **Target device:** Samsung Galaxy F13.
- **Target deadline:** One week; prioritise a narrow end-to-end demonstration over advanced features.
- **Source documents:** PRD, TRD, App Flow, UI/UX Design Brief, Backend Schema, and `MEMORY.md`. Verify that these files are actually available in the project repository before relying on them.

## 3. Accepted Decisions

### D-001 — Build a native Android application

**Status:** Accepted  
**Decision:** Use native Android development rather than a web app or cross-platform app for the prototype.  
**Rationale:** The project depends on phone camera access, Android text-to-speech, accessibility services, and potentially ARCore. Native Android provides direct access to these capabilities and matches the chosen target device.  
**Constraint:** Do not introduce a web frontend or a separate backend solely for this prototype.

### D-002 — Use Kotlin

**Status:** Accepted  
**Decision:** Use Kotlin as the primary application language.  
**Rationale:** It is the selected language for the native Android stack and integrates with Android and Jetpack libraries.  
**Constraint:** Do not mix in Java or another language unless a specific dependency or existing project constraint makes it necessary and the change is documented.

### D-003 — Use Jetpack Compose for the UI

**Status:** Accepted  
**Decision:** Use Jetpack Compose for the standard application screens and accessible 2D results.  
**Rationale:** It supports a concise native UI implementation and makes it practical to build and iterate on screens within the one-week deadline.  
**Constraint:** AR-rendered text must not be the only way to access information; mirror essential content in normal accessible UI.

### D-004 — Keep the prototype offline-first and without a backend

**Status:** Accepted  
**Decision:** No backend server, remote database, user accounts, authentication provider, or HTTP API in the prototype.  
**Rationale:** The core workflow can run locally, and avoiding server infrastructure reduces implementation time, network dependency, credential handling, and privacy risk.  
**Constraint:** Do not add Firebase, Supabase, or another backend unless the user explicitly changes scope.

### D-005 — Start with a bundled local JSON dataset

**Status:** Accepted  
**Decision:** Store the initial medicine reference dataset in `app/src/main/assets/medicines.json`, with approximately 20–30 curated academic test records.  
**Rationale:** JSON is sufficient for a small read-only dataset and is faster to implement than a database service or complex persistence layer.  
**Constraint:** Use stable record IDs and source metadata. Do not put package-specific expiry dates in general medicine records.  
**Upgrade path:** SQLite/Room may be added for local scan history or structured persistence only if needed and time permits; it does not imply adding a remote database.

### D-006 — Use on-device ML Kit Text Recognition v2 for OCR

**Status:** Accepted  
**Decision:** Use ML Kit Text Recognition v2 as the initial OCR implementation, with on-device processing.  
**Rationale:** It provides an achievable OCR baseline without a custom model or cloud service.  
**Constraint:** OCR extracts text; it does not verify medicine identity or validate medical facts. Keep matching and safety gating as separate logic. Verify dependency and model behaviour in the actual project.

### D-007 — Use Android TextToSpeech for spoken output

**Status:** Accepted  
**Decision:** Use Android TextToSpeech for scan guidance and result summaries.  
**Rationale:** It avoids requiring a paid or remote speech service and fits the accessibility goal.  
**Constraint:** Visual text must remain available if speech is unavailable. Avoid overlapping utterances and provide repeat/stop behaviour where implemented.

### D-008 — ARCore is conditional, not allowed to block the prototype

**Status:** Accepted  
**Decision:** Use ARCore for a simple overlay only if it is compatible with the Samsung Galaxy F13 setup and can be implemented reliably within the deadline. A 2D accessible result screen is the required fallback.  
**Rationale:** AR is central to the project concept, but device compatibility and a one-week deadline make advanced tracking a risk. The core OCR-to-result demonstration should remain usable without AR.  
**Constraint:** Do not claim ARCore support or stable tracking until verified on the actual device. Do not build elaborate 3D effects before the core flow works.

### D-009 — No barcode, QR, or DataMatrix scanning in the MVP

**Status:** Accepted  
**Decision:** Identify packages from printed label text using OCR; exclude barcode scanning from the prototype.  
**Rationale:** The user explicitly excluded barcode scanning from the MVP. Keeping it out protects the deadline and avoids expanding the recognition scope.  
**Constraint:** Do not add barcode scanning as a fallback without explicit user approval.

### D-010 — No cloud OCR, cloud AI, or cloud fallback

**Status:** Accepted  
**Decision:** Do not upload package images or OCR text to remote services.  
**Rationale:** The prototype is offline-first, avoids credentials and network dependency, and reduces privacy risk.  
**Constraint:** Do not add a vision API, generative AI service, or remote fallback unless the user explicitly approves a scope change and the privacy implications are documented.

### D-011 — Treat medicine matching conservatively

**Status:** Accepted  
**Decision:** Keep OCR, field extraction, local candidate matching, and the confidence/verification gate as separate steps. If evidence is weak or contradictory, ask for confirmation or report an unknown result.  
**Rationale:** OCR errors and look-alike medicine names can cause unsafe false matches. A smaller honest prototype is preferable to a confident but unsupported result.  
**Constraint:** Do not rely on fuzzy name similarity alone. Verify strength and composition when available. A match score is not a calibrated probability unless evaluated as such.

### D-012 — Never invent medical information

**Status:** Accepted  
**Decision:** Present only medicine facts supported by recorded sources in the local dataset. Omit unsupported purpose, directions, warnings, and side-effect fields. Do not generate personalised dosing or treatment advice.  
**Rationale:** This is an academic prototype without formal clinical validation or an appointed medical reviewer.  
**Constraint:** Clearly identify the prototype as educational and not a substitute for a pharmacist or doctor. Do not claim that a package is authentic, safe, or appropriate for a specific person.

### D-013 — Expiry is a property of the scanned package

**Status:** Accepted  
**Decision:** Read expiry from the physical package via OCR and store it in the transient scan result or optional scan record, not in the general medicine catalogue entry.  
**Rationale:** Different packages of the same product can have different expiry dates and batch numbers.  
**Constraint:** If the date is ambiguous or unreadable, report it as unknown. Never infer that the package is in date.

### D-014 — Accessibility is a core requirement

**Status:** Accepted  
**Decision:** Keep result information available as normal UI text, support TalkBack labels, use readable scalable text and large touch targets, and provide speech where available.  
**Rationale:** Older adults and blind/low-vision users are primary target personas.  
**Constraint:** Do not make colour, haptics, sound, or an AR overlay the only way to access essential information. Do not auto-dismiss important results.

### D-015 — Use a minimal, high-contrast Android utility design

**Status:** Accepted  
**Decision:** Use the visual tokens and component guidance in `AR_Medicine_Identifier_UI_UX_Design_Brief.md`, including a restrained blue/teal palette, system sans-serif/Roboto, generous touch targets, and subtle surfaces.  
**Rationale:** A calm, consistent design improves readability and reduces cognitive load for an accessibility-focused tool.  
**Constraint:** Centralise design tokens rather than scattering arbitrary colours and dimensions across screens. Verify contrast rather than assuming the token values automatically meet requirements.

### D-016 — Keep the one-week prototype scope narrow

**Status:** Accepted  
**Decision:** Prioritise camera/capture → OCR → field extraction → local match/confidence gate → accessible result.  
**Rationale:** This is the smallest end-to-end flow that demonstrates the product concept within the user's deadline.  
**Constraint:** Multi-frame fusion, custom object detection, complex AR anchoring, haptic guidance, scan history, expiry reminders, multilingual support, and advanced settings are optional unless the core flow is stable and time remains.

### D-017 — Android minimum version is a provisional baseline

**Status:** Accepted with verification required  
**Decision:** Begin with Android 8.0 (API 26) as the planning baseline.  
**Rationale:** It is the baseline stated in the technical plan, but actual dependency requirements and device compatibility must be checked.  
**Constraint:** Verify the target handset's Android version and selected library requirements. If a higher `minSdk` is required, document the evidence and proposed change rather than changing it silently.

### D-018 — No production or clinical-readiness claims

**Status:** Accepted  
**Decision:** Treat the app as an academic proof of concept.  
**Rationale:** The deadline, small dataset, and absence of formal medical validation do not support production-readiness claims.  
**Constraint:** Do not claim measured accuracy, clinical validation, certification, or general medicine coverage without documented evidence.

### D-019 — Toolchain and build system versions for Phase 1 baseline

**Status:** Accepted  
**Decision:** Use Gradle 8.11.1 (via project Gradle Wrapper), Android Gradle Plugin (AGP) 8.7.3, Kotlin 2.0.21 (with official Jetpack Compose compiler Gradle plugin `org.jetbrains.kotlin.plugin.compose`), and OpenJDK 21 (Android Studio bundled JBR 21.0.10). Configure `compileSdk = 35`, `targetSdk = 35`, `minSdk = 26`, and Java 21 compilation compatibility.  
**Rationale:** Confirmed full compatibility with the host laptop environment. The project compiles cleanly, runs unit tests, and packages `app-debug.apk` without errors. Setting `minSdk = 26` maintains the approved Android 8.0 baseline.  
**Constraint:** Do not upgrade or downgrade AGP or Gradle without verifying compatibility with Android Studio's JBR 21.

### D-020 — Use kotlinx.serialization for local dataset parsing and schema validation

**Status:** Accepted  
**Decision:** Use official Kotlin multiplatform serialization (`org.jetbrains.kotlinx:kotlinx-serialization-json:1.7.3`) with compile-time plugin `org.jetbrains.kotlin.plugin.serialization` for parsing and validating `medicines.json`.  
**Rationale:** Ensures compile-time typesafety without reflection overhead. Executes natively in standard JVM unit tests without needing Android framework mocks (unlike `org.json`). Allows strict validation of required fields (`id`, `sourceTitle`, `datasetVersion`) and safe handling of optional/unknown attributes.  
**Constraint:** Keep dataset parsing offline and self-contained; do not introduce remote network parsers or cloud schemas.

### D-021 — Use connected Redmi Note 11 (Android 13 / API 33) as temporary development and testing device

**Status:** Accepted  
**Decision:** Use connected Redmi Note 11 (Model 2201117TI, Android 13, API 33) for interim development and physical-device verification while primary target Samsung Galaxy F13 is unavailable.  
**Rationale:** Explicit user authorization; device physically connected via USB debugging with verified installation and hardware camera access. Local emulator is blocked by disabled CPU virtualization in host BIOS.  
**Constraint:** Retain minSdk 26 compatibility for primary target baseline.

### D-022 — Standardize on CameraX 1.4.1 with on-device in-memory image capture

**Status:** Accepted  
**Decision:** Use `androidx.camera:camera-core:1.4.1`, `camera-camera2:1.4.1`, `camera-lifecycle:1.4.1`, `camera-view:1.4.1` with `ProcessCameraProvider` and `AndroidView(PreviewView)`. Frames captured on-device with immediate `ImageProxy.close()`.  
**Rationale:** Robust compatibility with AGP 8.7.3, Compose BOM 2024.11.00, compileSdk 35, and hardware acceleration on physical Redmi Note 11 (Android 13 / API 33).  
**Constraint:** Never store raw camera frames to persistent storage or upload them; close ImageProxy immediately.

### D-023 — Standardize on ML Kit Text Recognition v2 (16.0.1) for on-device OCR pipeline

**Status:** Accepted  
**Decision:** Use `com.google.mlkit:text-recognition:16.0.1` (Latin script) with `InputImage.fromMediaImage` and lifecycle-managed `TextRecognizer` instance.  
**Rationale:** Runs completely on-device with zero network permissions (`android.permission.INTERNET` absent from manifest). Zero cloud model download dependency. Successfully verified on physical Redmi Note 11 hardware with real medicine packages.  
**Constraint:** OCR output is transient in memory. Bounding boxes, lines, and blocks are extracted into pure Kotlin domain models (`OcrResult`, `OcrBlock`, `OcrLine`) to decouple OCR recognition from UI and matching logic.

## 4. Explicitly Unresolved / Must Be Verified

These items are not settled facts and must not be silently treated as verified:

- Whether the exact Samsung Galaxy F13 model/software setup supports the selected ARCore implementation.
- Whether the selected ML Kit dependency/model works correctly with the project's final Android configuration (RESOLVED in Phase 4: verified with on-device `com.google.mlkit:text-recognition:16.0.1` on physical hardware).
- The final Android `minSdk` after checking the real dependency graph and device (API 26 confirmed for Phase 1 baseline).
- The final list of 20–30 medicine test records and the source/licence for each fact (RESOLVED in Phase 2: 25 curated records with full provenance in `docs/dataset-sources.md`).
- Actual OCR accuracy, matching accuracy, latency, battery usage, and AR stability (OCR baseline verified in Phase 4; matching and AR remain to be evaluated).
- Whether local scan history, Room, WorkManager reminders, or automatic point-and-hold scanning fit the remaining time.
- Whether any optional AR feature has been tested on the physical target device.

Anti-Gravity should move an item out of this section only after it is resolved by evidence or an explicit user decision.

## 5. Decision Change Log

Append new changes at the top. Do not delete superseded decisions.

### 2026-10-17 — D-023 — Standardize on ML Kit Text Recognition v2 (16.0.1) for on-device OCR pipeline
- Previous decision: Provisional ML Kit Text Recognition v2 planned without pinned version.
- Proposed replacement: Use `com.google.mlkit:text-recognition:16.0.1` with `InputImage.fromMediaImage` and pure domain mapping (`OcrResult`).
- Reason / evidence: Zero network permissions, fully on-device, verified against real medicine packaging on physical Redmi Note 11 hardware.
- User approval required: No (implements approved D-006).
- User approval/status: Accepted.
- Files and architecture affected: `gradle/libs.versions.toml`, `app/build.gradle.kts`, `OcrProcessor.kt`, `MlKitOcrProcessor.kt`, `OcrResult.kt`, `LiveScanScreen.kt`, `ReviewOcrScreen.kt`.
- Migration or cleanup required: None.
- Verification performed: Unit tests in `OcrPipelineUnitTest` (7/7 passed), debug APK built (55.2 MB), physically verified on Redmi Note 11 with Omeprazole 20mg and Calcium + D3 packages.
- New status: Accepted.

### 2026-10-17 — D-022 — Standardize on CameraX 1.4.1 with on-device in-memory image capture
- Previous decision: Provisional CameraX dependency planned without exact version.
- Proposed replacement: Use `androidx.camera:camera-core:1.4.1`, `camera-camera2:1.4.1`, `camera-lifecycle:1.4.1`, `camera-view:1.4.1` with `ProcessCameraProvider` and `AndroidView(PreviewView)`. Frames captured on-device with immediate `ImageProxy.close()`.
- Reason / evidence: Robust compatibility with AGP 8.7.3, Compose BOM 2024.11.00, compileSdk 35, and hardware acceleration on physical Redmi Note 11 (Android 13 / API 33).
- User approval required: No (aligns with TRD and PRD).
- User approval/status: Accepted.
- Files and architecture affected: `gradle/libs.versions.toml`, `app/build.gradle.kts`, `app/src/main/AndroidManifest.xml`, `CameraCaptureManager.kt`, `CameraPreviewView.kt`, `LiveScanScreen.kt`.
- Migration or cleanup required: None.
- Verification performed: Verified live camera preview and 2448x3264 photo capture on physical Redmi Note 11 device.
- New status: Accepted.

### 2026-10-17 — D-021 — Use connected Redmi Note 11 (Android 13 / API 33) as temporary development and testing device
- Previous decision: Samsung Galaxy F13 specified as target hardware; local emulator blocked by BIOS virtualization.
- Proposed replacement: Use connected Redmi Note 11 (Model 2201117TI, Android 13, API 33) for interim development and physical-device verification without waiting for Samsung phone.
- Reason / evidence: Explicit user authorization; device physically connected via USB debugging with verified installation and hardware camera access.
- User approval required: Yes (directed by user).
- User approval/status: Accepted.
- Files and architecture affected: Development and verification workflows in `MEMORY.md` and `TASKS.md`.
- Migration or cleanup required: None.
- Verification performed: `adb devices` showed `79145af9 device`, installed debug APK, launched app, verified camera stream.
- New status: Accepted.

### 2026-10-17 — D-020 — Use kotlinx.serialization for local dataset parsing and schema validation
- Previous decision: Generic JSON parsing specified in Backend Schema without concrete library decision.
- Proposed replacement: Standardize on `kotlinx-serialization-json:1.7.3`.
- Reason / evidence: Allows strict typesafe validation, zero reflection, and pure JVM unit testing of bundled `medicines.json`.
- User approval required: No (implementation detail aligning with D-005 and Backend Schema).
- User approval/status: Accepted.
- Files and architecture affected: `app/build.gradle.kts`, `gradle/libs.versions.toml`, `MedicineRecord.kt`, `JsonMedicineParser.kt`.
- Migration or cleanup required: None.
- Verification performed: Unit tests in `MedicineDataValidationTest` passed in `./gradlew test`.
- New status: Accepted.

### 2026-10-17 — D-019 — Toolchain and build system versions for Phase 1 baseline
- Previous decision: Provisional planning versions without concrete build toolchain specifications.
- Proposed replacement: Standardize on Gradle 8.11.1 Wrapper, AGP 8.7.3, Kotlin 2.0.21, Compose Compiler Plugin, JBR 21.0.10, compileSdk 35, minSdk 26.
- Reason / evidence: Verified full clean compilation and unit test execution on Windows 11 host.
- User approval required: Yes (approved as part of Phase 1 authorization).
- User approval/status: Accepted.
- Files and architecture affected: Root build.gradle.kts, app/build.gradle.kts, settings.gradle.kts, gradle/libs.versions.toml, gradle/wrapper.
- Migration or cleanup required: None; initial setup.
- Verification performed: `./gradlew test` (45 tasks executed) and `./gradlew assembleDebug` (35 tasks executed).
- New status: Accepted.


### Change entry template

```text
### YYYY-MM-DD — D-XXX — Short title
- Previous decision:
- Proposed replacement:
- Reason / evidence:
- User approval required: Yes / No
- User approval/status:
- Files and architecture affected:
- Migration or cleanup required:
- Verification performed:
- New status: Accepted / Rejected / Superseded
```

## 6. Session-start Checklist for Anti-Gravity

- [ ] Read `DECISIONS.md` and `MEMORY.md`.
- [ ] Inspect the actual repository and identify deviations from accepted decisions.
- [ ] Do not add a backend, authentication, cloud service, barcode scanning, or unrelated feature without approval.
- [ ] Check device/library compatibility before committing to AR-specific work.
- [ ] Preserve the core accessible 2D path.
- [ ] If a permanent choice must change, document the evidence and ask for approval when the change affects scope, privacy, or user-visible behaviour.

## 7. Final Rule

**Accepted decisions are sticky, not immutable.** Preserve them by default. Change them only when the user explicitly directs the change or verified technical evidence makes the current choice infeasible. Record why, what changed, and how it was verified. Never silently reverse a decision.

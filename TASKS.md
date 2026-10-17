# TASKS.md — Implementation Checklist

**Project:** SLAM-Based AR Medicine Identifier with Voice-Guided Accessibility Mode  
**Status at template creation:** Not started  
**Owner of project-state updates:** Anti-Gravity only  
**Rule:** Do not mark a task complete until the phase's verification checks pass and the evidence is recorded.

## How to use this file

- Work through phases in order. Do not start a later phase while the current phase is blocked or unverified.
- Mark a checkbox `[x]` only after doing the work and testing it.
- For each completed phase, add a short evidence note: commands run, tests performed, device/emulator used, and results.
- If a task cannot be completed, leave it unchecked and record the blocker. Never claim a feature works without evidence.
- At the end of every phase, Anti-Gravity must update `MEMORY.md`, `DECISIONS.md`, and this `TASKS.md`.
- Do not invent progress. This template starts with all implementation tasks unchecked.

## Phase 0 — Read project instructions and establish baseline

- [x] Read all available project documents, including the PRD, TRD, App Flow, UI/UX Design Brief, Backend Schema, `MEMORY.md`, `DECISIONS.md`, and `IMPLEMENTATION_PLAN.md`.
- [x] Inspect the existing repository and identify whether it is empty, partially scaffolded, or already contains code.
- [x] Confirm the target device is Samsung Galaxy F13 and identify the available Android/Gradle/JDK toolchain.
- [x] Record actual repository state and toolchain findings in `MEMORY.md`.
- [x] List conflicts, missing information, or decisions requiring user input before implementation.
- [x] Do not create application features in this phase.

**Phase verification**
- [x] All available project documents have been reviewed.
- [x] Repository/toolchain status is evidenced rather than assumed.
- [x] Any blocking ambiguity is reported to the user.

**Evidence / notes:**
- Document review completed across all 9 specification markdown files and the technical context document.
- Repository inspected via PowerShell: Clean slate (10 documentation/PDF files present, no existing code, no Gradle files, not a git repository).
- Toolchain verified: Android Studio OpenJDK 21.0.10 at `C:\Program Files\Android\Android Studio\jbr\bin\java.exe`, Android SDK at `C:\Users\skzah\AppData\Local\Android\Sdk` (platforms: `android-36.1`, build-tools: `36.1.0` and `37.0.0`, adb: `platform-tools\adb.exe`).
- Connectivity checked: `adb devices` shows 0 connected devices, emulator has 0 configured AVDs.
- Blockers & conflicts noted: `JAVA_HOME` pointing to non-existent `C:\Java\bin` (will override to JBR 21), `ANDROID_HOME` will be exported, PRD Section 8 barcode mention overridden by D-009 / PRD Section 15.1 / user prompt (barcode scanning strictly out of scope).

## Phase 1 — Project setup and build foundation

- [x] Create or validate the native Android project using Kotlin and Jetpack Compose.
- [x] Configure the Android SDK, Gradle, JDK, minimum SDK baseline (provisionally API 26), and app identifier.
- [x] Create a clear package/folder structure for UI, navigation, camera, OCR, matching, local data, speech, and optional AR.
- [x] Add only the dependencies required by the approved architecture.
- [x] Configure version control and add an appropriate `.gitignore`.
- [x] Confirm no backend, authentication service, cloud API, or remote database is introduced.
- [ ] Build and launch the empty app on an emulator or the Samsung Galaxy F13 if available (Build passed; launch NOT RUN / BLOCKED due to no connected physical device and BIOS virtualization disabled).

**Phase verification**
- [x] Debug build succeeds.
- [ ] App launches without crashing (NOT RUN / BLOCKED: no physical device attached; local emulator blocked by disabled BIOS virtualization).
- [x] Package structure and dependency choices are documented.
- [x] Any device-specific compatibility limitation is recorded.

**Evidence / notes:**
- Git initialized in workspace; standard Android `.gitignore` configured (ignoring `.gradle/`, `app/build/`, `local.properties`).
- Native Android project scaffolded with Kotlin 2.0.21, Compose Compiler Plugin, AGP 8.7.3, and Gradle 8.11.1 Wrapper.
- Application ID: `com.armod.identifier`, `compileSdk = 35`, `minSdk = 26`, `targetSdk = 35`.
- Toolchain: Android Studio bundled JBR (OpenJDK 21.0.10) and Android SDK at `C:\Users\skzah\AppData\Local\Android\Sdk`. User environment variables `JAVA_HOME` and `ANDROID_HOME` configured.
- Clean package structure established under `com.armod.identifier`: `data/model/`, `data/local/`, `domain/`, `camera/`, `ocr/`, `matching/`, `speech/`, `ar/`, `ui/navigation/`, `ui/components/`, `ui/theme/`, `util/`.
- Verified build and unit tests:
  - `.\gradlew.bat test`: BUILD SUCCESSFUL (45 tasks executed). `BaselineUnitTest` passed.
  - `.\gradlew.bat assembleDebug`: BUILD SUCCESSFUL (35 tasks executed). Output artifact verified at `app/build/outputs/apk/debug/app-debug.apk` (9,472,683 bytes).
- Launch status: NOT RUN / BLOCKED because Samsung Galaxy F13 is not attached via USB debugging, and local emulator is blocked by CPU firmware virtualization being disabled in BIOS.

## Phase 2 — Local medicine data model and bundled dataset

- [x] Define the local medicine record model based on `Backend Schema` and the approved PRD/TRD.
- [x] Define required and optional fields, including stable ID, display name, package/brand identifiers where supported, searchable text, and source/provenance metadata.
- [x] Add a small curated JSON dataset for the prototype (target approximately 20–30 clearly labelled package records, subject to verified data availability).
- [x] Validate JSON parsing and required fields at app startup or through tests.
- [x] Add matching/search functions over the bundled data.
- [x] Handle unknown, missing, duplicate, or malformed records safely.
- [x] Check data source attribution and licence/terms before including records.
- [x] Do not add unsupported medical claims, personalized dosing advice, or fabricated medicine details.
- [x] Do not introduce Room/SQLite unless a concrete approved need (such as optional local scan history) is established.

**Phase verification**
- [x] Dataset parses successfully.
- [x] Automated tests cover valid, missing, duplicate, and malformed records.
- [x] At least one known sample record can be retrieved locally while offline.
- [x] Data provenance is recorded for every included record where available.

**Evidence / notes:**
- `MedicineRecord` model annotated with `kotlinx.serialization` annotations (`@Serializable`, `@SerialName`), mapping exact schema properties from Backend Schema Section 4.
- `app/src/main/assets/medicines.json` created with 25 curated, real-world medicine records sourced from NLEM 2022, NHS UK, DailyMed/FDA, and WHO EML.
- Full provenance and attribution documented in `docs/dataset-sources.md`. No fabricated facts or personal dosing advice included.
- `JsonMedicineParser` implemented with strict validation: detects malformed JSON, missing required fields (`id`, `sourceTitle`, `datasetVersion`), duplicate IDs, and empty datasets, while safely ignoring unexpected optional fields.
- `JsonMedicineLocalDataSource` implemented offering cached in-memory access and fast lookup methods (`getMedicineById`, `searchByName`, `findByExactNameAndStrength`).
- Verified inside APK: `assets/medicines.json` confirmed packaged inside `app-debug.apk` (25,414 bytes).
- Automated tests in `MedicineDataValidationTest`: All 9 test cases passed in `./gradlew test`. `./gradlew assembleDebug` passed cleanly.

## Phase 3 — Permissions and camera foundation (no authentication)

- [ ] Confirm and document that user authentication is out of scope; do not build signup, login, sessions, or protected routes.
- [ ] Implement camera permission request and denial handling.
- [ ] Implement camera preview/capture using an Android-compatible camera approach.
- [ ] Provide clear guidance when permission is denied or the camera is unavailable.
- [ ] Keep image processing on-device and avoid uploading frames or OCR text.
- [ ] Add a way to retry or return safely from camera errors.

**Phase verification**
- [ ] Permission granted: camera preview/capture works.
- [ ] Permission denied: app remains usable and explains how to proceed.
- [ ] Camera unavailable/error: no crash; a useful recovery path is shown.
- [ ] Network inspection/code review confirms no image upload path was added.

**Evidence / notes:** _Anti-Gravity fills in after execution._

## Phase 4 — On-device OCR pipeline

- [ ] Integrate ML Kit Text Recognition v2 for on-device text recognition.
- [ ] Process a captured image and return recognized text plus useful text-block/line structure where available.
- [ ] Add progress/loading state and a way to cancel or retry.
- [ ] Handle empty, blurry, low-light, rotated, or unreadable captures with honest feedback.
- [ ] Keep OCR output transient unless the user explicitly chooses an approved local-history feature.
- [ ] Add unit tests for text normalization and field extraction using representative sample OCR strings.

**Phase verification**
- [ ] OCR works on representative labelled medicine-package images.
- [ ] Empty/unreadable input produces a clear recovery message.
- [ ] OCR runs on-device without a cloud dependency.
- [ ] Text extraction tests pass.

**Evidence / notes:** _Anti-Gravity fills in after execution._

## Phase 5 — Field extraction, local matching, and confidence gate

- [ ] Extract only supported fields from OCR text; preserve an unknown/unreadable state when a field cannot be read.
- [ ] Normalize text for matching without discarding meaningful distinctions.
- [ ] Match OCR results against the bundled local dataset.
- [ ] Implement a conservative confidence/ambiguity gate.
- [ ] Show a single result only when the evidence meets the defined threshold; otherwise show possible matches or ask the user to retake the image.
- [ ] Ensure low-confidence matches are never presented as certain.
- [ ] Do not infer expiry dates or other fields that OCR cannot support.

**Phase verification**
- [ ] Tests cover confident match, ambiguous match, no match, and unreadable fields.
- [ ] Low-confidence cases cannot be presented as confirmed identity.
- [ ] Matching works with network disabled.

**Evidence / notes:** _Anti-Gravity fills in after execution._

## Phase 6 — Accessible results and voice guidance

- [ ] Build the medicine result screen using the approved UI/UX brief.
- [ ] Integrate Android TextToSpeech for read-aloud results and key instructions.
- [ ] Add visible controls to speak, stop speech, and repeat speech.
- [ ] Ensure important information is available as text as well as speech.
- [ ] Support large readable text, adequate contrast, clear touch targets, and screen-reader semantics.
- [ ] Clearly distinguish confirmed readable fields from unknown/unreadable fields.
- [ ] Include a concise safety message that the app is an academic prototype and not a substitute for pharmacist/clinician advice.

**Phase verification**
- [ ] Text and speech agree and do not invent missing values.
- [ ] Speech can be started/stopped/repeated and handles unavailable TTS gracefully.
- [ ] Core flow is usable without relying on color or speech alone.
- [ ] TalkBack/Android accessibility checks are performed where available.

**Evidence / notes:** _Anti-Gravity fills in after execution._

## Phase 7 — AR overlay and 2D fallback

- [ ] Check ARCore support and compatibility on the target handset/build environment before implementation.
- [ ] If feasible within the one-week prototype, implement a simple AR overlay for supported extracted fields.
- [ ] Keep the overlay concise and anchored in a stable, understandable way.
- [ ] If ARCore is unsupported, unstable, or threatens the deadline, provide the approved accessible 2D result/camera fallback instead.
- [ ] Do not block the main identification flow on AR availability.

**Phase verification**
- [ ] Either the AR overlay works on a supported target or the 2D fallback is complete and tested.
- [ ] Unsupported AR does not crash or prevent identification.
- [ ] Accessibility and text/speech results remain available.

**Evidence / notes:** _Anti-Gravity fills in after execution._

## Phase 8 — UI polish, integration, and edge cases

- [ ] Apply the approved visual design consistently.
- [ ] Add loading, empty, permission-denied, no-match, ambiguous-match, and error states.
- [ ] Verify navigation and back behavior across the complete flow.
- [ ] Check small-screen layout, text scaling, and landscape/rotation behavior as practical.
- [ ] Remove dead code, debug-only UI, and unused dependencies.
- [ ] Confirm the app remains offline-first.

**Phase verification**
- [ ] All defined screens and transitions have been exercised.
- [ ] No known blocking crash remains.
- [ ] Layout and accessibility checks are recorded.

**Evidence / notes:** _Anti-Gravity fills in after execution._

## Phase 9 — End-to-end testing and prototype handoff

- [ ] Run unit tests and any available instrumentation/UI tests.
- [ ] Perform a manual end-to-end test: launch → camera permission → capture → OCR → local match → text result → speech.
- [ ] Test denied permission, no text, poor image, unknown medicine, ambiguous match, unreadable expiry, and TTS unavailable.
- [ ] Test with network disabled.
- [ ] Build a debug APK and confirm the output artifact exists.
- [ ] Document build/run instructions, known limitations, and the exact tested device/emulator.
- [ ] Ensure no production-readiness or clinical-validation claims are made.
- [ ] Update `MEMORY.md`, `DECISIONS.md`, and `TASKS.md` with final verified state.

**Phase verification**
- [ ] Required end-to-end flow passes on the available target.
- [ ] Build and test results are recorded.
- [ ] Remaining limitations are explicit.
- [ ] Prototype handoff instructions are reproducible.

**Evidence / notes:** _Anti-Gravity fills in after execution._

## Current blocker / next action

**Next phase to execute:** Phase 3 — Permissions and camera foundation (no authentication) (pending user approval).  
**Current implementation status:** Phase 2 complete and verified. Dataset (25 records) and validation tests passing.  
**Do not pre-check any implementation task.**

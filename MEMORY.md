# MEMORY.md — Anti-Gravity Project Continuity Rules

> **Purpose:** This file defines how Anti-Gravity must maintain project continuity while implementing the AR Medicine Identifier. It is a set of instructions for the coding agent, not a pre-filled status report.
>
> **Ownership rule:** Anti-Gravity is the only agent that should update the project state in this file during implementation. Do not treat the initial contents as evidence that a task has been completed. Do not fill in current-state claims until you have inspected the repository and verified them.

## 1. Mandatory Update Rule

At the start of every implementation session, before making code changes:

1. Read this `MEMORY.md` completely.
2. Inspect the current repository, Git status if available, project files, build configuration, tests, and recent changes.
3. Verify the actual implementation state instead of relying only on this file.
4. Update the **Current State** section below with the verified state before proceeding.
5. Preserve useful history and decisions. Do not erase unresolved issues merely to make the project look complete.

At the end of every implementation session, and whenever a meaningful milestone is completed:

1. Update this file to reflect what was actually changed in the repository.
2. Record which build, test, or device checks were actually run and their outcomes.
3. Record errors, incomplete work, regressions, assumptions, and blockers.
4. Write the single most useful next action so the next session can continue without rediscovering context.
5. Ensure this file agrees with the code. If the code and this file disagree, inspect the code and correct the file.

**Anti-Gravity must update this file itself.** The human user should not need to manually maintain implementation status. Do not ask another assistant to fabricate progress updates. Never claim to have edited or tested the project unless that action actually happened.

## 2. Source-of-Truth Order

When sources disagree, use this order:

1. Current repository contents and verified build/test results.
2. The latest explicit user decisions.
3. Project specification documents in the repository.
4. Previous notes in this file.
5. Agent assumptions.

Do not treat a plan, checklist, generated file, or proposed architecture as proof that it has been implemented. Mark items as planned until verified.

## 3. Project Baseline and Fixed Decisions

Project: **SLAM-Based AR Medicine Identifier with Voice-Guided Accessibility Mode**

Project type: **Academic proof of concept / working prototype**, not a production-ready medical application.

Target device: **Samsung Galaxy F13**.

Deadline: **One week**, so prioritise a narrow, demonstrable end-to-end flow.

Current intended technical direction, subject to repository verification:

- Native Android app.
- Kotlin and Jetpack Compose.
- On-device ML Kit Text Recognition v2 for OCR.
- Small bundled local JSON medicine dataset first; SQLite/Room only if needed for optional history.
- Android TextToSpeech for spoken guidance/results.
- ARCore overlay only if target-device compatibility and available time allow; maintain an accessible 2D fallback.
- No backend server, remote database, authentication, or cloud fallback in the prototype.
- No barcode, QR, or DataMatrix scanning in the MVP.
- No uploading camera frames or scan history.
- Start with a small, manually curated academic dataset (approximately 20–30 records) and preserve source information for supported facts.
- Never present an uncertain match as certain. Do not invent medical facts or provide personalised dosing/treatment advice.

These are project decisions, not proof that the corresponding features exist in code. If the user explicitly changes a decision, update this baseline and explain any affected documents or code.

## 4. Specification Documents

The project has been documented with these intended specification files:

1. `AR_Medicine_Identifier_PRD.md` — product requirements.
2. `AR_Medicine_Identifier_TRD.md` — technical requirements.
3. `AR_Medicine_Identifier_App_Flow.md` — screens, navigation, and user journeys.
4. `AR_Medicine_Identifier_UI_UX_Design_Brief.md` — visual and accessibility requirements.
5. `AR_Medicine_Identifier_Backend_Schema.md` — local data model and storage decisions.

At session start, verify which of these files are actually present in the repository and read the relevant ones before implementation. If any are missing, report that accurately; do not claim that they are part of the repository merely because they were created elsewhere.

## 5. Current State — Anti-Gravity Must Maintain This Section

**Last verified:** `2026-10-17 by Anti-Gravity (Phase 2 Local Medicine Data Model & Bundled Dataset)`

**Repository / branch:** `d:\ARVR Project (Git repo active, clean build, local dataset with 25 records and validation test suite)`

**Current milestone:** `Phase 2 — Local Medicine Data Model, Bundled JSON, Provenance, and Validation Complete & Verified`

**Implemented and verified:**
- Phases 0 & 1 foundations preserved and verified.
- `MedicineRecord` data model annotated with `@Serializable` and `@SerialName` properties matching Backend Schema Section 4.
- `medicines.json` created in `app/src/main/assets/` containing exactly 25 curated, real-world medicine-package records sourced from NLEM 2022, NHS UK, DailyMed FDA, and WHO EML.
- Provenance and attribution documented in `docs/dataset-sources.md`. Zero fabricated records or personalized dosing claims.
- `JsonMedicineParser` implemented with strict validation: detects malformed JSON, missing required fields (`id`, `sourceTitle`, `datasetVersion`), duplicate IDs, and empty datasets, while safely ignoring unexpected optional fields.
- `JsonMedicineLocalDataSource` implemented with cached in-memory retrieval and lookup methods (`getAllMedicines`, `getMedicineById`, `searchByName`, `findByExactNameAndStrength`).
- Packaged inside APK: `assets/medicines.json` (25,414 bytes) confirmed present in `app-debug.apk`.
- Automated tests: `MedicineDataValidationTest` (all 9 unit test cases passed) and `BaselineUnitTest` passed in `./gradlew test`.
- Build verification: `.\gradlew.bat assembleDebug` produced verified APK (9,725,842 bytes).

**Partially implemented:**
- App runtime launch: The debug APK was built successfully, but runtime launch on hardware remains unperformed / blocked because no physical Samsung Galaxy F13 is currently attached via USB debugging, and local Android emulator cannot run due to CPU virtualization being disabled in laptop BIOS.

**Not yet implemented:**
- Phase 3: Permissions and camera foundation (no authentication).
- Phases 4–9: OCR, field extraction, matching, speech, AR/2D fallback, integration, and final testing.

**Build and test status:**
- Build command/result: `.\gradlew.bat assembleDebug` -> BUILD SUCCESSFUL (artifact: `app/build/outputs/apk/debug/app-debug.apk`, 9,725,842 bytes)
- Unit tests/result: `.\gradlew.bat test` -> BUILD SUCCESSFUL (45 tasks executed; all unit tests passed)
- Instrumented tests/result: Not yet run (requires device)
- Samsung Galaxy F13 device check: Not connected via USB/ADB (`adb devices` list empty)
- ARCore compatibility check: Unverified on hardware; 2D accessible fallback architected

**Current task in progress:** `Phase 2 complete and build verified; awaiting user approval to begin Phase 3.`

**Known issues / blockers:**
- Hardware virtualization (VT-x) is disabled in laptop BIOS (`VirtualizationFirmwareEnabled: False`), preventing local Android emulator execution.
- Physical Samsung Galaxy F13 is not currently connected via USB. Physical device connection with USB debugging enabled is required to verify on-device runtime launch.

**Most recent change:** `Created medicines.json (25 records), JsonMedicineParser, JsonMedicineLocalDataSource, docs/dataset-sources.md, and MedicineDataValidationTest.`

**Next step:** `Obtain user approval for Phase 2 report, then proceed to Phase 3 (Permissions and camera foundation; no authentication).`

## 6. Session Log

### 2026-10-17 — Phase 2: Local Medicine Data Model, Bundled JSON, Provenance, and Validation
- Goal: Define local data model, bundle 25 traceable records in `medicines.json`, implement parser and data source with strict validation, verify via unit tests and build.
- Files changed: `app/src/main/java/com/armod/identifier/data/model/MedicineRecord.kt`, `app/src/main/java/com/armod/identifier/data/local/MedicineLocalDataSource.kt`, `app/src/main/java/com/armod/identifier/data/local/MedicineDataException.kt`, `app/src/main/java/com/armod/identifier/data/local/JsonMedicineParser.kt`, `app/src/main/java/com/armod/identifier/data/local/JsonMedicineLocalDataSource.kt`, `app/src/main/assets/medicines.json`, `docs/dataset-sources.md`, `app/src/test/java/com/armod/identifier/data/local/MedicineDataValidationTest.kt`, `app/build.gradle.kts`, `gradle/libs.versions.toml`, `MEMORY.md`, `DECISIONS.md`, `TASKS.md`.
- Implemented and verified: Added `kotlinx.serialization` dependency; updated `MedicineRecord`; created 25-record curated dataset in `medicines.json`; documented provenance in `docs/dataset-sources.md`; implemented parser and local data source; verified asset packaging inside `app-debug.apk`; passed all unit tests.
- Commands/builds/tests run and outcomes:
  - `.\gradlew.bat test`: BUILD SUCCESSFUL in 1m 28s (45 tasks executed). All 9 test cases in `MedicineDataValidationTest` passed.
  - `.\gradlew.bat assembleDebug`: BUILD SUCCESSFUL in 1m 10s (35 tasks executed). Generated APK: `app-debug.apk` (9,725,842 bytes).
  - APK asset verification: `assets/medicines.json` confirmed packaged inside `app-debug.apk` (25,414 bytes).
- Device checks performed: Device launch not performed (blocked: no device connected, emulator blocked by BIOS virtualization).
- Issues or blockers: Physical device connection required for runtime verification.
- Decisions made (and why): Accepted D-020 (using `kotlinx.serialization` for zero-reflection typesafe validation).
- Current task remaining: Wait for user approval of Phase 2 report.
- Next step: Phase 3 — Permissions and camera foundation (no authentication).

### 2026-10-17 — Phase 1: Project Setup and Build Foundation
- Goal: Audit environment, fix prerequisites, initialize Git, configure Gradle Wrapper, scaffold Kotlin + Jetpack Compose Android app, verify debug build and unit tests.
- Files changed: `.gitignore`, `local.properties`, `gradle.properties`, `settings.gradle.kts`, `build.gradle.kts`, `gradle/libs.versions.toml`, `gradle/wrapper/*`, `app/build.gradle.kts`, `app/proguard-rules.pro`, `app/src/main/AndroidManifest.xml`, `app/src/main/res/*`, `app/src/main/java/com/armod/identifier/*`, `app/src/test/java/com/armod/identifier/*`, `MEMORY.md`, `DECISIONS.md`, `TASKS.md`.
- Implemented and verified: Full audit of Windows 11 host (specs, storage, virtualization, JDK, SDK, adb); User-level `JAVA_HOME`, `ANDROID_HOME`, and `PATH` corrected; Git initialized; Gradle 8.11.1 Wrapper configured; Native Android Kotlin + Compose scaffold created; clean package structure established; baseline unit test executed and passed; debug APK built and verified.
- Commands/builds/tests run and outcomes:
  - System audit: Windows 11 64-bit, 7.75 GB RAM (1.14 GB free), i5-1135G7, Virtualization=False, 247 GB free on D:.
  - Environment configuration: Set User `JAVA_HOME` to JBR 21, User `ANDROID_HOME` to SDK path, added JBR bin to User PATH. Verified `java -version` (OpenJDK 21.0.10) and `adb version`.
  - `git init`: Initialized empty Git repository in `D:/ARVR Project/.git/`.
  - `.\gradlew.bat test`: BUILD SUCCESSFUL in 4m (45 tasks executed). `BaselineUnitTest` passed.
  - `.\gradlew.bat assembleDebug`: BUILD SUCCESSFUL in 1m 8s (35 tasks executed). Output artifact verified at `app/build/outputs/apk/debug/app-debug.apk` (9,472,683 bytes).
  - `adb devices`: Empty list (no physical device connected).
- Device checks performed: Device launch not performed (blocked: no device connected, emulator blocked by BIOS virtualization).
- Issues or blockers: CPU virtualization disabled in BIOS; physical device connection required for runtime verification.
- Decisions made (and why): Accepted D-019 establishing toolchain versions (Gradle 8.11.1, AGP 8.7.3, Kotlin 2.0.21, compileSdk 35, minSdk 26).
- Current task remaining: Wait for user approval of Phase 1 report.
- Next step: Phase 2 — Local medicine data model, bundled JSON, provenance, and validation.

### 2026-10-17 — Phase 0: Baseline Review and Toolchain Inspection
- Goal: Inspect all specification documents and actual repository/toolchain state; identify conflicts, blockers, and constraints before any coding.
- Files changed: `MEMORY.md`, `TASKS.md`.
- Implemented and verified: Full document review (PRD, TRD, App Flow, UI/UX Brief, Backend Schema, DECISIONS, IMPLEMENTATION_PLAN, MEMORY, TASKS); verified repository is clean slate (no code yet); verified Android Studio JBR 21 and Android SDK 36.1 locations; checked ADB and emulator state.
- Commands/builds/tests run and outcomes:
  - `git status`: Fatal, not a git repository.
  - `Get-ChildItem -Force`: 10 document/pdf files present.
  - `java -version`: Not on PATH; `JAVA_HOME` pointed to non-existent `C:\Java\bin`.
  - Bundled JBR check: `C:\Program Files\Android\Android Studio\jbr\bin\java.exe -version` -> OpenJDK 21.0.10 verified.
  - Android SDK check: `C:\Users\skzah\AppData\Local\Android\Sdk` verified (`platforms/android-36.1`, `build-tools/36.1.0`, `37.0.0`).
  - `adb devices`: Daemon started, 0 devices attached.
  - `emulator -list-avds`: No AVDs found.
- Device checks performed: Checked ADB; physical Samsung Galaxy F13 is not currently attached.
- Issues or blockers: `JAVA_HOME` path needs correction to JBR 21 in build commands; `ANDROID_HOME` must be exported; no device/emulator attached.
- Decisions made (and why): Confirmed D-001 through D-018; confirmed barcode scanning is strictly out of scope; confirmed ARCore is conditional with mandatory 2D accessible fallback.
- Current task remaining: Wait for user approval of Phase 0 report.
- Next step: Phase 1 — Project setup and reproducible Android build.

## 7. Implementation Workflow Rules

- Work in small, testable milestones.
- Before editing, identify the files and acceptance criteria relevant to the current task.
- After editing, inspect the diff and run the most relevant build/test checks available.
- Do not mark a feature complete merely because code was generated; compile it and test it where feasible.
- If physical-device testing is not possible, mark device behaviour as unverified.
- Keep the core path working: camera capture → OCR → field extraction → local match/confidence gate → accessible result.
- Implement a 2D accessible path before investing in advanced AR.
- Keep OCR, parsing, and matching off the UI/render thread.
- Clean up camera, ARCore, and TextToSpeech resources when their lifecycle ends.
- Every visible button must work. Remove or clearly disable controls for features not implemented.
- Update this file when scope, architecture, dependency decisions, data schema, or implementation status changes.
- Keep the project scope appropriate for a one-week academic prototype; do not add infrastructure without a clear user requirement.

## 8. Safety, Privacy, and Dataset Rules

- Treat medicine identification as uncertain text recognition, not proof of product authenticity or medical suitability.
- If name, strength, or composition is ambiguous or contradictory, request confirmation or return an unknown result.
- If expiry cannot be read unambiguously, report it as unreadable rather than guessing.
- Never invent indications, directions, warnings, side effects, or other medicine facts.
- Record a source and access date for factual dataset fields where available; omit unsupported content.
- Do not scrape or redistribute data without checking applicable terms and licences.
- Keep camera frames and scan history local. Do not introduce analytics or cloud transmission.
- Do not claim clinical validation, certification, production readiness, or measured accuracy without evidence.

## 9. How to Report Progress to the User

When asked for status, summarise from the verified state in this file and the repository:

1. What is completed and how it was verified.
2. What is currently in progress.
3. Known issues and unverified behaviour.
4. The next recommended action.

Clearly distinguish **implemented**, **tested**, **device-verified**, and **planned**. Do not describe an AR feature as working if only the 2D fallback has been tested.

## 10. Definition of Done for a Session

Before ending an implementation session:

- [ ] `MEMORY.md` reflects the repository state accurately.
- [ ] Changed files and meaningful decisions are recorded.
- [ ] Build/test results are reported honestly.
- [ ] Known issues and unverified checks are listed.
- [ ] One concrete next step is recorded.
- [ ] No planned feature is accidentally described as complete.

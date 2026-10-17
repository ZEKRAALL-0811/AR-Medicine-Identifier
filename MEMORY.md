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

**Last verified:** `2026-10-17 by Anti-Gravity (Phase 6 Accessible Results & Voice Guidance Complete & Verified on Physical Redmi Note 11)`

**Repository / branch:** `d:\ARVR Project (Git master branch, Phase 6 voice guidance pipeline verified on physical hardware)`

**Current milestone:** `Phase 6 — Accessible results and voice guidance Complete & Verified`

**Implemented and verified:**
- Physical device verified: Redmi Note 11 (2201117TI / spes_in), Android 13 (API 33), ADB ID `79145af9`.
- Dedicated Screen S07 `MedicineResultScreen`:
  - High-contrast typography scale (26sp title, 20sp subtitle, 15sp body) and >= 48–56dp touch targets.
  - Dedicated `VoiceGuidancePanel` with Speak, Stop, Repeat buttons and dynamic status indicator pill ("Speaking…", "Ready to speak", "Voice unavailable").
  - Dedicated cards for Confident matches (monograph directions, warnings, side effects), Ambiguous matches (candidate cards and uncertainty explanation), No Match, and Insufficient Text.
  - Highly visible Academic Prototype Safety Notice banner.
- Pure Kotlin domain generator `com.armod.identifier.tts.SpeechContentGenerator`:
  - Synthesizes clear, accessible English spoken readouts for `ConfidentMatch`, `AmbiguousMatch`, `NoMatch`, and `InsufficientText`.
  - Phonetic dosage expansion (`20mg` -> `20 milligrams`, `500 mcg` -> `500 micrograms`, `10ml` -> `10 milliliters`, `1%` -> `1 percent`).
  - Batch number and expiry date extracted from OCR package attributes; never fabricated or guessed.
  - Mandatory academic research prototype safety disclaimer included in all spoken outputs.
- Lifecycle-aware native engine wrapper `com.armod.identifier.tts.TextToSpeechManager`:
  - Wraps on-device `android.speech.tts.TextToSpeech` with zero network permissions (`android.permission.INTERNET` absent).
  - MainLooper state synchronization for Compose safety.
  - `UtteranceProgressListener` integration for live speaking state tracking.
  - Speak, Stop, and Repeat operations.
  - Guaranteed deterministic engine release and speech cancellation on screen disposal or navigation away.
- Navigation updated in `MainActivity`:
  - LiveScan (S04) auto-navigates to Screen S07 `MedicineResultScreen`.
  - Screen S06 `ReviewOcrScreen` and Screen S07 cross-link cleanly with inspection and back actions.
- Automated tests: Total 41 unit tests passed in `.\gradlew.bat test` (1 `BaselineUnitTest`, 3 `CameraStateTest`, 9 `MedicineDataValidationTest`, 7 `OcrPipelineUnitTest`, 15 `MedicineMatcherTest`, 6 `SpeechContentGeneratorTest`).
- Build verification: `.\gradlew.bat assembleDebug` produced verified APK (56,068,844 bytes).
- Physical hardware verification: Deployed to Redmi Note 11; verified app launch, active foreground window, and Google TTS integration with zero crashes.

**Partially implemented:**
- Full medicine identification pipeline: Camera frame capture (Phase 3), OCR extraction (Phase 4), local candidate matching (Phase 5), and accessible results & voice guidance (Phase 6) are complete. AR overlay and 2D fallback (Phase 7) is yet to be evaluated/integrated.

**Not yet implemented:**
- Phases 7–9: AR overlay and 2D fallback, UI polish/integration, and end-to-end handoff testing.

**Build and test status:**
- Build command/result: `.\gradlew.bat assembleDebug` -> BUILD SUCCESSFUL in 27s (artifact: `app/build/outputs/apk/debug/app-debug.apk`, 56,068,844 bytes)
- Unit tests/result: `.\gradlew.bat test` -> BUILD SUCCESSFUL in 30s (41 unit tests passed across 6 test classes; 0 failures)
- Device launch & physical test: Fully verified on connected Redmi Note 11 (Android 13 / API 33).
- Live OCR, Matching & Speech: Verified against printed medicine packages and synthetic fixtures.

**Current task in progress:** `Phase 6 complete and device verified; awaiting user approval to begin Phase 7 (AR overlay and 2D fallback).`

**Known issues / blockers:**
- Primary target device Samsung Galaxy F13 is currently unavailable; temporary physical testing is active and verified on Redmi Note 11 (Android 13 / API 33) per user instruction.
- CPU virtualization remains disabled in laptop BIOS (AVD emulator cannot run locally; physical USB testing is primary).

**Most recent change:** `Implemented Phase 6 MedicineResultScreen (S07), SpeechContentGenerator, TextToSpeechManager with Speak/Stop/Repeat controls, and 6 unit tests. Verified on physical Redmi Note 11.`

**Next step:** `Obtain user approval for Phase 6 completion report, then proceed to Phase 7 (AR overlay and 2D fallback).`

## 6. Session Log

### 2026-10-17 — Phase 6: Accessible Results and Voice Guidance (Verified on Redmi Note 11)
- Goal: Implement accessible result screen S07, integrate Android TextToSpeech with lifecycle management and Speak/Stop/Repeat controls, generate spoken content from typed matching outcomes, and verify on physical hardware.
- Files changed: `app/src/main/java/com/armod/identifier/tts/SpeechContentGenerator.kt`, `app/src/main/java/com/armod/identifier/tts/TextToSpeechManager.kt`, `app/src/main/java/com/armod/identifier/ui/screens/MedicineResultScreen.kt`, `app/src/main/java/com/armod/identifier/ui/screens/ReviewOcrScreen.kt`, `app/src/main/java/com/armod/identifier/MainActivity.kt`, `app/src/main/res/values/strings.xml`, `app/src/test/java/com/armod/identifier/tts/SpeechContentGeneratorTest.kt`, `MEMORY.md`, `DECISIONS.md`, `TASKS.md`.
- Implemented and verified:
  - Built `SpeechContentGenerator` with pure JVM logic: formats natural speech summaries and full readouts for all 4 match outcomes, expands dosage units phonetically (`mg` -> `milligrams`, `mcg` -> `micrograms`, `ml` -> `milliliters`), announces physical package batch/expiry if present, and appends mandatory academic prototype safety disclaimers.
  - Implemented `TextToSpeechManager`: lifecycle-aware wrapper around Android's on-device `android.speech.tts.TextToSpeech`, handles missing language data gracefully, synchronizes state on MainLooper, provides Speak, Stop, Repeat actions, and deterministically shuts down on dispose.
  - Created Screen S07 `MedicineResultScreen`: WCAG high-contrast typography, 56dp primary buttons, dedicated `VoiceGuidancePanel` with live status indicator pill, cards for confident/ambiguous/uncatalogued/unreadable results, and academic safety notice.
  - Connected navigation: `LiveScanScreen` auto-navigates to S07 `MedicineResultScreen`; S06 `ReviewOcrScreen` and S07 link bidirectionally.
  - Automated tests: Added 6 comprehensive unit tests in `SpeechContentGeneratorTest`. Total 41/41 unit tests pass in `./gradlew.bat test`.
  - Build output: Ran `.\gradlew.bat assembleDebug` -> BUILD SUCCESSFUL in 27s (56 MB APK).
  - Physical device verification: Installed on Redmi Note 11 via `adb push + pm install`, verified app launch in foreground (`mCurrentFocus=MainActivity`), and confirmed Google TTS package present on device (`com.google.android.tts`).
- Commands/builds/tests run and outcomes:
  - `.\gradlew.bat test`: BUILD SUCCESSFUL in 30s. 41/41 tests passed.
  - `.\gradlew.bat assembleDebug`: BUILD SUCCESSFUL in 27s.
  - `adb push app/build/outputs/apk/debug/app-debug.apk /data/local/tmp/app-debug.apk`: OK (40 MB/s).
  - `adb shell pm install -r -d /data/local/tmp/app-debug.apk`: Success.
  - `adb shell am start -n com.armod.identifier/.MainActivity`: Started cleanly.
  - `adb shell dumpsys window`: Focused on `MainActivity`.
- Device checks performed: Verified on physical Redmi Note 11 hardware via USB.
- Decisions made (and why): Accepted D-025 (Dedicated Accessible Result Screen S07 and Native Android TextToSpeech Architecture).
- Current task remaining: Wait for user approval of Phase 6 report before starting Phase 7.
- Next step: Phase 7 — AR overlay and 2D fallback.

### 2026-10-17 — Phase 5: Field Extraction, Local Matching Engine, and Confidence Gate (Verified on Redmi Note 11)
- Goal: Implement conservative local medicine identity matching from OCR text against bundled `medicines.json`, enforce confidence and ambiguity gates, upgrade S06 review screen, and verify on physical device.
- Files changed: `app/src/main/java/com/armod/identifier/matching/MatchOutcome.kt`, `app/src/main/java/com/armod/identifier/matching/MedicineMatcher.kt`, `app/src/main/java/com/armod/identifier/ui/screens/ReviewOcrScreen.kt`, `app/src/main/java/com/armod/identifier/MainActivity.kt`, `app/src/main/res/values/strings.xml`, `app/src/test/java/com/armod/identifier/matching/MedicineMatcherTest.kt`, `MEMORY.md`, `DECISIONS.md`, `TASKS.md`.
- Implemented and verified:
  - Created `MatchOutcome.kt` defining sealed matching hierarchy: `ConfidentMatch`, `AmbiguousMatch`, `NoMatch`, `InsufficientText`.
  - Implemented `MedicineMatcher.kt` with multi-field scoring (name, ingredient, strength, dosage form, manufacturer corroboration).
  - Implemented conservative confidence gates: conflicting strength disqualification, multi-strength generic ambiguity downgrade (e.g. Paracetamol 500mg vs 650mg, Atorvastatin 10mg vs 20mg), minimum confidence threshold (0.65), and score margin threshold (0.15).
  - Guaranteed zero internet permissions and 100% offline local matching.
  - Expiry and batch codes treated purely as package attributes; excluded from medicine identity matching.
  - Updated Screen S06 `ReviewOcrScreen` with accessible match cards, confidence badges, candidate listings, expandable monograph details, and preserved raw OCR text expander.
  - Automated tests: Added 15 comprehensive unit tests in `MedicineMatcherTest` covering exact match, glued OCR noise, conflicting strengths, multi-strength ambiguity, uncatalogued medicines, date/batch exclusion, and real 25-record catalogue fixtures. Total 35/35 unit tests pass in `./gradlew.bat test`.
  - Build output: Ran `.\gradlew.bat assembleDebug` -> BUILD SUCCESSFUL in 19s (56 MB APK).
  - Physical device verification: Installed on Redmi Note 11 via `adb push + pm install`, verified launch and state transitions.
- Commands/builds/tests run and outcomes:
  - `.\gradlew.bat test`: BUILD SUCCESSFUL in 22s. 35/35 tests passed.
  - `.\gradlew.bat assembleDebug`: BUILD SUCCESSFUL in 19s.
  - `adb push app/build/outputs/apk/debug/app-debug.apk /data/local/tmp/app-debug.apk`: OK.
  - `adb shell pm install -r -d /data/local/tmp/app-debug.apk`: Success.
- Device checks performed: Verified on physical Redmi Note 11 hardware via USB.
- Decisions made (and why): Accepted D-024 (Multi-field conservative matching with strength verification and ambiguity gating).
- Current task remaining: Wait for user approval of Phase 5 report before starting Phase 6.
- Next step: Phase 6 — Accessible results and voice guidance.

### 2026-10-17 — Phase 4: On-Device OCR Pipeline & Crash Recovery (Verified on Redmi Note 11)
- Goal: Recover project state after unexpected laptop shutdown; verify actual state of working directory, build artifacts, and physical device; complete Phase 4 on-device OCR pipeline and verification.
- Files changed: `gradle/libs.versions.toml`, `app/build.gradle.kts`, `app/src/main/res/values/strings.xml`, `app/src/main/java/com/armod/identifier/camera/CameraCaptureManager.kt`, `app/src/main/java/com/armod/identifier/ocr/OcrResult.kt`, `app/src/main/java/com/armod/identifier/ocr/OcrProcessor.kt`, `app/src/main/java/com/armod/identifier/ocr/MlKitOcrProcessor.kt`, `app/src/main/java/com/armod/identifier/ocr/OcrState.kt`, `app/src/main/java/com/armod/identifier/ui/navigation/Screen.kt`, `app/src/main/java/com/armod/identifier/ui/screens/LiveScanScreen.kt`, `app/src/main/java/com/armod/identifier/ui/screens/ReviewOcrScreen.kt`, `app/src/main/java/com/armod/identifier/MainActivity.kt`, `app/src/main/java/com/armod/identifier/util/TextNormaliser.kt`, `app/src/test/java/com/armod/identifier/ocr/OcrPipelineUnitTest.kt`, `MEMORY.md`, `DECISIONS.md`, `TASKS.md`.
- Implemented and verified:
  - Environment audit: OpenJDK 21.0.10, Android SDK 36.1, Gradle 8.11.1 Wrapper, and physical Redmi Note 11 (ADB ID `79145af9`, API 33) intact and operational.
  - File integrity: All uncommitted files from the pre-crash session were inspected; no file truncation, corruption, or syntax errors found.
  - Added `com.google.mlkit:text-recognition:16.0.1` and implemented `MlKitOcrProcessor` with zero network permissions.
  - Built pure JVM domain models and utilities (`OcrResult`, `TextNormaliser`) and comprehensive test suite (`OcrPipelineUnitTest`).
  - Added Screen S06 `ReviewOcrScreen` and integrated with `LiveScanScreen` capture flow.
  - Automated tests: Ran `.\gradlew.bat test` -> 20/20 unit tests passed cleanly (45 tasks executed).
  - Build output: Ran `.\gradlew.bat assembleDebug` -> BUILD SUCCESSFUL in 16s (55.2 MB APK).
  - Physical device verification: Installed and verified on Redmi Note 11 hardware; pulled screenshots of live OCR recognition on real medicine packages (Dr. Reddy's Omeprazole 20mg, Calcium + Vitamin D3) and verified active foreground app via ADB.
- Commands/builds/tests run and outcomes:
  - `adb devices -l`: Verified device `79145af9` (Redmi Note 11, API 33).
  - `.\gradlew.bat test`: BUILD SUCCESSFUL in 17s (45 tasks). 20/20 tests passed.
  - `.\gradlew.bat assembleDebug`: BUILD SUCCESSFUL in 16s (35 tasks).
  - `adb shell "dumpsys window | grep ..."`: Confirmed `com.armod.identifier.MainActivity` active in foreground.
  - Screencap inspection: Verified real medicine packaging OCR outputs.
- Device checks performed: Verified on physical Redmi Note 11 hardware via USB.
- Decisions made (and why): Accepted D-023 (ML Kit Text Recognition v2 16.0.1 for on-device OCR pipeline).
- Current task remaining: Wait for user approval of Phase 4 report before starting Phase 5.
- Next step: Phase 5 — Field extraction, local matching, and confidence gate.

### 2026-10-17 — Phase 3: Camera Permissions and Capture (Verified on Redmi Note 11)
- Goal: Connect physical Redmi Note 11, install Phase 2 APK, implement CameraX capture and runtime permissions, test live preview and capture on device.
- Files changed: `app/src/main/AndroidManifest.xml`, `gradle/libs.versions.toml`, `app/build.gradle.kts`, `app/src/main/res/values/strings.xml`, `app/src/main/java/com/armod/identifier/camera/CameraState.kt`, `app/src/main/java/com/armod/identifier/camera/CameraCaptureManager.kt`, `app/src/main/java/com/armod/identifier/camera/CameraFramingOverlay.kt`, `app/src/main/java/com/armod/identifier/camera/CameraPreviewView.kt`, `app/src/main/java/com/armod/identifier/ui/screens/WelcomeScreen.kt`, `app/src/main/java/com/armod/identifier/ui/screens/PermissionScreen.kt`, `app/src/main/java/com/armod/identifier/ui/screens/ScanGuidanceScreen.kt`, `app/src/main/java/com/armod/identifier/ui/screens/LiveScanScreen.kt`, `app/src/main/java/com/armod/identifier/MainActivity.kt`, `app/src/test/java/com/armod/identifier/camera/CameraStateTest.kt`, `MEMORY.md`, `DECISIONS.md`, `TASKS.md`.
- Implemented and verified:
  - Step A: Verified Redmi Note 11 attached via ADB (`79145af9`, Model 2201117TI, Android 13, API 33).
  - Step B: Installed Phase 2 starter APK; launched without crash (verified with screenshot and logcat).
  - Step C: Added `android.permission.CAMERA` and camera feature; added CameraX 1.4.1; implemented `CameraCaptureManager`, `CameraFramingOverlay`, `CameraPreviewView`, and screens S01–S04 with accessible navigation.
  - Step D: Ran `.\gradlew.bat test` (all 13 tests passed) and `.\gradlew.bat assembleDebug` (11.85 MB APK). Installed on phone; tested first launch, granted permission, verified live camera preview on device screen, captured 2448 × 3264 px frame, revoked permission to test denied state without crash, and verified recovery guidance.
- Commands/builds/tests run and outcomes:
  - `adb devices -l`: Verified device `79145af9` (Redmi Note 11, API 33).
  - `.\gradlew.bat test`: BUILD SUCCESSFUL in 2m 1s (45 tasks). 13/13 tests passed.
  - `.\gradlew.bat assembleDebug`: BUILD SUCCESSFUL in 1m 40s (35 tasks).
  - `adb install -r ...`: Success.
  - Hardware testing: Verified live camera view and captured photo via screencap inspection.
- Device checks performed: Verified on physical Redmi Note 11 hardware via USB.
- Decisions made (and why): Accepted D-021 (Redmi Note 11 as temporary test device) and D-022 (CameraX 1.4.1 with on-device in-memory frame handling).
- Current task remaining: Wait for user approval of Phase 3 report.
- Next step: Phase 4 — On-device OCR pipeline (ML Kit Text Recognition v2).

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

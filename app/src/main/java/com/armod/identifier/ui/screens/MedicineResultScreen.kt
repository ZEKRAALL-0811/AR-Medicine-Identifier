package com.armod.identifier.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.armod.identifier.R
import com.armod.identifier.data.model.MedicineRecord
import com.armod.identifier.matching.MatchCandidate
import com.armod.identifier.matching.MatchOutcome
import com.armod.identifier.ocr.OcrResult
import com.armod.identifier.tts.SpeechContentGenerator
import com.armod.identifier.tts.TextToSpeechManager
import com.armod.identifier.tts.TtsState
import com.armod.identifier.util.TextNormaliser

/**
 * Screen S07 — Dedicated Accessible Medicine Result Screen with Android TextToSpeech guidance.
 *
 * Implements:
 * - High-contrast, large-typography presentation of medicine identification results.
 * - On-device TextToSpeech with Speak, Stop, and Repeat voice controls.
 * - Explicit uncertainty presentation for ambiguous candidates, unknown packages, or unreadable labels.
 * - Sourced label details (directions, warnings, side effects) without fabricated medical advice.
 * - Mandatory visible and spoken academic prototype safety disclaimer.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MedicineResultScreen(
    matchOutcome: MatchOutcome,
    ocrResult: OcrResult,
    onScanAnotherClick: () -> Unit,
    onInspectOcrClick: () -> Unit,
    onBackClick: () -> Unit
) {
    val context = LocalContext.current
    var ttsState by remember { mutableStateOf<TtsState>(TtsState.Initializing) }

    val ttsManager = remember {
        TextToSpeechManager(context) { newState ->
            ttsState = newState
        }
    }

    val spokenResult = remember(matchOutcome, ocrResult) {
        SpeechContentGenerator.generateSpeech(matchOutcome, ocrResult)
    }

    // Auto-announce summary once upon initial TTS engine readiness
    var hasAnnouncedInitially by remember { mutableStateOf(false) }
    LaunchedEffect(ttsState) {
        if (ttsState is TtsState.Ready && !hasAnnouncedInitially) {
            hasAnnouncedInitially = true
            ttsManager.speak(spokenResult.summary)
        }
    }

    // Deterministically halt speech and release TTS engine when navigating away
    DisposableEffect(Unit) {
        onDispose {
            ttsManager.stop()
            ttsManager.release()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(R.string.medicine_result_title),
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 22.sp,
                        modifier = Modifier.semantics { heading() }
                    )
                },
                navigationIcon = {
                    IconButton(
                        onClick = {
                            ttsManager.stop()
                            onBackClick()
                        },
                        modifier = Modifier.semantics {
                            contentDescription = "Navigate back to live scanner"
                        }
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.back)
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // 1. Accessible Voice Guidance Control Panel
            VoiceGuidancePanel(
                ttsState = ttsState,
                spokenResult = spokenResult,
                onSpeakFull = { ttsManager.speak(spokenResult.fullSpeech) },
                onStop = { ttsManager.stop() },
                onRepeat = { ttsManager.repeat() }
            )

            // 2. Primary Medicine Result Display based on MatchOutcome
            when (matchOutcome) {
                is MatchOutcome.ConfidentMatch -> {
                    ConfidentResultCard(
                        record = matchOutcome.record,
                        ocrResult = ocrResult,
                        extractedBatch = matchOutcome.extractedBatch,
                        extractedExpiry = matchOutcome.extractedExpiry
                    )
                }

                is MatchOutcome.AmbiguousMatch -> {
                    AmbiguousResultCard(
                        candidates = matchOutcome.candidates,
                        explanation = matchOutcome.explanation,
                        ocrResult = ocrResult,
                        extractedBatch = matchOutcome.extractedBatch,
                        extractedExpiry = matchOutcome.extractedExpiry
                    )
                }

                is MatchOutcome.NoMatch -> {
                    NoMatchResultCard(
                        explanation = matchOutcome.explanation
                    )
                }

                is MatchOutcome.InsufficientText -> {
                    InsufficientTextResultCard(
                        explanation = matchOutcome.explanation
                    )
                }
            }

            // 3. Mandatory Academic Safety Notice Box
            AcademicSafetyDisclaimerCard()

            // 4. Primary & Secondary Actions
            Button(
                onClick = {
                    ttsManager.stop()
                    onScanAnotherClick()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .semantics {
                        contentDescription = "Scan another medicine package"
                    },
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary
                )
            ) {
                Icon(
                    imageVector = Icons.Default.Refresh,
                    contentDescription = null,
                    modifier = Modifier.size(22.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = stringResource(R.string.scan_another),
                    fontSize = 17.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }

            OutlinedButton(
                onClick = {
                    ttsManager.stop()
                    onInspectOcrClick()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .semantics {
                        contentDescription = "Inspect raw camera OCR text and detected lines"
                    },
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Info,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = stringResource(R.string.inspect_raw_ocr),
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Medium
                )
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

/**
 * Accessible Voice Guidance toolbar providing Speak, Stop, and Repeat actions.
 */
@Composable
fun VoiceGuidancePanel(
    ttsState: TtsState,
    spokenResult: com.armod.identifier.tts.SpokenResult,
    onSpeakFull: () -> Unit,
    onStop: () -> Unit,
    onRepeat: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .semantics {
                heading()
                contentDescription = "Voice guidance audio controls"
            },
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        ),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = stringResource(R.string.voice_guidance_title),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )

                // State indicator pill
                val isSpeaking = (ttsState as? TtsState.Ready)?.isSpeaking == true
                val statusText = when (ttsState) {
                    is TtsState.Initializing -> "Initializing voice…"
                    is TtsState.Ready -> if (isSpeaking) stringResource(R.string.tts_status_speaking) else stringResource(R.string.tts_status_idle)
                    is TtsState.Error -> "Voice error"
                    is TtsState.LanguageUnavailable -> "Voice unavailable"
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(
                            if (isSpeaking) MaterialTheme.colorScheme.primary
                            else MaterialTheme.colorScheme.surfaceVariant
                        )
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = statusText,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = if (isSpeaking) MaterialTheme.colorScheme.onPrimary
                        else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            // Spoken headline caption
            Text(
                text = spokenResult.summary,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onPrimaryContainer,
                modifier = Modifier.semantics {
                    contentDescription = "Voice guidance text: ${spokenResult.summary}"
                }
            )

            // Accessible Speak, Stop, Repeat buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Speak button
                Button(
                    onClick = onSpeakFull,
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp)
                        .semantics {
                            contentDescription = "Listen to complete medicine details"
                        },
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary
                    ),
                    enabled = ttsState is TtsState.Ready
                ) {
                    Icon(
                        imageVector = Icons.Default.PlayArrow,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = stringResource(R.string.tts_speak),
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                // Stop button
                OutlinedButton(
                    onClick = onStop,
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp)
                        .semantics {
                            contentDescription = "Stop voice guidance speech"
                        },
                    shape = RoundedCornerShape(10.dp),
                    enabled = ttsState is TtsState.Ready
                ) {
                    Text(
                        text = stringResource(R.string.tts_stop),
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                // Repeat button
                OutlinedButton(
                    onClick = onRepeat,
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp)
                        .semantics {
                            contentDescription = "Repeat speech readout from beginning"
                        },
                    shape = RoundedCornerShape(10.dp),
                    enabled = ttsState is TtsState.Ready
                ) {
                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(2.dp))
                    Text(
                        text = stringResource(R.string.tts_repeat),
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

            // Error notice if language data or TTS engine failed
            if (ttsState is TtsState.LanguageUnavailable) {
                Text(
                    text = ttsState.reason,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.semantics {
                        contentDescription = "Warning: ${ttsState.reason}"
                    }
                )
            } else if (ttsState is TtsState.Error) {
                Text(
                    text = ttsState.message,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.error
                )
            }
        }
    }
}

/**
 * High-confidence verified medicine result card.
 */
@Composable
fun ConfidentResultCard(
    record: MedicineRecord,
    ocrResult: OcrResult,
    extractedBatch: String?,
    extractedExpiry: String?
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        border = BorderStroke(1.5.dp, Color(0xFF2E7D32)) // Accessible forest green
    ) {
        Column(
            modifier = Modifier.padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Verified Badge
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.CheckCircle,
                    contentDescription = null,
                    tint = Color(0xFF2E7D32),
                    modifier = Modifier.size(20.dp)
                )
                Text(
                    text = stringResource(R.string.match_confirmed_title).uppercase(),
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF2E7D32)
                )
            }

            // Medicine Brand Name
            val brandDisplay = record.brandName ?: record.genericName ?: "Unknown medicine"
            Text(
                text = brandDisplay,
                fontSize = 26.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface,
                lineHeight = 32.sp
            )

            // Strength & Form
            val strengthDisplay = record.strength.orEmpty()
            val formDisplay = record.dosageForm.orEmpty()
            if (strengthDisplay.isNotBlank() || formDisplay.isNotBlank()) {
                Text(
                    text = "$strengthDisplay $formDisplay".trim(),
                    fontSize = 20.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            HorizontalDivider()

            // Key Identity Attribute Rows
            IdentityDetailRow(label = "Active Ingredient", value = record.genericName ?: "Not specified")
            IdentityDetailRow(label = "Dosage Form", value = record.dosageForm ?: "Not specified")
            IdentityDetailRow(label = "Verified Strength", value = record.strength ?: "Not specified")
            IdentityDetailRow(label = "Manufacturer", value = record.manufacturer ?: "Not specified")
            if (!record.labelPurpose.isNullOrBlank()) {
                IdentityDetailRow(label = "Purpose", value = record.labelPurpose)
            }

            HorizontalDivider()

            // Physical Package Attributes (from OCR)
            Text(
                text = stringResource(R.string.package_details_title),
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface
            )

            val batch = extractedBatch
                ?: TextNormaliser.extractBatchCandidate(ocrResult.rawText)
                ?: stringResource(R.string.not_detected_on_label)
            val expiry = extractedExpiry
                ?: TextNormaliser.extractExpiryCandidate(ocrResult.rawText)
                ?: stringResource(R.string.not_detected_on_label)

            IdentityDetailRow(label = stringResource(R.string.batch_number_label), value = batch)
            IdentityDetailRow(label = stringResource(R.string.expiry_date_label), value = expiry)

            HorizontalDivider()

            // Label Usage & Warnings
            if (!record.labelDirections.isNullOrBlank()) {
                Text(
                    text = stringResource(R.string.directions_title),
                    fontSize = 15.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = record.labelDirections,
                    fontSize = 15.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    lineHeight = 22.sp
                )
            }

            if (record.warnings.isNotEmpty()) {
                Text(
                    text = stringResource(R.string.warnings_title),
                    fontSize = 15.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.error
                )
                Text(
                    text = record.warnings.joinToString(". "),
                    fontSize = 15.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    lineHeight = 22.sp
                )
            }

            if (record.commonSideEffects.isNotEmpty()) {
                Text(
                    text = stringResource(R.string.side_effects_title),
                    fontSize = 15.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = record.commonSideEffects.joinToString(", "),
                    fontSize = 15.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    lineHeight = 22.sp
                )
            }
        }
    }
}

/**
 * Ambiguous match card showing uncertainty explanation and available candidates.
 */
@Composable
fun AmbiguousResultCard(
    candidates: List<MatchCandidate>,
    explanation: String,
    ocrResult: OcrResult,
    extractedBatch: String?,
    extractedExpiry: String?
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        border = BorderStroke(1.5.dp, Color(0xFFE65100)) // Accessible amber
    ) {
        Column(
            modifier = Modifier.padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Warning,
                    contentDescription = null,
                    tint = Color(0xFFE65100),
                    modifier = Modifier.size(20.dp)
                )
                Text(
                    text = stringResource(R.string.match_uncertain_title).uppercase(),
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFFE65100)
                )
            }

            Text(
                text = "Identity Uncertain — Multiple Matches",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )

            Text(
                text = "The camera detected text consistent with more than one record ($explanation). " +
                    "For your safety, the app will not guess. Please check your physical packaging directly.",
                fontSize = 15.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                lineHeight = 22.sp
            )

            HorizontalDivider()

            Text(
                text = "Possible Medicine Candidates:",
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface
            )

            candidates.forEachIndexed { index, candidate ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(12.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        val brand = candidate.record.brandName ?: candidate.record.genericName ?: "Unknown"
                        val generic = candidate.record.genericName ?: "Not specified"
                        Text(
                            text = "${index + 1}. $brand ($generic)",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "Strength: ${candidate.record.strength ?: "Unspecified"} • Form: ${candidate.record.dosageForm ?: "Unspecified"}",
                            fontSize = 14.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = "Manufacturer: ${candidate.record.manufacturer ?: "Not specified"}",
                            fontSize = 13.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            val batch = extractedBatch ?: TextNormaliser.extractBatchCandidate(ocrResult.rawText)
            val expiry = extractedExpiry ?: TextNormaliser.extractExpiryCandidate(ocrResult.rawText)
            if (batch != null || expiry != null) {
                HorizontalDivider()
                Text(
                    text = stringResource(R.string.package_details_title),
                    fontSize = 15.sp,
                    fontWeight = FontWeight.SemiBold
                )
                if (batch != null) IdentityDetailRow(label = stringResource(R.string.batch_number_label), value = batch)
                if (expiry != null) IdentityDetailRow(label = stringResource(R.string.expiry_date_label), value = expiry)
            }
        }
    }
}

/**
 * Result card when no medicine in the local catalogue matches the package.
 */
@Composable
fun NoMatchResultCard(explanation: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
    ) {
        Column(
            modifier = Modifier.padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Info,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.secondary,
                    modifier = Modifier.size(20.dp)
                )
                Text(
                    text = stringResource(R.string.match_none_title).uppercase(),
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.secondary
                )
            }

            Text(
                text = "Medicine Not Found in Local Catalogue",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )

            Text(
                text = "This packaging could not be matched against any of the 25 curated reference records in the offline dataset.\n\nReason: $explanation",
                fontSize = 15.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                lineHeight = 22.sp
            )

            Text(
                text = "Tips: Ensure the medicine brand name and strength are clearly in view, avoid bright reflections on foil packaging, and hold steady.",
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

/**
 * Result card when OCR text was unreadable or blank.
 */
@Composable
fun InsufficientTextResultCard(explanation: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.error.copy(alpha = 0.5f))
    ) {
        Column(
            modifier = Modifier.padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Warning,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.error,
                    modifier = Modifier.size(20.dp)
                )
                Text(
                    text = stringResource(R.string.match_insufficient_title).uppercase(),
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.error
                )
            }

            Text(
                text = "Unreadable Label Text",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )

            Text(
                text = "The on-device OCR engine could not detect clear printed characters on the captured image ($explanation).",
                fontSize = 15.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                lineHeight = 22.sp
            )

            Text(
                text = "Please ensure the label is well lit, held 15–25 cm away from the camera, and not blurred by hand motion.",
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

/**
 * Mandatory Academic Prototype Safety Disclaimer Banner.
 */
@Composable
fun AcademicSafetyDisclaimerCard() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)
        )
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalAlignment = Alignment.Top
        ) {
            Icon(
                imageVector = Icons.Default.Info,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(20.dp)
            )
            Text(
                text = stringResource(R.string.academic_disclaimer_full),
                fontSize = 13.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                lineHeight = 18.sp
            )
        }
    }
}

@Composable
private fun IdentityDetailRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 2.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Top
    ) {
        Text(
            text = label,
            fontSize = 14.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.weight(0.42f)
        )
        Text(
            text = value,
            fontSize = 15.sp,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.weight(0.58f)
        )
    }
}

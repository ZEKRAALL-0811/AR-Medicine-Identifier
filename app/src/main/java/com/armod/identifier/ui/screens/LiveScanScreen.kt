package com.armod.identifier.ui.screens

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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.armod.identifier.R
import com.armod.identifier.camera.CameraCaptureManager
import com.armod.identifier.camera.CameraFramingOverlay
import com.armod.identifier.camera.CameraPreviewView
import com.armod.identifier.camera.CameraState
import com.armod.identifier.ocr.OcrProcessor
import com.armod.identifier.ocr.OcrResult
import com.armod.identifier.ui.theme.PrimaryBlue
import com.armod.identifier.ui.theme.StatusError
import com.armod.identifier.ui.theme.StatusSuccess
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * Screen S04 — Live Scan / Camera Preview and Capture with On-Device OCR Pipeline
 * Follows Document 03 (App Flow) & Document 04 (UI/UX Design Brief):
 * - Live CameraX preview with hardware acceleration
 * - Minimal high-contrast framing brackets
 * - Large accessible capture button (56dp min height)
 * - Safe on-device OCR recognition off the main UI thread
 * - Immediate loading, recognition, and error feedback states
 */
@Composable
fun LiveScanScreen(
    ocrProcessor: OcrProcessor,
    onOcrResult: (OcrResult) -> Unit,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val captureManager = remember { CameraCaptureManager(context) }
    var cameraState by remember { mutableStateOf<CameraState>(CameraState.Initializing) }
    var isReadingLabel by remember { mutableStateOf(false) }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        when (val state = cameraState) {
            is CameraState.Error -> {
                // Error recovery view
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Card(
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier.padding(20.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = stringResource(id = R.string.camera_error),
                                style = MaterialTheme.typography.titleLarge,
                                color = StatusError,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(10.dp))
                            Text(
                                text = state.message,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurface,
                                textAlign = TextAlign.Center
                            )
                            Spacer(modifier = Modifier.height(20.dp))
                            if (state.canRetry) {
                                Button(
                                    onClick = {
                                        cameraState = CameraState.Initializing
                                        isReadingLabel = false
                                    },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(50.dp),
                                    shape = RoundedCornerShape(12.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue)
                                ) {
                                    Text(
                                        text = stringResource(id = R.string.retry),
                                        style = MaterialTheme.typography.titleMedium
                                    )
                                }
                                Spacer(modifier = Modifier.height(10.dp))
                            }
                            OutlinedButton(
                                onClick = onBackClick,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(48.dp),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Text(
                                    text = stringResource(id = R.string.back),
                                    style = MaterialTheme.typography.bodyLarge
                                )
                            }
                        }
                    }
                }
            }

            else -> {
                // Live Camera View
                CameraPreviewView(
                    captureManager = captureManager,
                    modifier = Modifier.fillMaxSize(),
                    onReady = {
                        cameraState = CameraState.Ready
                    },
                    onError = { error ->
                        cameraState = CameraState.Error(
                            message = error.localizedMessage ?: "Failed to open camera hardware"
                        )
                    }
                )

                // Framing guide overlay
                CameraFramingOverlay(modifier = Modifier.fillMaxSize())

                // Top bar: Status indicator & Back navigation
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 40.dp, start = 16.dp, end = 16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Button(
                        onClick = onBackClick,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.Black.copy(alpha = 0.65f),
                            contentColor = Color.White
                        ),
                        shape = RoundedCornerShape(20.dp)
                    ) {
                        Text(
                            text = "← ${stringResource(id = R.string.back)}",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.SemiBold
                        )
                    }

                    // Status pill
                    Surface(
                        shape = RoundedCornerShape(20.dp),
                        color = Color.Black.copy(alpha = 0.65f)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .clip(CircleShape)
                                    .background(
                                        when {
                                            isReadingLabel -> Color.Yellow
                                            state is CameraState.Ready -> StatusSuccess
                                            else -> Color.Gray
                                        }
                                    )
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = when {
                                    isReadingLabel -> stringResource(id = R.string.reading_label)
                                    state is CameraState.Initializing -> "Starting camera…"
                                    state is CameraState.Ready -> "Ready to scan"
                                    else -> "Camera"
                                },
                                style = MaterialTheme.typography.bodySmall,
                                color = Color.White,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }

                // Bottom Controls: Large capture action button
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.BottomCenter)
                        .padding(bottom = 32.dp, start = 24.dp, end = 24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Button(
                        onClick = {
                            if (state is CameraState.Ready && !isReadingLabel) {
                                isReadingLabel = true
                                captureManager.captureImageProxy(
                                    executor = ContextCompat.getMainExecutor(context),
                                    onCaptured = { imageProxy ->
                                        coroutineScope.launch(Dispatchers.Default) {
                                            try {
                                                val ocrResult = ocrProcessor.processImage(imageProxy)
                                                withContext(Dispatchers.Main) {
                                                    isReadingLabel = false
                                                    onOcrResult(ocrResult)
                                                }
                                            } catch (e: Exception) {
                                                withContext(Dispatchers.Main) {
                                                    isReadingLabel = false
                                                    cameraState = CameraState.Error(
                                                        message = e.localizedMessage ?: "OCR recognition failed"
                                                    )
                                                }
                                            }
                                        }
                                    },
                                    onError = { error ->
                                        isReadingLabel = false
                                        cameraState = CameraState.Error(
                                            message = error.localizedMessage ?: "Image capture failed"
                                        )
                                    }
                                )
                            }
                        },
                        enabled = state is CameraState.Ready && !isReadingLabel,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = PrimaryBlue,
                            disabledContainerColor = PrimaryBlue.copy(alpha = 0.5f)
                        )
                    ) {
                        if (isReadingLabel) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(24.dp),
                                color = Color.White,
                                strokeWidth = 2.dp
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(
                                text = stringResource(id = R.string.reading_label),
                                style = MaterialTheme.typography.titleMedium,
                                color = Color.White
                            )
                        } else {
                            Text(
                                text = stringResource(id = R.string.capture_button),
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.SemiBold,
                                color = Color.White
                            )
                        }
                    }
                }
            }
        }
    }
}

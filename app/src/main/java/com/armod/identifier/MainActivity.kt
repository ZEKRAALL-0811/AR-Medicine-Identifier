package com.armod.identifier

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import com.armod.identifier.ocr.MlKitOcrProcessor
import com.armod.identifier.ocr.OcrResult
import com.armod.identifier.ui.navigation.Screen
import com.armod.identifier.ui.screens.LiveScanScreen
import com.armod.identifier.ui.screens.PermissionScreen
import com.armod.identifier.ui.screens.ReviewOcrScreen
import com.armod.identifier.ui.screens.ScanGuidanceScreen
import com.armod.identifier.ui.screens.WelcomeScreen
import com.armod.identifier.ui.theme.ARMedicineIdentifierTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ARMedicineIdentifierTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    AppNavigation()
                }
            }
        }
    }
}

@Composable
fun AppNavigation() {
    val context = LocalContext.current
    val ocrProcessor = remember { MlKitOcrProcessor() }
    var currentScreen by remember { mutableStateOf<Screen>(Screen.Welcome) }
    var currentOcrResult by remember { mutableStateOf<OcrResult?>(null) }

    DisposableEffect(Unit) {
        onDispose {
            ocrProcessor.close()
        }
    }

    when (currentScreen) {
        Screen.Welcome -> {
            WelcomeScreen(
                onStartScanningClick = {
                    val hasCameraPermission = ContextCompat.checkSelfPermission(
                        context,
                        Manifest.permission.CAMERA
                    ) == PackageManager.PERMISSION_GRANTED

                    if (hasCameraPermission) {
                        currentScreen = Screen.ScanGuidance
                    } else {
                        currentScreen = Screen.Permission
                    }
                }
            )
        }

        Screen.Permission -> {
            BackHandler {
                currentScreen = Screen.Welcome
            }
            PermissionScreen(
                onPermissionGranted = {
                    currentScreen = Screen.ScanGuidance
                },
                onBackClick = {
                    currentScreen = Screen.Welcome
                }
            )
        }

        Screen.ScanGuidance -> {
            BackHandler {
                currentScreen = Screen.Welcome
            }
            ScanGuidanceScreen(
                onBeginScanClick = {
                    currentScreen = Screen.LiveScan
                },
                onBackClick = {
                    currentScreen = Screen.Welcome
                }
            )
        }

        Screen.LiveScan -> {
            BackHandler {
                currentScreen = Screen.ScanGuidance
            }
            LiveScanScreen(
                ocrProcessor = ocrProcessor,
                onOcrResult = { result ->
                    currentOcrResult = result
                    currentScreen = Screen.ReviewOcr
                },
                onBackClick = {
                    currentScreen = Screen.ScanGuidance
                }
            )
        }

        Screen.ReviewOcr -> {
            val result = currentOcrResult ?: OcrResult(rawText = "")
            BackHandler {
                currentScreen = Screen.LiveScan
            }
            ReviewOcrScreen(
                ocrResult = result,
                onScanAnotherClick = {
                    currentOcrResult = null
                    currentScreen = Screen.LiveScan
                },
                onBackClick = {
                    currentScreen = Screen.LiveScan
                }
            )
        }

        else -> {
            WelcomeScreen(
                onStartScanningClick = {
                    currentScreen = Screen.Permission
                }
            )
        }
    }
}

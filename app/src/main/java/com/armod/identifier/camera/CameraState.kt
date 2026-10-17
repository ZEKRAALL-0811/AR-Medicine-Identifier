package com.armod.identifier.camera

/**
 * State representing camera permissions and capture readiness.
 */
sealed interface CameraState {
    data object PermissionRequired : CameraState
    data object PermissionDenied : CameraState
    data object Initializing : CameraState
    data object Ready : CameraState
    data class Error(val message: String) : CameraState
}

package com.armod.identifier.ar

/**
 * AR tracking state and fallback availability.
 */
sealed interface ArTrackingState {
    data object Unsupported : ArTrackingState
    data object Initializing : ArTrackingState
    data object Tracking : ArTrackingState
    data object Lost : ArTrackingState
    data object Fallback2D : ArTrackingState
}

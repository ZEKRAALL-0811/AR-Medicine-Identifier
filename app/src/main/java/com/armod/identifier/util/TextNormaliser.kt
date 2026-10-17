package com.armod.identifier.util

/**
 * Text normalisation helper functions for matching and OCR sanitisation.
 */
object TextNormaliser {
    fun normalise(input: String): String {
        return input.trim().lowercase().replace(Regex("\\s+"), " ")
    }
}

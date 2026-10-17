package com.armod.identifier.ocr

/**
 * Representation of raw on-device OCR output blocks and lines.
 */
data class OcrResult(
    val rawText: String,
    val lines: List<String> = emptyList(),
    val timestampMillis: Long = System.currentTimeMillis()
)

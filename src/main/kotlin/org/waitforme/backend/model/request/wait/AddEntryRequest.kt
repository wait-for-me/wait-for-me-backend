package org.waitforme.backend.model.request.wait

data class AddEntryRequest(
    val phoneNumber: String?,
    val headCount: Int = 1,
    val entryCode: String,
)

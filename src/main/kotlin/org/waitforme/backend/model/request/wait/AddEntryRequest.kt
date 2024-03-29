package org.waitforme.backend.model.request.wait

data class AddEntryRequest(
    val userId: Int?,
    val phoneNumber: String?,
    val headCount: Int = 1
)

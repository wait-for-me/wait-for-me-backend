package org.waitforme.backend.model.request.wait

data class CheckStatusRequest(
    val phoneNumber: String,
    val password: String,
)

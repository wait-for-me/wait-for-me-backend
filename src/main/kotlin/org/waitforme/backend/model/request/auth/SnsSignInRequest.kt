package org.waitforme.backend.model.request.auth

import org.waitforme.backend.enums.Provider

data class SnsSignInRequest(
    val provider: Provider,
    val snsId: String,
)

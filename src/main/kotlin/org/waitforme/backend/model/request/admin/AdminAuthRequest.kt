package org.waitforme.backend.model.request.admin

data class AdminAuthRequest(
    val email: String,
    val password: String,
    val name: String? = null,
//    val authority: AdminAuthority
)

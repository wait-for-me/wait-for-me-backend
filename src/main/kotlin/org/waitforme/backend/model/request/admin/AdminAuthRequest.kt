package org.waitforme.backend.model.request.admin

import org.waitforme.backend.enums.AdminAuthority

data class AdminAuthRequest(
    val email: String,
    val password: String,
    val name: String,
    val authority: AdminAuthority
)

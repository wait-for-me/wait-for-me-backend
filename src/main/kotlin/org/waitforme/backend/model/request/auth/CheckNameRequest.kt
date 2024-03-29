package org.waitforme.backend.model.request.auth

import javax.validation.constraints.Pattern

data class CheckNameRequest(
    @field:Pattern(regexp = "^[a-zA-Z0-9]{1,8}\$")
    val name: String,
)

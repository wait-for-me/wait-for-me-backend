package org.waitforme.backend.config.security.exception

import org.springframework.http.HttpStatus

data class BaseRes(
    val status: HttpStatus,
    val message: String,
)
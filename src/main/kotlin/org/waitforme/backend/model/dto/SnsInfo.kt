package org.waitforme.backend.model.dto

import org.waitforme.backend.enums.SnsType

data class SnsInfo(
    val snsType: SnsType,
    val snsId: String
)

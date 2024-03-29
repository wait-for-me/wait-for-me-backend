package org.waitforme.backend.model.dto.shop

import java.time.LocalDate

data class OwnerShopListResultDto(
    val id: Int,
    val title: String,
    val imagePath: String,
    val startedAt: LocalDate,
    val endedAt: LocalDate,
    val registrationNumber: String,
)
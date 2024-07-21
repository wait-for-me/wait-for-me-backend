package org.waitforme.backend.model.dto.shop

import java.time.LocalDate

data class ShopListResultDto(
    val id: Int,
    val name: String,
    val hostName: String,
    val imagePath: String,
    val endedAt: LocalDate,
    val isFavorite: Boolean,
)

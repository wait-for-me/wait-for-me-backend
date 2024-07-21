package org.waitforme.backend.model.response.shop

import org.waitforme.backend.model.dto.shop.ShopListResultDto
import java.time.LocalDate

data class ShopListResponse(
    val id: Int,
    val title: String,
    val host: String,
    val isFavorite: Boolean,
    val imagePath: String,
    val endedAt: LocalDate,
)

fun ShopListResultDto.toResponse() = ShopListResponse(
    id = id,
    title = name,
    host = hostName,
    isFavorite = isFavorite,
    imagePath = imagePath,
    endedAt = endedAt
)

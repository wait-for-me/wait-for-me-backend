package org.waitforme.backend.model.response.shop

import org.waitforme.backend.model.dto.shop.ShopListResultDto
import java.time.LocalDate

data class ShopListResponse(
    val id: Int,
    val title: String,
    val imagePath: String,
    val endedAt: LocalDate,
)

fun ShopListResultDto.toResponse() = ShopListResponse(
    id = id,
    title = name,
    imagePath = imagePath,
    endedAt = endedAt
)

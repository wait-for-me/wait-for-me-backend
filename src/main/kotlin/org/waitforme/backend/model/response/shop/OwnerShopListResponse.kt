package org.waitforme.backend.model.response.shop

import org.waitforme.backend.model.dto.shop.OwnerShopListResultDto
import java.time.LocalDate

data class OwnerShopListResponse(
    val id: Int,
    val title: String,
    val imagePath: String,
    val startedAt: LocalDate,
    val endedAt: LocalDate,
    val registrationNumber: String,
)

fun OwnerShopListResultDto.toResponse() = OwnerShopListResponse(
    id = id,
    title = title,
    imagePath = imagePath,
    startedAt = startedAt,
    endedAt = endedAt,
    registrationNumber = registrationNumber
)
package org.waitforme.backend.model.response.shop

import org.waitforme.backend.entity.shop.Shop
import org.waitforme.backend.model.dto.SnsInfo
import java.time.LocalDate
import java.time.LocalTime

data class ShopDetailResponse(
    val title: String,
    val description: String?,
    val imageInfo: List<ShopImageResponse>,
    val startedAt: LocalDate,
    val endedAt: LocalDate,
    val openedAt: LocalTime,
    val closedAt: LocalTime,
    val address: String,
    val snsInfo: List<SnsInfo> = listOf(),
    val isFavorite: Boolean = false,
    val isReserved: Boolean = false,
)

fun Shop.toDetailResponse(imageInfo: List<ShopImageResponse>) = ShopDetailResponse(
    title = name,
    description = description,
    imageInfo = imageInfo,
    startedAt = startedAt,
    endedAt = endedAt,
    openedAt = openedAt,
    closedAt = closedAt,
    address = address,
//    snsInfo = snsInfo.toInfo(),
)

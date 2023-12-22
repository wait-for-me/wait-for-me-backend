package org.waitforme.backend.model.response.shop

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import org.waitforme.backend.entity.shop.Shop
import org.waitforme.backend.entity.shop.ShopImage
import org.waitforme.backend.model.dto.SnsInfo
import java.time.LocalDate
import java.time.LocalTime

data class ShopDetailResponse(
    val id: Int,
    val userId: Int,
    val userName: String,
    val registrationNumber: String,
    val category: String,
    val title: String,
    val description: String?,
    val imageInfo: List<ShopImageResponse>,
    val startedAt: LocalDate,
    val endedAt: LocalDate,
    val openedAt: LocalTime,
    val closedAt: LocalTime,
    val address: String,
    val snsInfo: List<SnsInfo> = listOf(),
)

fun Shop.toDetailResponse(userName: String = "", imageInfo: List<ShopImage>) = ShopDetailResponse(
    id = id,
    userId = userId,
    userName = userName,
    registrationNumber = registrationNumber,
    category = category,
    title = name,
    description = description,
    imageInfo = imageInfo.map { it.toResponse() },
    startedAt = startedAt,
    endedAt = endedAt,
    openedAt = openedAt,
    closedAt = closedAt,
    address = address,
    snsInfo = Gson().fromJson(snsInfo, object : TypeToken<MutableList<SnsInfo>>() {}.type)
)

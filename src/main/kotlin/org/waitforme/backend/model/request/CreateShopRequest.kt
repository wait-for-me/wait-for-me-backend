package org.waitforme.backend.model.request

import com.google.gson.Gson
import org.springframework.web.multipart.MultipartFile
import org.waitforme.backend.entity.shop.Shop
import org.waitforme.backend.model.dto.SnsInfo
import java.time.LocalDate
import java.time.LocalTime

data class CreateShopRequest(
    val userName: String,
    val registrationNumber: String,
    val category: String,
    val title: String,
    val description: String?,
    val mainImage: MultipartFile,
    val subImages: List<MultipartFile>?,
    val startedAt: LocalDate,
    val endedAt: LocalDate,
    val openedAt: LocalTime,
    val closedAt: LocalTime,
    val address: String,
    val snsInfo: List<SnsInfo> = listOf(),
) {
    fun toEntity(): Shop =
        Shop(
            registrationNumber = registrationNumber,
            category = category,
            name = title,
            description = description,
            startedAt = startedAt,
            endedAt = endedAt,
            openedAt = openedAt,
            closedAt = closedAt,
            address = address,
            snsInfo = Gson().toJson(snsInfo)
        )
}

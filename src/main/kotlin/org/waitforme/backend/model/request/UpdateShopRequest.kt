package org.waitforme.backend.model.request

import org.springframework.web.multipart.MultipartFile
import org.waitforme.backend.model.dto.SnsInfo
import java.time.LocalDate
import java.time.LocalTime

data class UpdateShopRequest(
    val registrationNumber: String?,
    val category: String?,
    val title: String?,
    val description: String?,
    val mainImage: MultipartFile?,
    val subImages: List<MultipartFile>?,
    val startedAt: LocalDate?,
    val endedAt: LocalDate?,
    val openedAt: LocalTime?,
    val closedAt: LocalTime?,
    val address: String?,
    val snsInfo: List<SnsInfo>?,
)

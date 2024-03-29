package org.waitforme.backend.model.response.shop

import org.waitforme.backend.model.dto.shop.ShopListResultDto

data class ShopListResponse(
    val id: Int,
    val title: String,
    val imagePath: String
)

fun ShopListResultDto.toResponse() = ShopListResponse(
    id = id,
    title = name,
    imagePath = imagePath
)

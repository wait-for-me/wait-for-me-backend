package org.waitforme.backend.model.response.shop

import org.waitforme.backend.entity.shop.Shop
import org.waitforme.backend.entity.shop.ShopImage
import org.waitforme.backend.model.dto.ShopListResultDto
import javax.swing.border.TitledBorder

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

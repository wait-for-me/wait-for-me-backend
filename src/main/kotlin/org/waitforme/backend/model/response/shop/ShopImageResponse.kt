package org.waitforme.backend.model.response.shop

import org.waitforme.backend.entity.shop.ShopImage
import org.waitforme.backend.enums.ImageType

data class ShopImageResponse(
    val type: ImageType,
    val path: String
)

fun ShopImage.toResponse() = ShopImageResponse(
    type = imageType,
    path = imagePath
)

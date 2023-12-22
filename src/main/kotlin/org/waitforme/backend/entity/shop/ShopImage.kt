package org.waitforme.backend.entity.shop

import org.waitforme.backend.common.BaseEntity
import org.waitforme.backend.enums.ImageType
import javax.persistence.*

@Table(name = "shop_image")
@Entity
data class ShopImage(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Int = 0,
    val shopId: Int,
    @Enumerated(value = EnumType.STRING)
    var imageType: ImageType,
    var imagePath: String,
    var orderNo: Int = 1,
    var isShow: Boolean = true,
): BaseEntity()

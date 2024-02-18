package org.waitforme.backend.entity.bookmark

import org.waitforme.backend.common.BaseEntity
import javax.persistence.*

@Table(name = "bookmark")
@Entity
data class Bookmark(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Int = 0,
    val userId: Int,
    var shopId: Int,
    var isShow: Boolean = true,
) : BaseEntity()

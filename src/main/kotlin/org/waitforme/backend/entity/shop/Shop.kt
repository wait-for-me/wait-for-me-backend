package org.waitforme.backend.entity.shop

import org.waitforme.backend.common.BaseEntity
import org.waitforme.backend.model.request.UpdateShopRequest
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import javax.persistence.*

@Table(name = "shop")
@Entity
data class Shop(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Int = 0,
    var userId: Int = 0,
    var registrationNumber: String,
    var category: String,
    var name: String,
    var startedAt: LocalDate,
    var endedAt: LocalDate,
    var openedAt: LocalTime,
    var closedAt: LocalTime,
    var address: String,
    var description: String? = null,
    var snsInfo: String = "{}",
    var isDeleted: Boolean = false,
    var isShow: Boolean = true,
    var deletedAt: LocalDateTime? = null
) : BaseEntity() {
    fun updateIsShow(isShow: Boolean) {
        this.isShow = isShow
    }

    fun update(updateShopRequest: UpdateShopRequest) {
        updateShopRequest.registrationNumber?.let { this.registrationNumber = it }
        updateShopRequest.category?.let { this.category = it }
        updateShopRequest.title?.let { this.name = it }
        updateShopRequest.description?.let { this.description = it }
        updateShopRequest.startedAt?.let { this.startedAt = it }
        updateShopRequest.endedAt?.let { this.endedAt = it }
        updateShopRequest.openedAt?.let { this.openedAt = it }
        updateShopRequest.closedAt?.let { this.closedAt = it }
        updateShopRequest.address?.let { this.address = it }
    }
}

package org.waitforme.backend.entity.shop

import org.waitforme.backend.common.BaseEntity
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
    val userId: Int,
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
    var deletedAt: LocalDateTime
) : BaseEntity()

package org.waitforme.backend.entity.user

import org.waitforme.backend.common.BaseEntity
import java.time.LocalDateTime
import javax.persistence.*

@Table(
    name = "user_auth",
    indexes = [Index(name = "idx_phone_number", columnList = "phoneNumber", unique = true)],
) // TODO: userAuthLog?
@Entity
data class UserAuth(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Int = 0,
    val phoneNumber: String,
    var requestedAt: LocalDateTime, // TODO : Long으로 바꾸기
    var authenticatedAt: LocalDateTime? = null,
) : BaseEntity()

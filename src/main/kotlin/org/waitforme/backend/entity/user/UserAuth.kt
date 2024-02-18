package org.waitforme.backend.entity.user

import org.waitforme.backend.common.BaseEntity
import java.time.LocalDateTime
import javax.persistence.*

@Table(name = "user_auth") // TODO: userAuthLog?
@Entity
data class UserAuth(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Int = 0,
    val phoneNumber: String, // TODO : unique index 걸기
    var requestedAt: LocalDateTime,
    var authenticatedAt: LocalDateTime? = null,
) : BaseEntity()

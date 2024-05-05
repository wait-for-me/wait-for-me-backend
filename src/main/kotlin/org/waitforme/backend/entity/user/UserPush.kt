package org.waitforme.backend.entity.user

import org.waitforme.backend.common.BaseEntity
import javax.persistence.*

@Table(name = "user_push") // TODO: userAuthLog?
@Entity
data class UserPush(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Int = 0,
    val userId: Int,
    val pushToken: String,
    val deviceId: String,
) : BaseEntity()

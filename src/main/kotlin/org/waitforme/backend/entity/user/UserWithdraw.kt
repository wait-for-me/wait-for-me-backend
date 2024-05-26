package org.waitforme.backend.entity.user

import org.waitforme.backend.common.BaseEntity
import javax.persistence.*

@Entity
@Table(name = "user_withdraw")
data class UserWithdraw(
    @Id
    @GeneratedValue
    val id: Int? = null,
    val userId: Int,
    @Column(columnDefinition = "TEXT")
    val reason: String,
) : BaseEntity()

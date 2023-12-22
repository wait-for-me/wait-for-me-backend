package org.waitforme.backend.entity.admin

import org.waitforme.backend.common.BaseEntity
import org.waitforme.backend.enums.AdminAuthority
import java.time.LocalDateTime
import javax.persistence.*

@Table(name = "admin")
@Entity
data class Admin(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Int = 0,
    val email: String,
    val name: String,
    val password: String,
    val authority: AdminAuthority,
    val isDeleted: Boolean? = false,
    var deletedAt: LocalDateTime? = null,
) : BaseEntity()
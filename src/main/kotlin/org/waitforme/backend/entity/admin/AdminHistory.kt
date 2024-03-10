package org.waitforme.backend.entity.admin

import org.waitforme.backend.common.BaseEntity
import org.waitforme.backend.enums.AdminAction
import org.waitforme.backend.enums.AdminMenuCode
import javax.persistence.*

@Table(name = "admin_history")
@Entity
data class AdminHistory(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Int = 0,
    val adminId: Int,
    val menuCode: AdminMenuCode,
    val menuId: Int,
    val action: AdminAction,
    val memo: String? = null,
) : BaseEntity()

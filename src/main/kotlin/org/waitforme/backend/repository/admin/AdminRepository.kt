package org.waitforme.backend.repository.admin

import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository
import org.waitforme.backend.entity.admin.Admin

@Repository
interface AdminRepository : CrudRepository<Admin, Int> {
    fun findAdminByEmailAndIsDeleted(email: String, isDeleted: Boolean): Admin?
}

package org.waitforme.backend.repository.admin

import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository
import org.waitforme.backend.entity.admin.AdminHistory

@Repository
interface AdminHistoryRepository: CrudRepository<AdminHistory, Int> {
}
package org.waitforme.backend.repository.notice

import org.springframework.data.domain.Pageable
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository
import org.waitforme.backend.entity.notice.Notice

@Repository
interface NoticeRepository : CrudRepository<Notice, Int> {

    fun findNoticeByIdAndIsDeleted(id: Int, isDeleted: Boolean = false): Notice?

    fun findNoticesByIsDeletedOrderByCreatedAtDesc(isDeleted: Boolean = false, pageable: Pageable): List<Notice>
}

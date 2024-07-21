package org.waitforme.backend.service

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.waitforme.backend.entity.admin.AdminHistory
import org.waitforme.backend.enums.AdminAction
import org.waitforme.backend.enums.AdminMenuCode
import org.waitforme.backend.model.LoginAdmin
import org.waitforme.backend.model.request.notice.NoticeRequest
import org.waitforme.backend.model.request.notice.toNoticeEntity
import org.waitforme.backend.model.response.notice.*
import org.waitforme.backend.repository.admin.AdminHistoryRepository
import org.waitforme.backend.repository.notice.NoticeRepository
import org.webjars.NotFoundException
import java.time.LocalDateTime

@Service
class NoticeService(
    private val noticeRepository: NoticeRepository,
    private val adminHistoryRepository: AdminHistoryRepository,
) {
    fun findNotice(noticeId: Int): NoticeResponse =
        noticeRepository.findNoticeByIdAndIsDeleted(id = noticeId, isDeleted = false)?.toNoticeResponse()
            ?: throw NotFoundException("공지를 찾을 수 없습니다.")

    fun findNoticeList(): List<NoticeListResponse> =
        noticeRepository.findNoticesByIsDeletedOrderByCreatedAtDesc(isDeleted = false).toNoticeListResponse()

    @Transactional
    fun saveNotice(noticeId: Int? = null, request: NoticeRequest, loginAdmin: LoginAdmin): NoticeSaveResponse {
        val notice = noticeRepository.save(request.toNoticeEntity(id = noticeId))

        noticeId?.let {
            adminHistoryRepository.save(
                AdminHistory(
                    adminId = loginAdmin.admin.id,
                    menuCode = AdminMenuCode.NOTICE,
                    menuId = it,
                    action = AdminAction.MODIFY,
                ),
            )
        } ?: kotlin.run {
            adminHistoryRepository.save(
                AdminHistory(
                    adminId = loginAdmin.admin.id,
                    menuCode = AdminMenuCode.NOTICE,
                    menuId = notice.id,
                    action = AdminAction.CREATE,
                ),
            )
        }

        return NoticeSaveResponse(noticeId = notice.id)
    }

    @Transactional
    fun deleteNotice(noticeId: Int, loginAdmin: LoginAdmin): Boolean {
        return noticeRepository.findNoticeByIdAndIsDeleted(id = noticeId)?.let { notice ->
            noticeRepository.save(
                notice.apply {
                    isDeleted = true
                    deletedAt = LocalDateTime.now()
                },
            ).takeIf { it.isDeleted }?.let {
                adminHistoryRepository.save(
                    AdminHistory(
                        adminId = loginAdmin.admin.id,
                        menuCode = AdminMenuCode.NOTICE,
                        menuId = it.id,
                        action = AdminAction.DELETE,
                    ),
                )

                true
            } ?: false
        } ?: throw NotFoundException("공지를 찾을 수 없습니다.")
    }
}

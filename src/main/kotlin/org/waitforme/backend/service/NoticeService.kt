package org.waitforme.backend.service

import org.springframework.data.domain.PageRequest
import org.springframework.stereotype.Service
import org.waitforme.backend.model.LoginAdmin
import org.waitforme.backend.model.request.notice.NoticeRequest
import org.waitforme.backend.model.request.notice.toNoticeEntity
import org.waitforme.backend.model.response.notice.NoticeListResponse
import org.waitforme.backend.model.response.notice.NoticeResponse
import org.waitforme.backend.model.response.notice.toNoticeListResponse
import org.waitforme.backend.model.response.notice.toNoticeResponse
import org.waitforme.backend.repository.notice.NoticeRepository
import org.webjars.NotFoundException
import java.time.LocalDateTime

@Service
class NoticeService(
    private val noticeRepository: NoticeRepository,
) {
    fun findNotice(noticeId: Int): NoticeResponse =
        noticeRepository.findNoticeByIdAndIsDeleted(id = noticeId, isDeleted = false)?.toNoticeResponse()
            ?: throw NotFoundException("공지를 찾을 수 없습니다.")

    fun findNoticeList(pageRequest: PageRequest): List<NoticeListResponse> =
        noticeRepository.findNoticesByIsDeletedOrderByCreatedAtDesc(isDeleted = false, pageable = pageRequest)
            .toNoticeListResponse()

    fun saveNotice(noticeId: Int? = null, request: NoticeRequest, loginAdmin: LoginAdmin): NoticeResponse {
        return noticeRepository.save(request.toNoticeEntity(id = noticeId)).toNoticeResponse()
        // TODO : AdminHistory 추가
    }

    fun deleteNotice(noticeId: Int, loginAdmin: LoginAdmin): Boolean {
        return noticeRepository.findNoticeByIdAndIsDeleted(id = noticeId)?.let { notice ->
            noticeRepository.save(
                notice.apply {
                    isDeleted = true
                    deletedAt = LocalDateTime.now()
                },
            ).id == noticeId
        } ?: throw NotFoundException("공지를 찾을 수 없습니다.")
        // TODO : AdminHistory 추가
    }
}

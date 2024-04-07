package org.waitforme.backend.service

import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.PageRequest
import org.springframework.stereotype.Service
import org.waitforme.backend.entity.wait.Waiting
import org.waitforme.backend.enums.EntryStatus
import org.waitforme.backend.model.request.wait.AddEntryRequest
import org.waitforme.backend.model.request.wait.CancelWaitingRequest
import org.waitforme.backend.model.response.wait.WaitingOwnerResponse
import org.waitforme.backend.model.response.wait.WaitingResponse
import org.waitforme.backend.model.response.wait.toResponse
import org.waitforme.backend.repository.wait.WaitingRepository
import org.webjars.NotFoundException
import java.security.InvalidParameterException
import java.util.regex.Pattern

@Service
class WaitingService(
    private val waitingRepository: WaitingRepository
) {
    fun getWaitingListOwner(userId: Int, shopId: Int, pageRequest: PageRequest): Page<WaitingOwnerResponse> {
        // TODO: userId로 관리자 여부 판별 로직 추가하기
        val waiting = waitingRepository.findWaitingList(shopId, pageRequest.pageSize, pageRequest.offset).map { it.toResponse() }
        val count = waitingRepository.countWaitingList(shopId)

        return PageImpl(waiting, pageRequest, count)
    }

    fun getWaitingList(userId: Int, pageRequest: PageRequest): Page<WaitingResponse> {
        val waitingList = waitingRepository.findByUserId(userId, pageRequest.pageSize, pageRequest.offset)
            .map { it.toResponse() }
        val count = waitingRepository.countByUserId(userId)

        return PageImpl(waitingList, pageRequest, count)
    }

    fun addEntry(shopId: Int, userId: Int?, request: AddEntryRequest): Int {
        val orderNo = waitingRepository.findOrderNoTop1(shopId)

        val waiting = userId?.let {
            Waiting(
                userId = it,
                shopId = shopId,
                orderNo = orderNo + 1,
                headCount = request.headCount
            )
        } ?: run {
            validatePhoneNumber(request.phoneNumber!!)
            Waiting(
                phoneNumber = request.phoneNumber,
                shopId = shopId,
                orderNo = orderNo + 1,
                headCount = request.headCount
            )
        }

        return waitingRepository.save(waiting).orderNo
    }

    fun getRemainCount(shopId: Int): Int = waitingRepository.countWaitingList(shopId).toInt()

    fun cancelWaiting(userId: Int?, shopId: Int, request: CancelWaitingRequest): Boolean {
        val waiting = userId?.let {
            waitingRepository.findByShopIdAndUserId(shopId, it)
        } ?: run {
            waitingRepository.findByShopIdAndPhoneNumber(shopId, request.phoneNumber)
        } ?: throw NotFoundException("해당 대기 정보를 찾을 수 없습니다.")

        waiting.cancel()
        return (waitingRepository.save(waiting).status == EntryStatus.CANCELED)
    }

    private fun validatePhoneNumber(phoneNum: String) {
        val regex = "^\\s*(010|011|012|013|014|015|016|017|018|019)(-|\\)|\\s)*(\\d{3,4})(-|\\s)*(\\d{4})\\s*$";
        val p = Pattern.compile(regex);
        val m = p.matcher(phoneNum);

        if (!m.matches()) throw InvalidParameterException("유효한 휴대폰 번호가 아닙니다.")
    }
}
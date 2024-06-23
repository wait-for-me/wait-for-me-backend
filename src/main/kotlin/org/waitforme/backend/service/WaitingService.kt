package org.waitforme.backend.service

import com.google.gson.Gson
import com.google.gson.JsonParser
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.springframework.cloud.aws.messaging.listener.annotation.SqsListener
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.PageRequest
import org.springframework.stereotype.Service
import org.waitforme.backend.entity.wait.Waiting
import org.waitforme.backend.enums.EntryStatus
import org.waitforme.backend.model.dto.wait.WaitingMembersResult
import org.waitforme.backend.model.request.wait.AddEntryRequest
import org.waitforme.backend.model.request.wait.CancelWaitingRequest
import org.waitforme.backend.model.request.wait.CheckStatusRequest
import org.waitforme.backend.model.response.wait.WaitingOwnerResponse
import org.waitforme.backend.model.response.wait.WaitingResponse
import org.waitforme.backend.model.response.wait.toResponse
import org.waitforme.backend.repository.wait.WaitingRepository
import org.waitforme.backend.util.SqsUtil
import org.webjars.NotFoundException
import java.security.InvalidKeyException
import java.security.InvalidParameterException
import java.util.regex.Pattern

@Service
class WaitingService(
    private val waitingRepository: WaitingRepository,
    private val sqsUtil: SqsUtil,
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

    fun addEntry(shopId: Int, userId: Int?, request: AddEntryRequest) {
        val entryRequest = request.toEntryRequest(shopId, userId)

        val waiting = waitingRepository.findByShopIdAndEntryCode(shopId, request.entryCode)
            ?: throw NotFoundException("코드를 찾을 수 없습니다.")

        if (waiting.status != EntryStatus.DEFAULT) {
            throw InvalidKeyException("올바르지 않은 코드값입니다.")
        }

              // json 화
        val message = Gson().toJson(entryRequest)
        sqsUtil.sendMessage(message)
    }

    @SqsListener("waiting-sqs")
    fun addEntry(message: String): Int {
        val request = JsonParser.parseString(message).asJsonObject
        val shopId = request.get("shopId").asInt
        val entryCode = request.get("entryCode").asString
        val waiting = waitingRepository.findByShopIdAndEntryCode(shopId, entryCode)
            ?: throw NotFoundException("코드를 찾을 수 없습니다.")

        if (waiting.status != EntryStatus.DEFAULT) {
            throw InvalidKeyException("올바르지 않은 코드값입니다.")
        }

        waiting.update(
            userId = request.get("userId").asInt,
            phoneNumber = validatePhoneNumber(request.get("phoneNumber").asString),
            password = request.get("password").asString,
            headCount = request.get("headCount").asInt
        )

        return waitingRepository.save(waiting).orderNo
    }

    @SqsListener("DLQ")
    fun addEntryDLQ(message: String): Int {
        val request = JsonParser.parseString(message).asJsonObject
        val shopId = request.get("shopId").asInt
        val entryCode = request.get("entryCode").asString
        val waiting = waitingRepository.findByShopIdAndEntryCode(shopId, entryCode)
            ?: throw NotFoundException("코드를 찾을 수 없습니다.")

        if (waiting.status != EntryStatus.DEFAULT) {
            throw InvalidKeyException("올바르지 않은 코드값입니다.")
        }

        waiting.update(
            userId = request.get("userId").asInt,
            phoneNumber = validatePhoneNumber(request.get("phoneNumber").asString),
            password = request.get("password").asString,
            headCount = request.get("headCount").asInt
        )

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

    fun createCode(shopId: Int): String {
        var waiting = waitingRepository.findTop1ByShopIdOrderByIdDesc(shopId)

        if (waiting == null || waiting.status != EntryStatus.DEFAULT) {
            val charSet = ('0'..'9') + ('a'..'z') + ('A'..'Z')
            val randomCode = List(10) { charSet.random() }.joinToString("")

            waiting = Waiting(
                shopId = shopId,
                entryCode = randomCode,
                orderNo = (waiting?.orderNo ?: 0) + 1
            )
            waitingRepository.save(waiting)
        }

        return waiting.entryCode
    }

    fun checkStatus(userId: Int?, shopId: Int, request: CheckStatusRequest) =
        waitingRepository.countWaitingMembers(userId, shopId, request.password, request.phoneNumber)
            ?.toResponse() ?: WaitingMembersResult()

    private fun validatePhoneNumber(phoneNum: String): String {
        val regex = "^\\s*(010|011|012|013|014|015|016|017|018|019)(-|\\)|\\s)*(\\d{3,4})(-|\\s)*(\\d{4})\\s*$";
        val p = Pattern.compile(regex);
        val m = p.matcher(phoneNum);

        if (!m.matches()) throw InvalidParameterException("유효한 휴대폰 번호가 아닙니다.")

        return phoneNum
    }
}
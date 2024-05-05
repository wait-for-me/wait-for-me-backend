package org.waitforme.backend.service

import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.PageRequest
import org.springframework.stereotype.Service
import org.waitforme.backend.entity.wait.Waiting
import org.waitforme.backend.enums.EntryStatus
import org.waitforme.backend.model.request.wait.AddEntryRequest
import org.waitforme.backend.model.request.wait.ChangeEntryStatusRequest
import org.waitforme.backend.model.response.wait.WaitingOwnerResponse
import org.waitforme.backend.model.response.wait.WaitingResponse
import org.waitforme.backend.model.response.wait.WaitingStatusResponse
import org.waitforme.backend.model.response.wait.toResponse
import org.waitforme.backend.repository.shop.ShopRepository
import org.waitforme.backend.repository.wait.WaitingRepository
import java.security.InvalidParameterException
import java.time.LocalDateTime
import java.util.regex.Pattern

@Service
class WaitingService(
    private val waitingRepository: WaitingRepository,
    private val userPushService: UserPushService,
    private val shopRepository: ShopRepository,
) {
    fun getWaitingListOwner(userId: Int, shopId: Int, pageRequest: PageRequest): Page<WaitingOwnerResponse> {
        // TODO: userId로 관리자 여부 판별 로직 추가하기
        val waiting =
            waitingRepository.findWaitingList(shopId, pageRequest.pageSize, pageRequest.offset).map { it.toResponse() }
        val count = waitingRepository.countWaitingList(shopId)

        return PageImpl(waiting, pageRequest, count)
    }

    fun getWaitingList(userId: Int, pageRequest: PageRequest): Page<WaitingResponse> {
        val waitingList = waitingRepository.findByUserId(userId, pageRequest.pageSize, pageRequest.offset)
            .map { it.toResponse() }
        val count = waitingRepository.countByUserId(userId)

        return PageImpl(waitingList, pageRequest, count)
    }

    fun addEntry(shopId: Int, request: AddEntryRequest): Int {
        val orderNo = waitingRepository.findOrderNoTop1(shopId)

        val waiting = request.userId?.let {
            Waiting(
                userId = it,
                shopId = shopId,
                orderNo = orderNo + 1,
                headCount = request.headCount,
            )
        } ?: run {
            validatePhoneNumber(request.phoneNumber!!)
            Waiting(
                phoneNumber = request.phoneNumber,
                shopId = shopId,
                orderNo = orderNo + 1,
                headCount = request.headCount,
            )
        }

        return waitingRepository.save(waiting).orderNo
    }

    fun getRemainCount(shopId: Int): Int = waitingRepository.countWaitingList(shopId).toInt()

    fun changeEntryStatusOwner(request: ChangeEntryStatusRequest): WaitingStatusResponse {
        val result = waitingRepository.findStatusByPhoneNumberAndShopId(
            phoneNumber = request.phoneNumber,
            shopId = request.shopId,
        )?.let { waitingUser ->

            // 가게 정보 Get
            val shopInfo = shopRepository.findByIdAndIsShow(id = request.shopId)
                ?: throw InvalidParameterException("찾을 수 없는 가게 정보입니다.")

            // 각 상태에 따른 PUSH 요청
            when (waitingUser.status) {
                EntryStatus.WAIT -> {
                    when (request.entryStatus) {
                        EntryStatus.CALL -> {
                            sendMessageByStatus(
                                status = EntryStatus.CALL,
                                phoneNum = waitingUser.phoneNumber!!,
                                shopName = shopInfo.name,
                            )

                            waitingRepository.save(
                                waitingUser.apply {
                                    status = EntryStatus.CALL
                                    callCount = callCount++
                                },
                            )
                        }

                        EntryStatus.NO_SHOW -> {
                            sendMessageByStatus(
                                status = EntryStatus.NO_SHOW,
                                phoneNum = waitingUser.phoneNumber!!,
                                shopName = shopInfo.name,
                            )

                            waitingRepository.save(
                                waitingUser.apply {
                                    // callCount = 0이어도 상관없이 노쇼 처리 가능
                                    status = EntryStatus.NO_SHOW
                                },
                            )
                        }

                        else -> throw InvalidParameterException("유효하지 않은 요청입니다.")
                    }
                }

                EntryStatus.CALL -> {
                    when (request.entryStatus) {
                        EntryStatus.CALL -> {
                            sendMessageByStatus(
                                status = EntryStatus.CALL,
                                phoneNum = waitingUser.phoneNumber!!,
                                shopName = shopInfo.name,
                            )

                            waitingRepository.save(
                                waitingUser.apply {
                                    callCount = callCount++
                                },
                            )
                        }

                        EntryStatus.ENTRY -> {
                            sendMessageByStatus(
                                status = EntryStatus.ENTRY,
                                phoneNum = waitingUser.phoneNumber!!,
                                shopName = shopInfo.name,
                            )

                            waitingRepository.save(
                                waitingUser.apply {
                                    status = EntryStatus.ENTRY
                                    enteredAt = LocalDateTime.now()
                                },
                            )
                        }

                        EntryStatus.NO_SHOW -> {
                            sendMessageByStatus(
                                status = EntryStatus.NO_SHOW,
                                phoneNum = waitingUser.phoneNumber!!,
                                shopName = shopInfo.name,
                            )

                            waitingRepository.save(
                                waitingUser.apply {
                                    status = EntryStatus.NO_SHOW
                                },
                            )
                        }

                        else -> throw InvalidParameterException("유효하지 않은 요청입니다.")
                    }
                }

                EntryStatus.CANCELED -> {
                    throw InvalidParameterException("대기가 취소된 유저입니다. 다시 대기 등록 후 요청해주세요.")
                }

                EntryStatus.NO_SHOW -> {
                    if (request.entryStatus == EntryStatus.WAIT) {
                        sendMessageByStatus(
                            status = EntryStatus.WAIT,
                            phoneNum = waitingUser.phoneNumber!!,
                            shopName = shopInfo.name,
                        )

                        // TODO : 최하단으로 안해도 X / 그대로 돌아오기 → 정렬에 대해서는 한번 더 로직 보고 검토해서 정하기
                        waitingRepository.save(
                            waitingUser.apply {
                                status = EntryStatus.WAIT
                            },
                        )
                    } else {
                        throw InvalidParameterException("유효하지 않은 요청입니다.")
                    }
                }

                else -> throw InvalidParameterException("유효하지 않은 요청입니다.")
            }
        } ?: throw InvalidParameterException("상태 변경을 할 수 있는 유저가 아닙니다.")

        return result.toResponse()
    }

    private fun validatePhoneNumber(phoneNum: String) {
        val regex = "^\\s*(010|011|012|013|014|015|016|017|018|019)(-|\\)|\\s)*(\\d{3,4})(-|\\s)*(\\d{4})\\s*$"
        val p = Pattern.compile(regex)
        val m = p.matcher(phoneNum)

        if (!m.matches()) throw InvalidParameterException("유효한 휴대폰 번호가 아닙니다.")
    }

    private fun sendUserPush(phoneNum: String, title: String, body: String) {
        userPushService.sendPushMessage(
            targetToken = userPushService.getUserPushToken(phoneNum)?.pushToken
                ?: throw InvalidParameterException("해당 유저에게 PUSH를 보낼 수 없습니다."),
            title = title,
            body = body,
        )
    }

    private fun sendMessageByStatus(phoneNum: String, shopName: String, status: EntryStatus) {
        when (status) {
            EntryStatus.CALL -> {
                sendUserPush(
                    phoneNum = phoneNum,
                    title = "[Wait-For-Me] 입장 안내",
                    body = "$shopName 에서 고객님을 기다리고 있어요! 지금 입장해주세요!",
                )
            }

            EntryStatus.NO_SHOW -> {
                sendUserPush(
                    phoneNum = phoneNum,
                    title = "[Wait-For-Me] 노쇼 안내",
                    body = "$shopName 에서 고객님을 노쇼 처리했음을 알려드립니다.",
                )
            }

            EntryStatus.ENTRY -> {
                sendUserPush(
                    phoneNum = phoneNum,
                    title = "[Wait-For-Me] 입장 완료 안내",
                    body = "$shopName 입장을 완료했어요! 즐거운 시간 되시길 바래요!",
                )
            }

            EntryStatus.WAIT -> {
                sendUserPush(
                    phoneNum = phoneNum,
                    title = "[Wait-For-Me] 대기 신청 안내",
                    body = "$shopName 현장 대기를 신청했어요! 조금만 기다려주세요! ",
                )
            }

            else -> {
                throw InvalidParameterException("PUSH 메시지 전송에 실패하였습니다. (status : $status)")
            }
        }
    }
}

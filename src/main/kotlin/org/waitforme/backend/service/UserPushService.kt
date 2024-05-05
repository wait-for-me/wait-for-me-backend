package org.waitforme.backend.service

import org.springframework.stereotype.Service
import org.waitforme.backend.entity.user.UserPush
import org.waitforme.backend.model.dto.wait.UserPushTokenRequest
import org.waitforme.backend.model.dto.wait.toEntity
import org.waitforme.backend.repository.user.UserPushRepository
import org.waitforme.backend.repository.user.UserRepository

@Service
class UserPushService(
    private val userPushRepository: UserPushRepository,
    private val firebaseCloudMessageService: FirebaseCloudMessageService,
    private val userRepository: UserRepository,
) {

    fun registerUserPushToken(request: UserPushTokenRequest) {
        val userPush =
            userRepository.findByPhoneNumber(phoneNumber = request.phoneNumber)?.let { user ->
                // userId가 있으면 조회해서 있을 경우 Update, 없으면 insert
                userPushRepository.findByUserId(userId = user.id)?.let {
                    request.toEntity(id = it.id, userId = user.id)
                } ?: request.toEntity(userId = user.id)
            } ?: request.toEntity() // 비회원의 경우

        userPushRepository.save(userPush)
    }

    fun sendPushMessage(targetToken: String, title: String, body: String) {
        firebaseCloudMessageService.sendMessageTo(
            targetToken = targetToken,
            title = title,
            body = body,
        )
    }

    fun getUserPushToken(userId: Int): UserPush? {
        return userPushRepository.findByUserId(userId)
    }
}

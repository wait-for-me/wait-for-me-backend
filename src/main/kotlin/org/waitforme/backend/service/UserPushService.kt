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
        // 조회해서 있을 경우 Update, 없으면 insert
        val userPush =
            userPushRepository.findByPhoneNumber(phoneNumber = request.phoneNumber)?.let {
                request.toEntity(id = it.id)
            } ?: request.toEntity()

        userPushRepository.save(userPush)
    }

    fun sendPushMessage(targetToken: String, title: String, body: String) {
        firebaseCloudMessageService.sendMessageTo(
            targetToken = targetToken,
            title = title,
            body = body,
        )
    }

    fun getUserPushToken(phoneNum: String): UserPush? {
        return userPushRepository.findByPhoneNumber(phoneNum)
    }
}

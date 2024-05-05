package org.waitforme.backend.service

import org.springframework.stereotype.Service
import org.waitforme.backend.entity.user.UserPush
import org.waitforme.backend.model.dto.wait.UserPushTokenRequest
import org.waitforme.backend.model.dto.wait.toEntity
import org.waitforme.backend.repository.user.UserPushRepository

@Service
class UserPushService(
    private val userPushRepository: UserPushRepository,
    private val firebaseCloudMessageService: FirebaseCloudMessageService,
) {

    fun registerUserPushToken(request: UserPushTokenRequest) {
        val userPush = userPushRepository.findByUserId(userId = request.userId)?.let {
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

    fun getUserPushToken(userId: Int): UserPush? {
        return userPushRepository.findByUserId(userId)
    }
}

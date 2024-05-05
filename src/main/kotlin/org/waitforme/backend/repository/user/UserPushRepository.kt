package org.waitforme.backend.repository.user

import org.springframework.data.repository.CrudRepository
import org.waitforme.backend.entity.user.UserPush

interface UserPushRepository : CrudRepository<UserPush, Int> {

    fun findByUserId(userId: Int): UserPush?
}

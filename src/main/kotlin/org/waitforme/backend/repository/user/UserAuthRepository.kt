package org.waitforme.backend.repository.user

import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository
import org.waitforme.backend.entity.user.UserAuth

@Repository
interface UserAuthRepository : CrudRepository<UserAuth, Int> {

    fun findByPhoneNumber(phoneNumber: String): UserAuth?
}

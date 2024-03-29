package org.waitforme.backend.repository.user

import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository
import org.waitforme.backend.entity.user.User
import org.waitforme.backend.enums.Provider

@Repository
interface UserRepository : CrudRepository<User, Int> {

    fun findByProviderAndPhoneNumber(provider: Provider, phoneNumber: String): User?

    fun findByProviderAndSnsId(provider: Provider, snsId: String): User?

    fun findByPhoneNumber(phoneNumber: String): User?

    fun existsByName(name: String): Boolean
}

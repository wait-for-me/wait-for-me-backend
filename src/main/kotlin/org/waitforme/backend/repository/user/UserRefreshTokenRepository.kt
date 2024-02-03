package org.waitforme.backend.repository.user

import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository
import org.waitforme.backend.entity.user.UserRefreshToken

@Repository
interface UserRefreshTokenRepository : CrudRepository<UserRefreshToken, Int> {
    fun findByRefreshToken(refreshToken: String): UserRefreshToken?
}

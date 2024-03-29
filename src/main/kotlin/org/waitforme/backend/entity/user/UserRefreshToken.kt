package org.waitforme.backend.entity.user

import org.waitforme.backend.common.BaseEntity
import java.time.LocalDateTime
import javax.persistence.*

@Entity
@Table(name = "user_refresh_token")
data class UserRefreshToken(
    @Id
    @GeneratedValue
    val id: Int? = null,
    val userId: Int,
    @Column(columnDefinition = "TEXT")
    var refreshToken: String,
) : BaseEntity() {
    fun updateRefreshToken(refreshToken: String) {
        this.refreshToken = refreshToken
        this.updatedAt = LocalDateTime.now()
    }
}
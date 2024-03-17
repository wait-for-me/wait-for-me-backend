package org.waitforme.backend.entity.user

import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.UserDetails
import org.waitforme.backend.common.BaseEntity
import org.waitforme.backend.enums.GenderType
import org.waitforme.backend.enums.Provider
import org.waitforme.backend.enums.UserRole
import java.time.LocalDate
import java.time.LocalDateTime
import javax.persistence.*
import javax.security.auth.message.AuthException

@Entity
@Table(
    name = "`user`",
    indexes = [
        Index(name = "idx_phone_number", columnList = "phoneNumber", unique = true), // index (unique = false (default))
    ]
)
data class User(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Int = 0,
    val provider: Provider,
    val phoneNumber: String,
    val snsId: String? = null,
    var email: String? = null,
    var name: String,
    private var password: String? = null,
    var birthedAt: LocalDate? = null,
    var gender: GenderType? = null,
    val profileImage: String? = null,
    var isOwner: Boolean = false,
    var isAuth: Boolean = false, // 인증 여부, sns로 등록 시 자동 인증, local은 회원 가입 시 인증 절차 필요
    var isAdult: Boolean = false,
    var isDeleted: Boolean = false,
    var deletedAt: LocalDateTime? = null,
) : BaseEntity(), UserDetails {
    override fun getAuthorities(): Collection<GrantedAuthority> {
        return if (isOwner) {
            UserRole.OWNER.authorities.map { SimpleGrantedAuthority(it) }
        } else {
            UserRole.USER.authorities.map { SimpleGrantedAuthority(it) }
        }
    }

    override fun getPassword(): String? {
        return password // TODO: null이어도 되나..? 확인 필요
    }

    override fun getUsername(): String {
        return name
    }

    override fun isEnabled(): Boolean {
        return !isDeleted
    }

    override fun isAccountNonExpired(): Boolean {
        return true
    }

    override fun isAccountNonLocked(): Boolean {
        return true
    }

    override fun isCredentialsNonExpired(): Boolean {
        return true
    }

    fun checkDeletedUser() {
        if (isDeleted || deletedAt != null) {
            throw AuthException("탈퇴 처리된 계정입니다.")
        }
    }

    fun checkAuthUser() {
        if (!isAuth) {
            throw AuthException("인증이 확인되지 않은 계정입니다.")
        }
    }
}

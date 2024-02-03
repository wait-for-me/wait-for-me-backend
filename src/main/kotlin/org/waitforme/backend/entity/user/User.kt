package org.waitforme.backend.entity.user

import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.UserDetails
import org.waitforme.backend.common.BaseEntity
import org.waitforme.backend.enums.GenderType
import org.waitforme.backend.enums.Provider
import org.waitforme.backend.enums.UserRole
import java.time.LocalDateTime
import javax.persistence.*
import javax.security.auth.message.AuthException

@Table(name = "`user`")
@Entity
data class User(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Int = 0,
    val provider: Provider,
    val phoneNumber: String,
    val snsId: String? = null,
    val email: String? = null,
    var name: String,
    private val password: String? = null,
    var birthedAt: LocalDateTime? = null,
    var gender: GenderType? = null,
    val profileImage: String? = null,
    val isOwner: Boolean = false,
    val isAuth: Boolean = false, // 인증 여부, sns로 등록 시 자동 인증, local은 회원 가입 시 인증 절차 필요
    val isAdult: Boolean = false,
    val isDeleted: Boolean = false,
    var deletedAt: LocalDateTime? = null,
) : BaseEntity(), UserDetails {
    override fun getAuthorities(): Collection<GrantedAuthority> {
        return if (isOwner) {
            listOf<GrantedAuthority>(SimpleGrantedAuthority(UserRole.OWNER.name))
        } else {
            listOf<GrantedAuthority>(SimpleGrantedAuthority(UserRole.USER.name))
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

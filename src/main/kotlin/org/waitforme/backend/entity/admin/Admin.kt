package org.waitforme.backend.entity.admin

import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.UserDetails
import org.waitforme.backend.common.BaseEntity
import org.waitforme.backend.enums.AdminAuthority
import org.waitforme.backend.enums.UserRole
import java.time.LocalDateTime
import javax.persistence.*

@Table(name = "admin")
@Entity
data class Admin(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Int = 0,
    val email: String,
    val name: String? = null,
    private val password: String,
    val authority: AdminAuthority,
    val isDeleted: Boolean? = false,
    var deletedAt: LocalDateTime? = null,
) : BaseEntity(),
    UserDetails {
    override fun getAuthorities(): Collection<GrantedAuthority> {
        return UserRole.ADMIN.authorities.map { SimpleGrantedAuthority(it) }
    }

    override fun getPassword(): String {
        return password
    }

    override fun getUsername(): String? {
        return name
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

    override fun isEnabled(): Boolean {
        return true
    }
}

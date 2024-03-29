package org.waitforme.backend.model

import org.springframework.security.core.GrantedAuthority
import org.waitforme.backend.entity.user.User
import org.waitforme.backend.enums.Provider

class LoginUser(
    val id: Int,
    val provider: Provider,
    val email: String?,
    val name: String,
    private val password: String?,
    private val authorities: Collection<GrantedAuthority>,
) : org.springframework.security.core.userdetails.User(
    id.toString(),
    password,
    authorities,
)

fun User.toLoginUser() = LoginUser(
    id = this.id,
    provider = provider,
    email = email,
    password = password,
    authorities = authorities,
    name = name,
)

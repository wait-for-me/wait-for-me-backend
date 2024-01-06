package org.waitforme.backend.model

import org.springframework.security.core.userdetails.User
import org.waitforme.backend.entity.admin.Admin

class LoginAdmin(
    val admin: Admin,
) : User(
    admin.id.toString(),
    admin.password,
    admin.authorities,
)

package org.waitforme.backend.model.request.user

import org.waitforme.backend.enums.GenderType
import java.time.LocalDateTime

data class UserInfoRequest(
    val name: String,
    val birthedAt: LocalDateTime? = null,
    val gender: GenderType? = null,
//    val profileImage: FilePart? = null,
)

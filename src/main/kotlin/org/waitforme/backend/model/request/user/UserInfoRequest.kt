package org.waitforme.backend.model.request.user

import org.springframework.http.codec.multipart.FilePart
import org.waitforme.backend.enums.GenderType
import java.time.LocalDateTime

data class UserInfoRequest(
    val id: Int = 0,
    val name: String,
    val birthedAt: LocalDateTime? = null,
    val gender: GenderType? = null,
    val profileImage: FilePart? = null,
)

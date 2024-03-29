package org.waitforme.backend.model.request.user

import org.waitforme.backend.enums.GenderType
import java.time.LocalDate

data class UserInfoRequest(
    val name: String,
    val birthedAt: LocalDate? = null,
    val gender: GenderType? = null,
//    val profileImage: FilePart? = null,
)

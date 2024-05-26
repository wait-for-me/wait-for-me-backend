package org.waitforme.backend.model.request.user

import org.springframework.web.multipart.MultipartFile
import org.waitforme.backend.enums.GenderType
import java.time.LocalDate
import javax.validation.constraints.Pattern

data class UserInfoRequest(
    val name: String,
    val birthedAt: LocalDate? = null,
    val gender: GenderType? = null,
    val profileImage: MultipartFile? = null,
    @field:Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[\$@\$!%*#?&])[A-Za-z\\d\$@\$!%*#?&]{8,}\$")
    val password: String? = null,
)

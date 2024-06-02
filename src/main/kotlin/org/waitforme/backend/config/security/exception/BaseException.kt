package org.waitforme.backend.config.security.exception

import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.RestController
import org.waitforme.backend.enums.BaseResponseCode

class BaseException(baseResponse: BaseResponseCode): RuntimeException() {
    val baseResponseCode: BaseResponseCode = baseResponse
}
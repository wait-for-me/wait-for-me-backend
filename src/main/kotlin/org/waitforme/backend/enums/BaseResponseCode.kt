package org.waitforme.backend.enums

import org.springframework.http.HttpStatus


enum class BaseResponseCode(val status: HttpStatus, val message: String) {
    BAD_REQUEST(HttpStatus.BAD_REQUEST, "잘못된 요청입니다"),
    INTERNAL(HttpStatus.INTERNAL_SERVER_ERROR, "서버 에러입니다."),

}
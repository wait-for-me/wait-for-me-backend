package org.waitforme.backend.config.security.exception

import io.jsonwebtoken.security.InvalidKeyException
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice

@RestControllerAdvice
class ExceptionHandler {
    @ExceptionHandler(BaseException::class)
    protected fun handleBaseException(e: BaseException): ResponseEntity<BaseRes> {
        return ResponseEntity.status(e.baseResponseCode.status)
            .body(
                BaseRes(
                    e.baseResponseCode.status,
                    e.baseResponseCode.message
                )
            )
    }

    @ExceptionHandler(InvalidKeyException::class)
    fun handlerInvalidKeyException(e: InvalidKeyException): ResponseEntity<BaseRes> {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
            .body(
                BaseRes(
                    HttpStatus.BAD_REQUEST,
                    e.message!!
                )
            )
    }
}
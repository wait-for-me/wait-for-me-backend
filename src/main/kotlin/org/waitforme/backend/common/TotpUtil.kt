package org.waitforme.backend.common

import dev.turingcomplete.kotlinonetimepassword.HmacAlgorithm
import dev.turingcomplete.kotlinonetimepassword.TimeBasedOneTimePasswordConfig
import dev.turingcomplete.kotlinonetimepassword.TimeBasedOneTimePasswordGenerator
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.stereotype.Component
import java.util.Date
import java.util.concurrent.TimeUnit
import javax.annotation.PostConstruct

@Component
@ConfigurationProperties(prefix = "totp")
class TotpUtil() {
    lateinit var secret: String
    lateinit var validTime: String

    lateinit var config: TimeBasedOneTimePasswordConfig
    lateinit var timeBasedOneTimePasswordGenerator: TimeBasedOneTimePasswordGenerator

    @PostConstruct
    fun init() {
        config = TimeBasedOneTimePasswordConfig(
            codeDigits = 6,
            hmacAlgorithm = HmacAlgorithm.SHA1,
            timeStep = 10000,
            timeStepUnit = TimeUnit.MILLISECONDS,
        )

        timeBasedOneTimePasswordGenerator = TimeBasedOneTimePasswordGenerator(secret.toByteArray(), config)
    }

    fun generateTotp(date: Date): String {
        return timeBasedOneTimePasswordGenerator.generate(date = date)
    }

    fun isValidTotp(code: String, date: Date): Boolean {
        return if (checkValidTime(code, date)) timeBasedOneTimePasswordGenerator.isValid(code, date) else false
    }

    fun checkValidTime(code: String, date: Date): Boolean {
        val now = System.currentTimeMillis()
        val startEpochMillis = date.toInstant().epochSecond * 1000
        return (now - startEpochMillis) < validTime.toLong()
    }
}

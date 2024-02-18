package org.waitforme.backend.common

import net.nurigo.sdk.NurigoApp.initialize
import net.nurigo.sdk.message.exception.NurigoMessageNotReceivedException
import net.nurigo.sdk.message.model.Message
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.stereotype.Component

@Component
@ConfigurationProperties(prefix = "sms")
class SmsUtil {

    lateinit var fromNumber: String
    lateinit var apiKey: String
    lateinit var apiSecretKey: String

    fun sendSms(toPhoneNumber: String, authText: String): Boolean {
        val messageService = initialize(apiKey, apiSecretKey, "https://api.solapi.com")
        val message = Message()
        message.apply {
            from = fromNumber // 계정에서 등록한 발신번호 입력
            to = toPhoneNumber // 수신번호 입력
            text = "[WAIT-FOR-ME] 인증번호를 입력해주세요. $authText" // SMS는 한글 45자, 영자 90자까지 입력할 수 있습니다.
        }

        return try {
            // send 메소드로 ArrayList<Message> 객체를 넣어도 동작합니다!
            messageService.send(message)
            true
        } catch (exception: NurigoMessageNotReceivedException) {
            println(exception.failedMessageList)
            println(exception.message)
            false
        } catch (exception: Exception) {
            println(exception.message)
            false
        }
    }
}

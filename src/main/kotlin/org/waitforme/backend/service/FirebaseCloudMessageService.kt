package org.waitforme.backend.service

import com.fasterxml.jackson.core.JsonParseException
import com.fasterxml.jackson.core.JsonProcessingException
import com.fasterxml.jackson.databind.ObjectMapper
import com.google.auth.oauth2.GoogleCredentials
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseOptions
import com.google.firebase.messaging.Message
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.springframework.beans.factory.annotation.Value
import org.springframework.core.io.ClassPathResource
import org.springframework.http.HttpHeaders
import org.springframework.stereotype.Component
import java.io.FileInputStream
import java.io.IOException
import java.util.*
import javax.annotation.PostConstruct

@Component
class FirebaseCloudMessageService(private val objectMapper: ObjectMapper) {

    @Value("\${firebase.url}")
    lateinit var API_URL: String

//    @PostConstruct
//    private fun initFirebaseOptions() {
//        val accessKey = FileInputStream("firebase_service_key.json")
//
//        val options = FirebaseOptions.builder()
//            .setCredentials(
//                GoogleCredentials.fromStream(accessKey),
//            ).build()
//
//        FirebaseApp.initializeApp(options)
//    }

    @Throws(IOException::class)
    fun sendMessageTo(targetToken: String, title: String, body: String) {
        val message = makeMessage(targetToken, title, body)

        val client = OkHttpClient()
        val requestBody = message.toRequestBody("application/json; charset=utf-8".toMediaType())

        val request = Request.Builder()
            .url(API_URL)
            .post(requestBody)
            .addHeader(HttpHeaders.AUTHORIZATION, "Bearer " + getAccessToken())
            .addHeader(HttpHeaders.CONTENT_TYPE, "application/json; UTF-8")
            .build()

        val response = client.newCall(request).execute()

        println(response.body?.string())
    }

    @Throws(JsonParseException::class, JsonProcessingException::class)
    private fun makeMessage(targetToken: String, title: String, body: String): String {
        val fcmMessage =
            Message.builder()
                .putData("title", title)
                .putData("body", body)
                .setToken(targetToken)
//            .setNotification(
//                Notification.builder()
//                    .setTitle(title)
//                    .setBody(body)
//                    .setImage(null)
//                    .build()
//            )
                .build()

        return objectMapper.writeValueAsString(fcmMessage)
    }

    @Throws(IOException::class)
    private fun getAccessToken(): String {
        val firebaseConfigPath = "firebase_service_key.json"

        val googleCredentials = GoogleCredentials
            .fromStream(ClassPathResource(firebaseConfigPath).inputStream)
            .createScoped(listOf("https://www.googleapis.com/auth/cloud-platform"))

        googleCredentials.refreshIfExpired()
        return googleCredentials.accessToken.tokenValue
    }
}

package org.waitforme.backend.service

import com.google.auth.oauth2.GoogleCredentials
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseOptions
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.messaging.Message
import com.google.firebase.messaging.Notification
import org.springframework.core.io.ClassPathResource
import org.springframework.stereotype.Component
import java.io.IOException
import java.util.*
import javax.annotation.PostConstruct

@Component
class FirebaseCloudMessageService() {

    lateinit var firebaseMessaging: FirebaseMessaging

    @PostConstruct
    private fun initFirebase() {
        val firebaseConfigPath = "firebase_service_key.json"

        val options = FirebaseOptions.builder()
            .setCredentials(
                GoogleCredentials.fromStream(ClassPathResource(firebaseConfigPath).inputStream),
            ).build()

        val firebaseApp = FirebaseApp.initializeApp(options)

        firebaseMessaging = FirebaseMessaging.getInstance(firebaseApp)
    }

    @Throws(IOException::class)
    fun sendMessage(targetToken: String, title: String, body: String): String {
        val fcmMessage = Message.builder()
            .setToken(targetToken)
            .setNotification(
                Notification.builder()
                    .setTitle(title)
                    .setBody(body)
                    .setImage(null)
                    .build(),
            )
            .build()

        val result = firebaseMessaging.send(fcmMessage)

        println(result)

        return result
    }
}

package org.waitforme.backend.util

import com.amazonaws.auth.AWSStaticCredentialsProvider
import com.amazonaws.auth.BasicAWSCredentials
import com.amazonaws.services.sqs.AmazonSQSAsync
import com.amazonaws.services.sqs.AmazonSQSAsyncClientBuilder
import org.springframework.beans.factory.annotation.Value
import org.springframework.cloud.aws.messaging.core.QueueMessagingTemplate
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Primary
import org.springframework.messaging.support.MessageBuilder


@Configuration
class SqsUtil {

    @Value("\${cloud.aws.credentials.access-key}")
    private val accessKey: String? = null

    @Value("\${cloud.aws.credentials.secret-key}")
    private val secretKey: String? = null

    @Value("\${cloud.aws.region.static}")
    private val region: String? = null

    private lateinit var queueMessagingTemplate: QueueMessagingTemplate

    @Primary
    @Bean
    fun amazonSQSAsync(): AmazonSQSAsync {
        val basicAwsCredentials = BasicAWSCredentials(accessKey, secretKey)
        return AmazonSQSAsyncClientBuilder.standard()
            .withRegion(region)
            .withCredentials(AWSStaticCredentialsProvider(basicAwsCredentials))
            .build()
    }

    @Bean
    fun queueMessagingTemplate(amazonSQS: AmazonSQSAsync): QueueMessagingTemplate {
        this.queueMessagingTemplate = QueueMessagingTemplate(amazonSQS)
        return queueMessagingTemplate
    }

    fun sendMessage(message: String) {
        val newMessage = MessageBuilder.withPayload(message).build()
        queueMessagingTemplate.send("waiting-sqs", newMessage)
    }
}
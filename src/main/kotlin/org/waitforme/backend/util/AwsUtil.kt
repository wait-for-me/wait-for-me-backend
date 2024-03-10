package org.waitforme.backend.util

import com.amazonaws.auth.AWSStaticCredentialsProvider
import com.amazonaws.auth.BasicAWSCredentials
import com.amazonaws.services.s3.AmazonS3Client
import com.amazonaws.services.s3.AmazonS3ClientBuilder
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class AwsUtil {
    @Value("\${cloud.aws.credentials.access-key}")
    lateinit var accessKey: String
    @Value("\${cloud.aws.credentials.secret-key}")
    lateinit var secretKey: String
    @Value("\${cloud.aws.region.static}")
    lateinit var region: String

    @Bean
    fun amazonS3Client(): AmazonS3Client {
        val accessKey = accessKey
        val secretKey = secretKey
        val awsCredentials = BasicAWSCredentials(accessKey, secretKey)
        return AmazonS3ClientBuilder.standard()
            .withRegion(region)
            .withCredentials(AWSStaticCredentialsProvider(awsCredentials))
            .build()
        as AmazonS3Client
    }
}
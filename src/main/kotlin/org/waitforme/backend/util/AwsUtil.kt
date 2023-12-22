package org.waitforme.backend.util

import com.amazonaws.auth.AWSStaticCredentialsProvider
import com.amazonaws.auth.BasicAWSCredentials
import com.amazonaws.services.s3.AmazonS3Client
import com.amazonaws.services.s3.AmazonS3ClientBuilder
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration("cloud.aws")
class AwsUtil {
    lateinit var credential: Map<String, String>
    lateinit var region: Map<String, String>

    @Bean
    fun amazonS3Client(): AmazonS3Client {
        val accessKey = credential["access-key"]!!
        val secretKey = credential["secret-key"]!!
        val awsCredentials = BasicAWSCredentials(accessKey, secretKey)
        return AmazonS3ClientBuilder.standard()
            .withRegion(region["static"]!!)
            .withCredentials(AWSStaticCredentialsProvider(awsCredentials))
            .build()
        as AmazonS3Client
    }
}
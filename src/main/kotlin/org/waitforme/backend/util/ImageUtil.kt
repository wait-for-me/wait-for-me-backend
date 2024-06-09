package org.waitforme.backend.util

import com.amazonaws.services.s3.model.ObjectMetadata
import com.amazonaws.services.s3.model.PutObjectRequest
import com.amazonaws.util.IOUtils
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import org.springframework.web.multipart.MultipartFile
import org.waitforme.backend.enums.FileType
import java.io.ByteArrayInputStream
import java.util.*

@Component
class ImageUtil(
    private val awsUtil: AwsUtil
) {
    @Value("\${cloud.aws.s3.bucket}")
    lateinit var bucket: String

    fun uploadFile(file: MultipartFile, fileType: FileType): String {
        val convertInfo = convertFile(file)
        val fileName = fileType.prefix + UUID.randomUUID().toString().plus("." + file.name.substringAfterLast("."))
        awsUtil.amazonS3Client().putObject(
            PutObjectRequest(
                bucket,
                fileName,
                convertInfo.first,
                convertInfo.second
            )
        )

        return awsUtil.amazonS3Client().getUrl(bucket, fileName).toString()
    }

    fun convertFile(file: MultipartFile): Pair<ByteArrayInputStream, ObjectMetadata> {
        val bytes = IOUtils.toByteArray(file.inputStream)
        val objMeta = ObjectMetadata()
        objMeta.contentLength = bytes.size.toLong()

        val byteArray = ByteArrayInputStream(bytes)

        return Pair(byteArray, objMeta)
    }
}
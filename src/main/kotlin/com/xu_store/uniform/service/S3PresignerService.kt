package com.xu_store.uniform.service

import com.xu_store.uniform.model.PresignResult
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider
import software.amazon.awssdk.regions.Region
import software.amazon.awssdk.services.s3.model.PutObjectRequest
import software.amazon.awssdk.services.s3.presigner.S3Presigner
import software.amazon.awssdk.services.s3.presigner.model.PutObjectPresignRequest
import java.time.Duration




@Service
class S3PresignerService(
    @Value("\${aws.s3.bucket-name}") private val bucketName: String,
    @Value("\${aws.s3.region}") regionValue: String,
    @Value("\${aws.access-key}") accessKey: String,
    @Value("\${aws.secret-key}") secretKey: String,
    @Value("\${aws.cloudfront.base-url}") private val cloudFrontBaseUrl: String
) {
    private val region: Region = Region.of(regionValue)
    private val presigner: S3Presigner = S3Presigner.builder()
        .region(region)
        .credentialsProvider(
            StaticCredentialsProvider.create(
                AwsBasicCredentials.create(accessKey, secretKey)
            )
        )
        .build()

    fun generatePresignedUrl(productId: Long, filename: String, contentType: String): PresignResult {
        val objectKey = "products/$productId/${System.currentTimeMillis()}_$filename"
        val putObjectRequest = PutObjectRequest.builder()
            .bucket(bucketName)
            .key(objectKey)
            .contentType(contentType)
            .build()

        val presignRequest = PutObjectPresignRequest.builder()
            .signatureDuration(Duration.ofMinutes(10))
            .putObjectRequest(putObjectRequest)
            .build()

        val presignedUrl = presigner.presignPutObject(presignRequest).url().toString()
        val publicUrl = "$cloudFrontBaseUrl/$objectKey"

        return PresignResult(presignedUrl, objectKey, publicUrl)
    }
}

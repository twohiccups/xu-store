package com.xu_store.uniform.controller

import com.xu_store.uniform.dto.PresignRequest
import com.xu_store.uniform.dto.PresignResponse
import org.springframework.stereotype.Controller
import com.xu_store.uniform.service.S3PresignerService
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/images")
class ImagesController(
    private val s3PresignerService: S3PresignerService
) {

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/presign/{productId}")
    fun generatePresignedUrl(
        @PathVariable productId: Long,
        @RequestBody request: PresignRequest
    ): PresignResponse {
        val presignedResult = s3PresignerService.generatePresignedUrl(
            productId,
            request.filename,
            request.contentType
        )
        return PresignResponse.from(presignedResult)
    }
}

package com.xu_store.uniform.dto

import com.xu_store.uniform.model.PresignResult

data class PresignResponse(val presignedUrl: String, val objectKey: String, val publicUrl: String) {
    companion object {
        fun from(presignResult: PresignResult): PresignResponse {
            return PresignResponse(
                presignedUrl = presignResult.presignedUrl,
                objectKey = presignResult.objectKey,
                publicUrl = presignResult.publicUrl
            )
        }
    }
}

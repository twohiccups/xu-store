package com.xu_store.uniform.advice

import com.xu_store.uniform.exception.InsufficientCreditsException
import com.xu_store.uniform.exception.InvalidProductVariationException
import com.xu_store.uniform.exception.UserWithoutTeamException
import org.springframework.http.HttpStatus
import org.springframework.http.ProblemDetail
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice

@RestControllerAdvice
class GlobalExceptionAdvice {

    @ExceptionHandler(IllegalArgumentException::class)
    fun illegalArgument(e: IllegalArgumentException) : ProblemDetail =
        ProblemDetail.forStatusAndDetail(HttpStatus.UNPROCESSABLE_ENTITY, e.message ?:  "Invalid request")

    @ExceptionHandler(InsufficientCreditsException::class)
    fun insufficientCredits(e: InsufficientCreditsException) : ProblemDetail =
        ProblemDetail.forStatusAndDetail(HttpStatus.PAYMENT_REQUIRED, e.message ?: "Insufficient credits")

    // Useful for @Valid annotation in the controller
    @ExceptionHandler(MethodArgumentNotValidException::class)
    fun validation(e: MethodArgumentNotValidException): ProblemDetail =
        ProblemDetail.forStatusAndDetail(HttpStatus.UNPROCESSABLE_ENTITY, "Validation failed").apply {
            val errors = e.bindingResult.fieldErrors.associate { it.field to (it.defaultMessage ?: "invalid") }
            setProperty("errors", errors)
        }

    @ExceptionHandler(InvalidProductVariationException::class)
    fun invalidVariation(e: InvalidProductVariationException): ProblemDetail =
        ProblemDetail.forStatusAndDetail(
            HttpStatus.UNPROCESSABLE_ENTITY,
            e.message ?: "Invalid product variation"
        ).apply {
            setProperty("code", "INVALID_PRODUCT_VARIATION")
            e.variationId?.let { setProperty("variationId", it) }
        }

    @ExceptionHandler(UserWithoutTeamException::class)
    fun userWithoutTeam(e: UserWithoutTeamException): ProblemDetail =
        ProblemDetail.forStatusAndDetail(
            HttpStatus.UNPROCESSABLE_ENTITY,
            e.message ?: "User does not belong to a team"
        ).apply {
            setProperty("code", "USER_WITHOUT_TEAM")
            e.userId?.let { setProperty("userId", it) }
        }

}
package com.tinyurl.controller

import com.tinyurl.controller.validation.RequestUrlValidator.Companion.URL_VALIDATION_ERROR
import com.tinyurl.exception.UrlNotFoundException
import org.springframework.http.HttpStatus.BAD_REQUEST
import org.springframework.http.HttpStatus.NOT_FOUND
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.ResponseBody
import java.time.Instant
import java.time.Instant.now

@ControllerAdvice
class ControllerExceptionHandler {

    @ResponseBody
    @ExceptionHandler(UrlNotFoundException::class)
    fun urlNotFoundExceptionHandler(e: UrlNotFoundException): ResponseEntity<ValidationError> {
        val validationError = ValidationError(
            timestamp = now(),
            status = NOT_FOUND.value(),
            error = NOT_FOUND.reasonPhrase,
            message = e.message,
        )
        return ResponseEntity(validationError, NOT_FOUND)
    }

    @ResponseBody
    @ExceptionHandler(MethodArgumentNotValidException::class)
    fun fieldValidationExceptionHandler(e: MethodArgumentNotValidException): ResponseEntity<ValidationError> {
        val validationError = ValidationError(
            timestamp = now(),
            status = BAD_REQUEST.value(),
            error = BAD_REQUEST.reasonPhrase,
            message = message(e),
        )
        return ResponseEntity(validationError, BAD_REQUEST)
    }

    private fun message(e: MethodArgumentNotValidException): String =
        with(e) {
            when {
                message.contains(URL_VALIDATION_ERROR) -> URL_VALIDATION_ERROR
                else -> message
            }
        }

    data class ValidationError(
        val timestamp: Instant,
        val status: Int,
        val error: String,
        val message: String,
    )
}

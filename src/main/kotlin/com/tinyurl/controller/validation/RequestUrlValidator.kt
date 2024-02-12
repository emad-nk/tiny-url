package com.tinyurl.controller.validation

import com.tinyurl.controller.dto.UrlRequestDTO
import com.tinyurl.controller.validation.RequestUrlValidator.Companion.URL_VALIDATION_ERROR
import jakarta.validation.Constraint
import jakarta.validation.ConstraintValidator
import jakarta.validation.ConstraintValidatorContext
import jakarta.validation.Payload
import org.apache.commons.validator.routines.UrlValidator
import org.springframework.stereotype.Component
import kotlin.annotation.AnnotationTarget.CLASS
import kotlin.reflect.KClass

@Component
class RequestUrlValidator : ConstraintValidator<ValidateUrl, UrlRequestDTO> {

    override fun isValid(urlRequestDTO: UrlRequestDTO, p1: ConstraintValidatorContext): Boolean {
        return urlValidator.isValid(urlRequestDTO.originalUrl)
    }

    companion object {
        private val urlValidator = UrlValidator()
        const val URL_VALIDATION_ERROR = "Not valid URL"
    }
}

@MustBeDocumented
@Constraint(validatedBy = [RequestUrlValidator::class])
@Target(allowedTargets = [CLASS])
@Retention(AnnotationRetention.RUNTIME)
annotation class ValidateUrl(
    val message: String = URL_VALIDATION_ERROR,
    val groups: Array<KClass<out Any>> = [],
    val payload: Array<KClass<out Payload>> = [],
)

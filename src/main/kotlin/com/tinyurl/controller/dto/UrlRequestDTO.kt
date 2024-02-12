package com.tinyurl.controller.dto

import com.tinyurl.controller.validation.ValidateUrl

@ValidateUrl
data class UrlRequestDTO(
    val originalUrl: String,
)

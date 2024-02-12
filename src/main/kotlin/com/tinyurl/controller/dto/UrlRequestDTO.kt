package com.tinyurl.controller.dto

import com.tinyurl.controller.ValidateUrl

@ValidateUrl
data class UrlRequestDTO(
    val originalUrl: String,
)

package com.tinyurl.property

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "tiny-url")
data class TinyUrlProperties(
    val baseUrl: String
)

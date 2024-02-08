package com.tinyurl.domain.model

import jakarta.persistence.Entity
import jakarta.persistence.Id

@Entity
data class Url(
    @Id
    val id: Long,
    val base62: String,
    val originalUrl: String,
)

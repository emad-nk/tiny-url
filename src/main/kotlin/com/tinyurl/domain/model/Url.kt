package com.tinyurl.domain.model

import jakarta.persistence.Entity
import jakarta.persistence.Id
import java.io.Serializable
import java.util.UUID.randomUUID

@Entity
data class Url(
    @Id
    val id: String = randomUUID().toString(),
    val sequenceId: Long,
    val tinyUrlWithoutDomain: String,
    val originalUrl: String,
) : Serializable {

    companion object {
        private const val serialVersionUID = 6498378134993474269L
    }
}

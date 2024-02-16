package com.tinyurl.service

import com.tinyurl.common.convertToBase62
import com.tinyurl.controller.dto.UrlRequestDTO
import com.tinyurl.controller.dto.UrlResponseDTO
import com.tinyurl.domain.model.Url
import com.tinyurl.domain.repository.UrlRepository
import com.tinyurl.exception.UrlNotFoundException
import com.tinyurl.property.TinyUrlProperties
import mu.KotlinLogging
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.dao.DataIntegrityViolationException
import org.springframework.stereotype.Service

@Service
@EnableConfigurationProperties(TinyUrlProperties::class)
class TinyUrlService(
    private val urlRepository: UrlRepository,
    private val tinyUrlProperties: TinyUrlProperties,
) {

    fun createTinyUrl(urlRequestDTO: UrlRequestDTO): UrlResponseDTO {
        urlRepository.findByOriginalUrl(urlRequestDTO.originalUrl)?.let {
            return UrlResponseDTO(tinyUrl = "${tinyUrlProperties.baseUrl}${it.tinyUrlWithoutDomain}")
        }
        val sequenceId = urlRepository.getNewSequenceId()

        return try {
            sequenceId.generateTinyUrl(urlRequestDTO.originalUrl)
        } catch (ex: DataIntegrityViolationException) {
            LOGGER.warn { "Url exists by id $sequenceId, generating a new id" }
            findNextAvailableId().generateTinyUrl(urlRequestDTO.originalUrl)
        }
    }

    fun getOriginalUrl(tinyUrlWithoutDomain: String): String {
        return urlRepository.findUrlByTinyUrlWithoutDomain(tinyUrlWithoutDomain = tinyUrlWithoutDomain)?.let {
            return it.originalUrl
        } ?: throw UrlNotFoundException("Tiny url ${tinyUrlProperties.baseUrl}$tinyUrlWithoutDomain not found")
    }

    private fun findNextAvailableId(): Long {
        urlRepository.setNewSequenceId(urlRepository.getLatestSequenceId())
        return urlRepository.getNewSequenceId()
    }

    private fun Long.generateTinyUrl(url: String): UrlResponseDTO {
        val tinyUrlWithoutDomain = convertToBase62(this)
        urlRepository.save(Url(sequenceId = this, tinyUrlWithoutDomain = tinyUrlWithoutDomain, originalUrl = url))
        return UrlResponseDTO(tinyUrl = "${tinyUrlProperties.baseUrl}$tinyUrlWithoutDomain")
    }

    companion object {
        private val LOGGER = KotlinLogging.logger { }
    }
}

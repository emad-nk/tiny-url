package com.tinyurl.service

import com.tinyurl.common.convertToBase62
import com.tinyurl.common.take7Chars
import com.tinyurl.controller.dto.UrlRequestDTO
import com.tinyurl.controller.dto.UrlResponseDTO
import com.tinyurl.domain.model.Url
import com.tinyurl.domain.repository.UrlRepository
import com.tinyurl.exception.UrlNotFoundException
import com.tinyurl.property.TinyUrlProperties
import mu.KotlinLogging
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@EnableConfigurationProperties(TinyUrlProperties::class)
class TinyUrlService(
    private val urlRepository: UrlRepository,
    private val tinyUrlProperties: TinyUrlProperties,
) {

    @Transactional
    fun createTinyUrl(urlRequestDTO: UrlRequestDTO): UrlResponseDTO {
        urlRepository.findByOriginalUrl(urlRequestDTO.originalUrl)?.let {
            return UrlResponseDTO(tinyUrl = "${tinyUrlProperties.baseUrl}${it.tinyUrlWithoutDomain}")
        }
        val id = urlRepository.getNewId()

        if (urlRepository.existsById(id)) {
            LOGGER.warn { "Url exists by id $id, generating a new id" }
            return findNextAvailableId().generateTinyUrl(urlRequestDTO.originalUrl)
        }
        return id.generateTinyUrl(urlRequestDTO.originalUrl)
    }

    fun getOriginalUrl(tinyUrlWithoutDomain: String): String {
        return urlRepository.findUrlByTinyUrlWithoutDomain(tinyUrlWithoutDomain = tinyUrlWithoutDomain)?.let {
            return it.originalUrl
        } ?: throw UrlNotFoundException("Tiny url ${tinyUrlProperties.baseUrl}$tinyUrlWithoutDomain not found")
    }

    /**
     * This is a rare case that probably will never happen
     */
    private fun findNextAvailableId(): Long {
        urlRepository.setNewId(urlRepository.getLatestId())
        return urlRepository.getNewId()
    }

    private fun Long.generateTinyUrl(url: String): UrlResponseDTO {
        val tinyUrlWithoutDomain = convertToBase62(this).take7Chars()
        urlRepository.save(Url(id = this, tinyUrlWithoutDomain = tinyUrlWithoutDomain, originalUrl = url))
        return UrlResponseDTO(tinyUrl = "${tinyUrlProperties.baseUrl}$tinyUrlWithoutDomain")
    }

    companion object {
        private val LOGGER = KotlinLogging.logger { }
    }
}

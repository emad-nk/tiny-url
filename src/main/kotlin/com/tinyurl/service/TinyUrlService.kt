package com.tinyurl.service

import com.tinyurl.common.convertToBase62
import com.tinyurl.common.take7Chars
import com.tinyurl.controller.dto.UrlRequestDTO
import com.tinyurl.controller.dto.UrlResponseDTO
import com.tinyurl.domain.model.Url
import com.tinyurl.domain.repository.UrlRepository
import com.tinyurl.exception.UrlNotFoundException
import com.tinyurl.property.TinyUrlProperties
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
        val id = urlRepository.getNewId()
        val tinyUrlWithoutDomain = convertToBase62(id).take7Chars()
        return try {
            urlRepository.save(Url(id = id, tinyUrlWithoutDomain = tinyUrlWithoutDomain, originalUrl = urlRequestDTO.originalUrl))
            UrlResponseDTO(tinyUrl = "${tinyUrlProperties.baseUrl}$tinyUrlWithoutDomain")
        } catch (ex: DataIntegrityViolationException) {
            return handleDuplicateKey(urlRequestDTO.originalUrl)
        }
    }

    fun getOriginalUrl(tinyUrlWithoutDomain: String): String {
        return urlRepository.findUrlByTinyUrlWithoutDomain(tinyUrlWithoutDomain = tinyUrlWithoutDomain)?.let {
            return it.originalUrl
        } ?: throw UrlNotFoundException("Tiny url ${tinyUrlProperties.baseUrl}$tinyUrlWithoutDomain not found")
    }

    /**
     * This is a rare case that probably will never happen
     */
    private fun handleDuplicateKey(url: String): UrlResponseDTO {
        urlRepository.setNewId(urlRepository.getLatestId())
        val id = urlRepository.getNewId()
        val tinyUrlWithoutDomain = convertToBase62(id).take7Chars()
        urlRepository.save(Url(id = id, tinyUrlWithoutDomain = tinyUrlWithoutDomain, originalUrl = url))
        return UrlResponseDTO(tinyUrl = "${tinyUrlProperties.baseUrl}$tinyUrlWithoutDomain")
    }
}

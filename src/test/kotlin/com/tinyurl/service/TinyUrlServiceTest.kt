package com.tinyurl.service

import com.tinyurl.controller.dto.UrlRequestDTO
import com.tinyurl.domain.repository.UrlRepository
import com.tinyurl.dummyUrl
import com.tinyurl.exception.UrlNotFoundException
import com.tinyurl.property.TinyUrlProperties
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.dao.DataIntegrityViolationException

class TinyUrlServiceTest{

    private val urlRepository = mockk<UrlRepository>()
    private val tinyUrlProperties = TinyUrlProperties(baseUrl = "https://tinyurl.com/")
    private val tinyUrlService = TinyUrlService(
        urlRepository = urlRepository,
        tinyUrlProperties = tinyUrlProperties,
    )

    @Test
    fun `creates a tiny url based on a url`() {
        every { urlRepository.findByOriginalUrl(any()) } returns null
        every { urlRepository.save(any()) } returns dummyUrl()
        every { urlRepository.getNewId() } returns 1000

        val responseDTP = tinyUrlService.createTinyUrl(UrlRequestDTO("https://google.com"))

        assertThat(responseDTP.tinyUrl).isEqualTo("https://tinyurl.com/4FQZqy")
    }

    @Test
    fun `does not create a tiny url when the original url already exists`() {
        every { urlRepository.findByOriginalUrl(any()) } returns dummyUrl(tinyUrlWithoutDomain = "tzxa")

        val responseDTP = tinyUrlService.createTinyUrl(UrlRequestDTO("https://google.com"))

        assertThat(responseDTP.tinyUrl).isEqualTo("https://tinyurl.com/tzxa")
        verify(exactly = 0) { urlRepository.getNewId() }
        verify(exactly = 0) { urlRepository.save(any()) }
    }

    @Test
    fun `handles when there is a DataIntegrityViolationException and creates the tiny url`() {
        every { urlRepository.findByOriginalUrl(any()) } returns null
        every { urlRepository.getNewId() } returns 999 andThen 1000
        every { urlRepository.getLatestId() } returns 999
        every { urlRepository.setNewId(any()) } returns 999
        every { urlRepository.save(any()) } throws DataIntegrityViolationException("") andThen dummyUrl()
        val responseDTP = tinyUrlService.createTinyUrl(UrlRequestDTO("https://google.com"))

        assertThat(responseDTP.tinyUrl).isEqualTo("https://tinyurl.com/4FQZqy")
        verify(exactly = 1) { urlRepository.setNewId(any()) }
        verify(exactly = 2) { urlRepository.save(any()) }
    }

    @Test
    fun `gets the original url when exists by tiny url`() {
        val url = dummyUrl(originalUrl = "https://google.com")
        every { urlRepository.findUrlByTinyUrlWithoutDomain(any()) } returns url

        assertThat(tinyUrlService.getOriginalUrl("xyz")).isEqualTo("https://google.com")
    }

    @Test
    fun `throws not found exception when url is not found by tiny url`() {
        val url = dummyUrl(originalUrl = "https://google.com")
        every { urlRepository.findUrlByTinyUrlWithoutDomain(any()) } returns null

        assertThrows<UrlNotFoundException> { tinyUrlService.getOriginalUrl("xyz") }
    }
}

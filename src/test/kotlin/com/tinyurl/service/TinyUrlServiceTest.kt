package com.tinyurl.service

import com.tinyurl.domain.repository.UrlRepository
import com.tinyurl.dummyUrl
import com.tinyurl.property.TinyUrlProperties
import io.mockk.every
import org.junit.jupiter.api.Assertions.*
import io.mockk.mockk
import io.mockk.verify
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

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

        val responseDTP = tinyUrlService.createTinyUrl("https://google.com")

        assertThat(responseDTP.tinyUrl).isEqualTo("https://tinyurl.com/4FQZqy")
    }

    @Test
    fun `does not create a tiny url when the original url already exists`() {
        every { urlRepository.findByOriginalUrl(any()) } returns dummyUrl(tinyUrlWithoutDomain = "tzxa")

        val responseDTP = tinyUrlService.createTinyUrl("https://google.com")

        assertThat(responseDTP.tinyUrl).isEqualTo("https://tinyurl.com/tzxa")
        verify(exactly = 0) { urlRepository.getNewId() }
        verify(exactly = 0) { urlRepository.save(any()) }
    }
}

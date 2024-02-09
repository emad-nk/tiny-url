package com.tinyurl.domain.repository

import com.tinyurl.IntegrationTestParent
import com.tinyurl.common.CacheNames
import com.tinyurl.common.CacheNames.URL_BY_ORIGINAL_URL
import com.tinyurl.common.CacheNames.URL_BY_TINY_URL
import com.tinyurl.dummyUrl
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.redis.core.RedisTemplate

class UrlRepositoryIT(
    @Autowired private val urlRepository: UrlRepository,
    @Autowired private val redisTemplate: RedisTemplate<String, String>,
): IntegrationTestParent() {

    @Test
    fun `sets the current id and retrieves the next id`(){
        urlRepository.setNewId(10)
        assertThat(urlRepository.getNewId()).isEqualTo(11)
    }

    @Test
    fun `finds a URL by original URL and caches it`(){
        val url = dummyUrl(originalUrl = "www.original.com")
        urlRepository.save(url)

        assertThat(redisTemplate.keys("$URL_BY_ORIGINAL_URL*")).isEmpty()
        assertThat(urlRepository.findByOriginalUrl(url = url.originalUrl)).isEqualTo(url)
        assertThat(redisTemplate.keys("$URL_BY_ORIGINAL_URL*")).hasSize(1)
    }

    @Test
    fun `finds a URL by tiny url caches it`(){
        val url = dummyUrl(tinyUrlWithoutDomain = "xxbaA")
        urlRepository.save(url)

        assertThat(redisTemplate.keys("$URL_BY_TINY_URL*")).isEmpty()
        assertThat(urlRepository.findUrlByTinyUrlWithoutDomain(tinyUrlWithoutDomain = url.tinyUrlWithoutDomain)).isEqualTo(url)
        assertThat(redisTemplate.keys("$URL_BY_TINY_URL*")).hasSize(1)
    }

    @Test
    fun `returns null when URL does not exist by the original URL and does not caches it`(){
        assertThat(redisTemplate.keys("$URL_BY_ORIGINAL_URL*")).isEmpty()
        assertThat(urlRepository.findByOriginalUrl(url = "non-existent-url")).isNull()
        assertThat(redisTemplate.keys("$URL_BY_ORIGINAL_URL*")).isEmpty()
    }

    @Test
    fun `returns the latest id in the URL table`(){
        urlRepository.save(dummyUrl(id = 10))
        urlRepository.save(dummyUrl(id = 8))
        urlRepository.save(dummyUrl(id = 12))
        urlRepository.save(dummyUrl(id = 5))

        assertThat(urlRepository.getLatestId()).isEqualTo(12)
    }
}

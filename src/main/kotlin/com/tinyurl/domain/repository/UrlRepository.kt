package com.tinyurl.domain.repository

import com.tinyurl.configuration.CacheNames.URL_BY_ORIGINAL_URL
import com.tinyurl.configuration.CacheNames.URL_BY_TINY_URL
import com.tinyurl.domain.model.Url
import org.springframework.cache.annotation.Cacheable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query

interface UrlRepository : JpaRepository<Url, Long> {

    @Query(
        nativeQuery = true,
        value = """
            select nextval(pg_get_serial_sequence('url', 'id')) as new_id
        """,
    )
    fun getNewId(): Long

    @Query(
        nativeQuery = true,
        value = """
            select setval(pg_get_serial_sequence('url', 'id'), :id) as new_id
        """,
    )
    fun setNewId(id: Long): Long

    @Cacheable(URL_BY_ORIGINAL_URL, key = "{#url}", unless = "#result == null")
    fun findByOriginalUrl(url: String): Url?

    @Cacheable(URL_BY_TINY_URL, key = "{#tinyUrlWithoutDomain}", unless = "#result == null")
    fun findUrlByTinyUrlWithoutDomain(tinyUrlWithoutDomain: String): Url?

    @Query(
        nativeQuery = true,
        value = """
            select id from url order by id desc limit 1
        """,
    )
    fun getLatestId(): Long
}

package com.tinyurl.domain.repository

import com.tinyurl.domain.model.Url
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query

interface UrlRepository: JpaRepository<Url, Long> {

    @Query(
        """
            select nextval(pg_get_serial_sequence('url', 'id')) as new_id
        """
    )
    fun getNewId(): Long

    @Query(
        """
            select setval(pg_get_serial_sequence('url', 'id'), :id) as new_id
        """
    )
    fun setNewId(id: Long): Long
}

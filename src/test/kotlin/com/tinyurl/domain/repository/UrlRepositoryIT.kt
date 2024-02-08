package com.tinyurl.domain.repository

import com.tinyurl.IntegrationTestParent
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired

class UrlRepositoryIT(
    @Autowired private val urlRepository: UrlRepository,
): IntegrationTestParent() {

    @Test
    fun `retrieves the next id`(){
        assertThat(urlRepository.getNewId()).isGreaterThan(0)
    }
}

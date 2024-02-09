package com.tinyurl.domain.repository

import com.tinyurl.IntegrationTestParent
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired

class UrlRepositoryIT(
    @Autowired private val urlRepository: UrlRepository,
): IntegrationTestParent() {

    @Test
    fun `sets the current id and retrieves the next id`(){
        urlRepository.setNewId(10)
        assertThat(urlRepository.getNewId()).isEqualTo(11)
    }
}

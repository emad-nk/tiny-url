package com.tinyurl.controller

import com.tinyurl.IntegrationTestParent
import com.tinyurl.common.convertToBase62
import com.tinyurl.common.take7Chars
import com.tinyurl.controller.ControllerExceptionHandler.ValidationError
import com.tinyurl.controller.dto.UrlRequestDTO
import com.tinyurl.controller.dto.UrlResponseDTO
import com.tinyurl.controller.validation.RequestUrlValidator.Companion.URL_VALIDATION_ERROR
import com.tinyurl.domain.repository.UrlRepository
import com.tinyurl.dummyUrl
import com.tinyurl.property.TinyUrlProperties
import io.restassured.RestAssured.given
import io.restassured.http.ContentType.JSON
import org.apache.http.HttpStatus
import org.apache.http.HttpStatus.SC_MOVED_PERMANENTLY
import org.apache.http.HttpStatus.SC_NOT_FOUND
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired

class TinyUrlControllerIT(
    @Autowired private val urlRepository: UrlRepository,
    @Autowired private val tinyUrlProperties: TinyUrlProperties,
) : IntegrationTestParent() {

    @Test
    fun `creates a tiny url out of original url`() {
        urlRepository.setNewId(1)
        val expectedEncodedValue = convertToBase62(2).take7Chars()
        val expectedTinyUrl = "${tinyUrlProperties.baseUrl}$expectedEncodedValue"
        val urlRequest = UrlRequestDTO(
            originalUrl = "https://google.com",
        )

        val response = given()
            .contentType(JSON)
            .body(urlRequest)
            .`when`()
            .post(TINY_URL_URI)
            .then()
            .log().ifValidationFails()
            .statusCode(HttpStatus.SC_CREATED)
            .extract()
            .body()
            .`as`(UrlResponseDTO::class.java)

        assertThat(response.tinyUrl).isEqualTo(expectedTinyUrl)
    }

    @Test
    fun `throws bad request exception when URL is invalid`() {
        val urlRequest = UrlRequestDTO(
            originalUrl = "bad-url",
        )

        val response = given()
            .contentType(JSON)
            .body(urlRequest)
            .`when`()
            .post(TINY_URL_URI)
            .then()
            .log().ifValidationFails()
            .statusCode(HttpStatus.SC_BAD_REQUEST)
            .extract()
            .body()
            .`as`(ValidationError::class.java)

        assertThat(response.message).isEqualTo(URL_VALIDATION_ERROR)
    }

    @Test
    fun `creates a tiny url out of original url when there is a clash by id`() {
        urlRepository.save(dummyUrl(id = 2))
        urlRepository.setNewId(1) // next id will be 2, but 2 is already taken
        val expectedEncodedValue = convertToBase62(3).take7Chars()
        val expectedTinyUrl = "${tinyUrlProperties.baseUrl}$expectedEncodedValue"
        val urlRequest = UrlRequestDTO(
            originalUrl = "https://google.com",
        )

        val response = given()
            .contentType(JSON)
            .body(urlRequest)
            .`when`()
            .post(TINY_URL_URI)
            .then()
            .log().ifValidationFails()
            .statusCode(HttpStatus.SC_CREATED)
            .extract()
            .body()
            .`as`(UrlResponseDTO::class.java)

        assertThat(response.tinyUrl).isEqualTo(expectedTinyUrl)
    }

    @Test
    fun `gets the original url when tiny url is correct`() {
        val originalUrl = "https://google.com"
        val encodedValue = convertToBase62(2).take7Chars()

        urlRepository.save(dummyUrl(tinyUrlWithoutDomain = encodedValue, originalUrl = originalUrl))

        val location = given()
            .`when`()
            .redirects()
            .follow(false)
            .pathParam("tinyUrlWithoutDomain", encodedValue)
            .get("{tinyUrlWithoutDomain}")
            .then()
            .log().ifValidationFails()
            .statusCode(SC_MOVED_PERMANENTLY)
            .extract()
            .header("location")

        assertThat(location).isEqualTo(originalUrl)
    }

    @Test
    fun `returns not found exception when tiny url does not exist`() {
        given()
            .`when`()
            .redirects()
            .follow(false)
            .pathParam("tinyUrlWithoutDomain", "non-existent-url")
            .get("{tinyUrlWithoutDomain}")
            .then()
            .log().ifValidationFails()
            .statusCode(SC_NOT_FOUND)
    }

    companion object {
        private const val TINY_URL_URI = "api/v1/urls"
    }
}

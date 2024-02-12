package com.tinyurl.controller

import com.tinyurl.controller.dto.UrlRequestDTO
import com.tinyurl.controller.dto.UrlResponseDTO
import com.tinyurl.service.TinyUrlService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springframework.http.HttpStatus.CREATED
import org.springframework.http.HttpStatus.MOVED_PERMANENTLY
import org.springframework.http.MediaType.APPLICATION_JSON_VALUE
import org.springframework.http.ResponseEntity
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController
import java.net.URI

@RestController
@Validated
@Tag(name = "Tiny URL Controller", description = "Creates short URLs as well as returns original URLs from short URLs")
class TinyUrlController(
    private val tinyUrlService: TinyUrlService,
) {

    @Operation(description = "Returns the created short URL")
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "201", description = "Created"),
        ],
    )
    @PostMapping("/api/v1/urls", produces = [APPLICATION_JSON_VALUE])
    @ResponseStatus(code = CREATED)
    fun createTinyUrl(
        @Valid @RequestBody urlRequestDTO: UrlRequestDTO,
    ): UrlResponseDTO = tinyUrlService.createTinyUrl(urlRequestDTO)

    @Operation(description = "Returns the original url from the provided tiny url. Endpoint is without /v1/api/ to mimic Tinyurl")
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "301", description = "Moved Permanently"),
        ],
    )
    @GetMapping("/{tinyUrlWithoutDomain}")
    @ResponseStatus(code = MOVED_PERMANENTLY)
    fun getOriginalUrl(
        @PathVariable tinyUrlWithoutDomain: String,
    ): ResponseEntity<String> {
        val url = tinyUrlService.getOriginalUrl(tinyUrlWithoutDomain)
        return ResponseEntity.status(MOVED_PERMANENTLY).location(URI(url)).build()
    }
}

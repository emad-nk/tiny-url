package com.tinyurl

import com.tinyurl.common.take7Chars
import com.tinyurl.domain.model.Url
import java.util.UUID

fun dummyUrl(
    id: Long = 1L,
    tinyUrlWithoutDomain: String = UUID.randomUUID().toString().take7Chars(),
    originalUrl: String = "www.tinyurl.com",
): Url =
    Url(
        id = id,
        tinyUrlWithoutDomain = tinyUrlWithoutDomain,
        originalUrl = originalUrl,
    )

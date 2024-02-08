package com.tinyurl

import com.tinyurl.domain.model.Url

fun dummyUrl(
    id: Long = 1L,
    base62: String = "xyz",
    originalUrl: String = "www.tinyurl.com",
): Url =
    Url(
        id = id,
        base62 = base62,
        originalUrl = originalUrl,
    )

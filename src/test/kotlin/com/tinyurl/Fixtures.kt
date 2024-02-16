package com.tinyurl

import com.tinyurl.common.take7Chars
import com.tinyurl.domain.model.Url
import java.util.UUID.randomUUID

fun dummyUrl(
    id: String = randomUUID().toString(),
    sequenceId: Long = 1L,
    tinyUrlWithoutDomain: String = randomUUID().toString().take7Chars(),
    originalUrl: String = "www.tinyurl.com",
): Url =
    Url(
        id = id,
        sequenceId = sequenceId,
        tinyUrlWithoutDomain = tinyUrlWithoutDomain,
        originalUrl = originalUrl,
    )

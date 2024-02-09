package com.tinyurl.exception

import org.springframework.http.HttpStatus.NOT_FOUND
import org.springframework.web.bind.annotation.ResponseStatus

@ResponseStatus(NOT_FOUND)
class TinyUrlNotFoundException (override val message: String): RuntimeException(message)

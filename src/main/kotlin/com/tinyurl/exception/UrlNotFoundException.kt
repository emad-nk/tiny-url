package com.tinyurl.exception

class UrlNotFoundException(override val message: String) : RuntimeException(message)

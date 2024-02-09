package com.tinyurl.common

import io.seruco.encoding.base62.Base62

private val base62 = Base62.createInstance()
fun convertToBase62(id: Long): String {
    return String(base62.encode(numberToByteArray(id)))
}

fun String.take7Chars(): String {
    if(this.length >= 7){
        return this.substring(startIndex = 0, endIndex = 7)
    }
    return this
}

private fun numberToByteArray (data: Number, size: Int = 4) : ByteArray =
    ByteArray (size) {i -> (data.toLong() shr (i*8)).toByte()}

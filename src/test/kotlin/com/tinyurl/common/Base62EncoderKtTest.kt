package com.tinyurl.common

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource

class Base62EncoderKtTest{

    @Test
    fun `converts number to base62`(){
        val base62 = convertToBase62(1000)

        assertThat(base62).isEqualTo("4FQZqy")
    }

    @ParameterizedTest
    @CsvSource(
        "abcdefgh,abcdefg",
        "abcdefg,abcdefg",
        "abc,abc",
    )
    fun `take 7 character from the provided string`(str: String, expectedResult: String){
        assertThat(str.take7Chars()).isEqualTo(expectedResult)

    }
}
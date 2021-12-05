package com.mpiotrowski.maudiofasttrackmixer.usb

import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert.assertThat

import org.junit.Test

class ByteUtilTest {

    @Test
    fun toByteArray_firstByte195_extractedAs195() {
        assertThat(ByteUtil.toByteArray(0b11000011_00000000)[0], `is`(0b11000011.toByte()))
    }

    @Test
    fun toByteArray_secondByte195_extractedAs195() {
        assertThat(ByteUtil.toByteArray(0b00000000_00111100)[1], `is`(0b00111100.toByte()))
    }

    @Test
    fun toByteArray_firstByte0_extractedAs0() {
        assertThat(ByteUtil.toByteArray(0b00000000_11111111)[0], `is`(0b00000000.toByte()))
    }

    @Test
    fun toByteArray_secondByte0_extractedAs0() {
        assertThat(ByteUtil.toByteArray(0b11111111_00000000)[1], `is`(0b00000000.toByte()))
    }

    @Test
    fun toByteArray_firstByte255_extractedAs255() {
        assertThat(ByteUtil.toByteArray(0b11111111_00000000)[0], `is`(0b11111111.toByte()))
    }

    @Test
    fun toByteArray_secondByte255_extractedAs255() {
        assertThat(ByteUtil.toByteArray(0b00000000_11111111)[1], `is`(0b11111111.toByte()))
    }

    @Test
    fun toByteArray_moreThanTwoBytes_returnsTwoBytes() {
        assertThat(ByteUtil.toByteArray(0b11111111_11000011_11000011)[1], `is`(0b11000011.toByte()))
        assertThat(ByteUtil.toByteArray(0b11111111_11000011_11000011)[1], `is`(0b11000011.toByte()))
    }
}
package com.mpiotrowski.maudiofasttrackmixer.usb

import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert.assertThat

import org.junit.Test

class ByteUtilTest{

    @Test
    fun toByteArray_lkj_lkj(){
        assertThat(0, `is`(0))
        assertThat(ByteUtil.toByteArray(0b11000011_00111100)[0], `is`(195))
        //assertThat(ByteUtil.toByteArray(0b11000011_00111100)[1], `is`(0b00111100))
    }
}
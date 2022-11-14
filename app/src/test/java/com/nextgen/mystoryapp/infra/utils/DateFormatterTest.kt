package com.nextgen.mystoryapp.infra.utils

import org.junit.Assert
import org.junit.Assert.*
import org.junit.Test
import java.time.format.DateTimeParseException
import java.time.zone.ZoneRulesException

class DateFormatterTest{
    //TTD test implementation
    //failed test first
    @Test
    fun `given correct ISO 8601 format then should format correctly`(){
        val currentDate = "2022-02-02T10:10:10Z"
        Assert.assertEquals("02 Feb 2022 | 17:10", DateFormatter.formatDate(currentDate, "Asia/Jakarta"))
        Assert.assertEquals("02 Feb 2022 | 18:10", DateFormatter.formatDate(currentDate, "Asia/Makassar"))
        Assert.assertEquals("02 Feb 2022 | 19:10", DateFormatter.formatDate(currentDate, "Asia/Jayapura"))
    }

    //test given wrong iso and time zone
    @Test
    fun `given wrong ISO 8601 format then should throw error`(){
        val wrongFormat = "2022-02-02T10:10"
        Assert.assertThrows(DateTimeParseException::class.java){
            DateFormatter.formatDate(wrongFormat, "Asia/Jakarta")
        }
    }

    @Test
    fun `given invalid timezone then should throw error`(){
        val wrongFormat = "2022-02-02T10:10Z"
        Assert.assertThrows(DateTimeParseException::class.java){
            DateFormatter.formatDate(wrongFormat, "Asia/Bandung")
        }
    }
}
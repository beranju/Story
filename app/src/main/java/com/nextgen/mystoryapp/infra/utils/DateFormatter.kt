package com.nextgen.mystoryapp.infra.utils

import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

object DateFormatter {
    //first format, tapi time zone membuat test susah silakukan maka dilakukan injection
//        fun formatDate(currentDateString: String): String{
//            val instant = Instant.parse(currentDateString)
//            val formatter = DateTimeFormatter.ofPattern("dd MMM yyyy | HH:mm")
//                .withZone(ZoneId.of(TimeZone.getDefault().id))
//
//            return formatter.format(instant)
//        }

    //dengan dependency injection sehingga test mudah dilakukan
    fun formatDate(currentDateString: String, targetTimeZone: String): String {
        val instant = Instant.parse(currentDateString)
        val formatter = DateTimeFormatter.ofPattern("dd MMM yyyy | HH:mm")
            .withZone(ZoneId.of(targetTimeZone))

        return formatter.format(instant)
    }
}
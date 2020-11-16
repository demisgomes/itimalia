package com.abrigo.itimalia.application.config

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.datatype.joda.JodaModule
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import java.text.SimpleDateFormat

object ItimaliaJsonSerializer {
    private const val DATE_PATTERN = "yyyy-MM-dd"
    fun build(): ObjectMapper {
        return jacksonObjectMapper().registerModule(JodaModule())
            .enable(DeserializationFeature.READ_ENUMS_USING_TO_STRING)
            .enable(SerializationFeature.WRITE_ENUMS_USING_TO_STRING)
            .setDateFormat(SimpleDateFormat(DATE_PATTERN))
    }
}

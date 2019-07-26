package application.config

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.datatype.joda.JodaModule
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper

object ItimaliaJsonSerializer {
    fun build():ObjectMapper{
        return jacksonObjectMapper().registerModule(JodaModule())
    }
}

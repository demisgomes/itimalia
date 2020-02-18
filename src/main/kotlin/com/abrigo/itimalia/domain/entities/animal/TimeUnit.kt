package com.abrigo.itimalia.domain.entities.animal

import com.fasterxml.jackson.annotation.JsonProperty

enum class TimeUnit {
    @JsonProperty("year")
    YEAR,
    @JsonProperty("month")
    MONTH
}
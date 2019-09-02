package com.abrigo.itimalia.domain.entities

import com.fasterxml.jackson.annotation.JsonProperty

enum class TimeUnit {
    @JsonProperty("year")
    YEAR,
    @JsonProperty("month")
    MONTH
}
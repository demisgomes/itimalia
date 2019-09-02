package com.abrigo.itimalia.domain.entities

import com.fasterxml.jackson.annotation.JsonProperty

enum class AnimalStatus {
    @JsonProperty("available")
    AVAILABLE,
    @JsonProperty("adopted")
    ADOPTED,
    @JsonProperty("dead")
    DEAD,
    @JsonProperty("gone")
    GONE

}

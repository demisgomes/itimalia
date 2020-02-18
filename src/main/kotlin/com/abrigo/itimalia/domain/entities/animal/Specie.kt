package com.abrigo.itimalia.domain.entities.animal

import com.fasterxml.jackson.annotation.JsonProperty

enum class Specie {
    @JsonProperty("cat")
    CAT,
    @JsonProperty("dog")
    DOG
}

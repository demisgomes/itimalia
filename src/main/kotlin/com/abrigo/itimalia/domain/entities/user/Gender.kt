package com.abrigo.itimalia.domain.entities.user

import com.fasterxml.jackson.annotation.JsonProperty

enum class Gender(val description:String){
    @JsonProperty("male")
    MALE("male"),
    @JsonProperty("female")
    FEMALE("female"),
    @JsonProperty("not_declared")
    NOT_DECLARED("not_declared")
}
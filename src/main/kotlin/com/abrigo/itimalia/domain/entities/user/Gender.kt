package com.abrigo.itimalia.domain.entities.user

import com.fasterxml.jackson.annotation.JsonProperty

enum class Gender(val description:String){
    MALE("MALE"),
    FEMALE("FEMALE"),
    NOT_DECLARED("NOT_DECLARED")
}
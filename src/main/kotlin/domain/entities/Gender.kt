package domain.entities

import com.fasterxml.jackson.annotation.JsonProperty

enum class Gender(val description:String){
    @JsonProperty("male")
    MASC("male"),
    @JsonProperty("female")
    FEM("female"),
    @JsonProperty("undefined")
    UNDEFINED("undefined")
}
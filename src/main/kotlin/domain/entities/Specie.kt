package domain.entities

import com.fasterxml.jackson.annotation.JsonProperty

enum class Specie {
    @JsonProperty("cat")
    CAT,
    @JsonProperty("dog")
    DOG
}

package domain.entities

import com.fasterxml.jackson.annotation.JsonProperty
import io.javalin.core.security.Role

enum class Roles: Role {
    @JsonProperty(value = "anyone")
    ANYONE,
    @JsonProperty(value = "user")
    USER,
    @JsonProperty(value = "admin")
    ADMIN
}
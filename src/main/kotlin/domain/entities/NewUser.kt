package domain.entities

import com.fasterxml.jackson.annotation.JsonFormat
import org.joda.time.DateTime

data class NewUser(
    val email: String,
    val password:String,
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    val birthDate: DateTime?,
    val gender:Gender?,
    val name: String,
    val phone: String)
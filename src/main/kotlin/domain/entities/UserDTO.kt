package domain.entities

import com.fasterxml.jackson.annotation.JsonFormat
import org.joda.time.DateTime
import java.util.*

data class UserDTO(
    var id: Int?,
    var email: String,
    val password:String,
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm a z")
    val birthDate: DateTime?,
    val gender:Gender?,
    val name: String,
    val phone: String,
    var role: Roles,
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm a z")
    var creationDate: DateTime,
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm a z")
    val modificationDate: DateTime,
    var token:String?)

package domain.entities.user

import com.fasterxml.jackson.annotation.JsonFormat
import domain.entities.Gender
import domain.entities.Roles
import org.joda.time.DateTime

data class UserDTO(
    var id: Int?,
    var email: String,
    val password:String,
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm a z")
    val birthDate: DateTime?,
    val gender: Gender?,
    val name: String,
    val phone: String,
    var role: Roles,
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm a z")
    val creationDate: DateTime?,
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm a z")
    val modificationDate: DateTime?,
    var token:String?)

fun UserDTO.toUserSearched()= UserSearched(
        email = email,
        birthDate = birthDate,
        role = role,
        creationDate = creationDate,
        name = name,
        gender = gender,
        id = id,
        phone = phone
    )

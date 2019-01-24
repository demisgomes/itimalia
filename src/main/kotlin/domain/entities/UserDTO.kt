package domain.entities

import java.util.*

data class UserDTO(
    var id: Int?,
    val email: String,
    val password:String,
    val birthDate: Date?,
    val gender:Gender?,
    val name: String,
    val phone: String,
    val admin: Boolean,
    val creationDate: Date,
    val modificationDate: Date)

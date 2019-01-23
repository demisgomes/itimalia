package domain.entities

import java.util.*

data class UserDTO(
    val id: Int,
    val email: String,
    val password:String,
    val birthDate: Date?,
    val gender:Gender?,
    val name: String,
    val phone: String,
    val creationDate: Date,
    val modificationDate: Date)

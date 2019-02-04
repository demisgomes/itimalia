package domain.entities

import java.util.*

data class UserDTO(
    var id: Int?,
    var email: String,
    val password:String,
    val birthDate: Date?,
    val gender:Gender?,
    val name: String,
    val phone: String,
    var role: Roles,
    var creationDate: Date?,
    var modificationDate: Date?,
    var token:String?)

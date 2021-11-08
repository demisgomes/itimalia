package com.abrigo.itimalia.domain.entities.user

import com.abrigo.itimalia.domain.entities.animal.AnimalWithoutAdopter
import org.joda.time.DateTime

class UserPublicInfo(
    val id: Int?,
    val email: String,
    val birthDate: DateTime?,
    val gender: Gender?,
    val name: String,
    val phone: String,
    val role: UserRole,
    val creationDate: DateTime?,
    val adoptedAnimals: List<AnimalWithoutAdopter>
)
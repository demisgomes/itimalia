package com.abrigo.itimalia.domain.entities.user

import com.abrigo.itimalia.domain.entities.animal.AnimalWithoutAdopter
import org.joda.time.DateTime

data class User(
    val id: Int?,
    val email: String,
    val password: String,
    val birthDate: DateTime,
    val gender: Gender,
    val name: String,
    val phone: String,
    val role: UserRole,
    val creationDate: DateTime,
    val modificationDate: DateTime,
    val token: String,
    val adoptedAnimals: List<AnimalWithoutAdopter> = emptyList()
)

fun User.toUserPublicInfo() = UserPublicInfo(
    email = email,
    birthDate = birthDate,
    role = role,
    creationDate = creationDate,
    name = name,
    gender = gender,
    id = id,
    phone = phone,
    adoptedAnimals = adoptedAnimals
)

fun User.toUserWithoutAnimals() = UserWithoutAnimals(
    email = email,
    birthDate = birthDate,
    role = role,
    creationDate = creationDate,
    name = name,
    gender = gender,
    id = id,
    phone = phone
)

fun User.toLoggedUser() = LoggedUser(
    email = email,
    birthDate = birthDate,
    role = role,
    creationDate = creationDate,
    name = name,
    gender = gender,
    id = id,
    phone = phone,
    token = token,
    adoptedAnimals = adoptedAnimals
)

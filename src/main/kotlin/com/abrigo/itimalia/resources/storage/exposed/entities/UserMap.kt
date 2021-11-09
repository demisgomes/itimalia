package com.abrigo.itimalia.resources.storage.exposed.entities

import com.abrigo.itimalia.domain.entities.user.Gender
import com.abrigo.itimalia.domain.entities.user.User
import com.abrigo.itimalia.domain.entities.user.UserPublicInfo
import com.abrigo.itimalia.domain.entities.user.UserRole
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.jodatime.datetime

object UserMap: IntIdTable("users") {
    val email = varchar("email", VARCHAR_LENGTH)
    val password = varchar("password", VARCHAR_LENGTH)
    val birthDate = datetime("birth_date")
    val gender = varchar("gender", VARCHAR_LENGTH)
    val name = varchar("name", VARCHAR_LENGTH)
    val phone = varchar("phone", VARCHAR_LENGTH)
    val role = varchar("role", VARCHAR_LENGTH)
    val creationDate = datetime("creation_date")
    val modificationDate = datetime("modification_date")
    val token = varchar("token", VARCHAR_LENGTH)
}

class UserEntity(id: EntityID<Int>): IntEntity(id) {
    companion object : IntEntityClass<UserEntity>(UserMap)
    var email by UserMap.email
    var password by UserMap.password
    var birthDate by UserMap.birthDate
    var gender by UserMap.gender
    var name by UserMap.name
    var phone by UserMap.phone
    var role by UserMap.role
    var creationDate by UserMap.creationDate
    var modificationDate by UserMap.modificationDate
    var token by UserMap.token
    private val adoptedAnimals by AnimalEntity optionalReferrersOn AnimalMap.adoptedBy

    fun toUser() = User(id.value, email, password, birthDate,
        Gender.valueOf(gender), name, phone, UserRole.valueOf(role), creationDate, modificationDate, token, adoptedAnimals.map { adoptedAnimal -> adoptedAnimal.toAnimalWithoutAdopter() })

    fun toUserPublicInfo() = UserPublicInfo(id.value, email, birthDate,
        Gender.valueOf(gender), name, phone, UserRole.valueOf(role), creationDate, adoptedAnimals.map { adoptedAnimal -> adoptedAnimal.toAnimalWithoutAdopter() })
}
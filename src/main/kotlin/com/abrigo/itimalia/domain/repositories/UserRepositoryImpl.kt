package com.abrigo.itimalia.domain.repositories

import com.abrigo.itimalia.domain.entities.user.Gender
import com.abrigo.itimalia.domain.entities.user.Roles
import com.abrigo.itimalia.domain.entities.user.UserDTO
import com.abrigo.itimalia.domain.exceptions.InvalidCredentialsException
import com.abrigo.itimalia.domain.exceptions.UserNotFoundException
import com.abrigo.itimalia.resources.storage.entities.UserMap
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.update
import org.joda.time.DateTime

class UserRepositoryImpl:UserRepository{
    override fun findByEmail(email: String): UserDTO {
        try{
            return transaction {
                UserMap.select { UserMap.email eq email }.map { resultRow ->
                    buildUserDTO(resultRow)
                }.first()
            }
        }
        catch (exception:NoSuchElementException){
            throw UserNotFoundException()
        }

    }

    override fun findByCredentials(email: String, password: String): UserDTO {
        try{
            return transaction {
                UserMap.select { UserMap.email.eq(email) and UserMap.password.eq(password) }.map { resultRow ->
                    buildUserDTO(resultRow)
                }.first()
            }
        }
        catch (exception:NoSuchElementException){
            throw InvalidCredentialsException()
        }
    }

    override fun add(userDTO: UserDTO): UserDTO {
        transaction {
            UserMap.insert {
                it[UserMap.name] = userDTO.name
                it[UserMap.birthDate] = userDTO.birthDate
                it[UserMap.creationDate] = userDTO.creationDate
                it[UserMap.email] = userDTO.email
                it[UserMap.gender] = userDTO.gender.toString()
                it[UserMap.password] = userDTO.password
                it[UserMap.modificationDate] = userDTO.modificationDate
                it[UserMap.phone] = userDTO.phone
                it[UserMap.token] = userDTO.token!!
                it[UserMap.role] = userDTO.role.toString()
            }
        }
        return findByEmail(userDTO.email)
    }

    override fun update(id: Int, userDTO: UserDTO): UserDTO {
        val result = transaction {
                (UserMap).update({ UserMap.id eq id }) {
                    it[UserMap.name] = userDTO.name
                    it[UserMap.birthDate] = userDTO.birthDate
                    it[UserMap.email] = userDTO.email
                    it[UserMap.gender] = userDTO.gender.toString()
                    it[UserMap.password] = userDTO.password
                    it[UserMap.modificationDate] = DateTime.now()
                    it[UserMap.phone] = userDTO.phone
                    it[UserMap.token] = userDTO.token!!
                    it[UserMap.role] = userDTO.role.toString()
                }
            }

        result.let { res ->
            when (res) {
                0 -> throw UserNotFoundException()
                else -> return get(id)
            }
        }
    }

    override fun delete(id: Int) {
        val result = transaction {
            UserMap.deleteWhere { UserMap.id eq id }
        }

        if (result == 0) throw UserNotFoundException()
    }

    override fun get(id: Int): UserDTO {
        try{
            return transaction {
                UserMap.select { UserMap.id eq id }.map { resultRow ->
                    buildUserDTO(resultRow)
                }.first()
            }
        }
        catch (exception:NoSuchElementException){
            throw UserNotFoundException()
        }
    }

    private fun buildUserDTO(resultRow: ResultRow): UserDTO {
        return UserDTO(
            resultRow[UserMap.id].value,
            resultRow[UserMap.email],
            resultRow[UserMap.password],
            resultRow[UserMap.birthDate],
            Gender.valueOf(resultRow[UserMap.gender]),
            resultRow[UserMap.name],
            resultRow[UserMap.phone],
            Roles.valueOf(resultRow[UserMap.role]),
            resultRow[UserMap.creationDate],
            resultRow[UserMap.modificationDate],
            resultRow[UserMap.token]
        )
    }

}
package com.abrigo.itimalia.resources.storage.exposed.gateways

import com.abrigo.itimalia.domain.entities.user.Gender
import com.abrigo.itimalia.domain.entities.user.Roles
import com.abrigo.itimalia.domain.entities.user.UserDTO
import com.abrigo.itimalia.domain.exceptions.InvalidCredentialsException
import com.abrigo.itimalia.domain.exceptions.UserNotFoundException
import com.abrigo.itimalia.domain.repositories.UserRepository
import com.abrigo.itimalia.resources.storage.exposed.entities.UserMap
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.update
import org.joda.time.DateTime

class UserRepositoryImpl: UserRepository {
    override fun getIdByToken(token: String): Int {
        try{
            return transaction {
                UserMap.slice(UserMap.id).select { UserMap.token eq token }.map { resultRow ->
                    resultRow[UserMap.id].value
                }.first()
            }
        }
        catch (exception:NoSuchElementException){
            throw UserNotFoundException()
        }
    }

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
                it[name] = userDTO.name
                it[birthDate] = userDTO.birthDate
                it[creationDate] = userDTO.creationDate
                it[email] = userDTO.email
                it[gender] = userDTO.gender.toString()
                it[password] = userDTO.password
                it[modificationDate] = userDTO.modificationDate
                it[phone] = userDTO.phone
                it[token] = userDTO.token!!
                it[role] = userDTO.role.toString()
            }
        }
        return findByEmail(userDTO.email)
    }

    override fun update(id: Int, userDTO: UserDTO): UserDTO {
        val result = transaction {
                (UserMap).update({ UserMap.id eq id }) {
                    it[name] = userDTO.name
                    it[birthDate] = userDTO.birthDate
                    it[email] = userDTO.email
                    it[gender] = userDTO.gender.toString()
                    it[password] = userDTO.password
                    it[modificationDate] = DateTime.now()
                    it[phone] = userDTO.phone
                    it[token] = userDTO.token!!
                    it[role] = userDTO.role.toString()
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
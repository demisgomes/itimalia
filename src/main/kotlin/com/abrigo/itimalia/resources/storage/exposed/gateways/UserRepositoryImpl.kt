package com.abrigo.itimalia.resources.storage.exposed.gateways

import com.abrigo.itimalia.domain.entities.user.Gender
import com.abrigo.itimalia.domain.entities.user.User
import com.abrigo.itimalia.domain.entities.user.UserRole
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

    override fun findByEmail(email: String): User {
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

    override fun findByCredentials(email: String, password: String): User {
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

    override fun add(user: User): User {
        transaction {
            UserMap.insert {
                it[name] = user.name
                it[birthDate] = user.birthDate
                it[creationDate] = user.creationDate
                it[email] = user.email
                it[gender] = user.gender.toString()
                it[password] = user.password
                it[modificationDate] = user.modificationDate
                it[phone] = user.phone
                it[token] = user.token
                it[role] = user.role.toString()
            }
        }
        return findByEmail(user.email)
    }

    override fun update(id: Int, user: User): User {
        val result = transaction {
                (UserMap).update({ UserMap.id eq id }) {
                    it[name] = user.name
                    it[birthDate] = user.birthDate
                    it[email] = user.email
                    it[gender] = user.gender.toString()
                    it[password] = user.password
                    it[modificationDate] = DateTime.now()
                    it[phone] = user.phone
                    it[token] = user.token
                    it[role] = user.role.toString()
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

    override fun get(id: Int): User {
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

    private fun buildUserDTO(resultRow: ResultRow): User {
        return User(
            resultRow[UserMap.id].value,
            resultRow[UserMap.email],
            resultRow[UserMap.password],
            resultRow[UserMap.birthDate],
            Gender.valueOf(resultRow[UserMap.gender]),
            resultRow[UserMap.name],
            resultRow[UserMap.phone],
            UserRole.valueOf(resultRow[UserMap.role]),
            resultRow[UserMap.creationDate],
            resultRow[UserMap.modificationDate],
            resultRow[UserMap.token]
        )
    }

}
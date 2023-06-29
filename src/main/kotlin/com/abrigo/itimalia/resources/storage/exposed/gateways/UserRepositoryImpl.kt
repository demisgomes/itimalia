package com.abrigo.itimalia.resources.storage.exposed.gateways

import com.abrigo.itimalia.domain.entities.user.User
import com.abrigo.itimalia.domain.exceptions.InvalidCredentialsException
import com.abrigo.itimalia.domain.exceptions.UserNotFoundException
import com.abrigo.itimalia.domain.repositories.UserRepository
import com.abrigo.itimalia.resources.storage.exposed.entities.UserEntity
import com.abrigo.itimalia.resources.storage.exposed.entities.UserMap
import org.jetbrains.exposed.dao.exceptions.EntityNotFoundException
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.update
import org.joda.time.DateTime
import java.util.Optional
import kotlin.NoSuchElementException

class UserRepositoryImpl : UserRepository {

    override fun findByEmail(email: String): Optional<User> {
        return try {
            transaction {
                UserEntity.find { UserMap.email eq email }.map { userEntity ->
                    Optional.of(userEntity.toUser())
                }.first()
            }
        } catch (exception: NoSuchElementException) {
            Optional.empty()
        }
    }

    override fun findByCredentials(email: String, password: String): User {
        try {
            return transaction {
                UserEntity.find { UserMap.email.eq(email) and UserMap.password.eq(password) }.map { userEntity ->
                    userEntity.toUser()
                }.first()
            }
        } catch (exception: NoSuchElementException) {
            throw InvalidCredentialsException()
        }
    }

    override fun add(user: User): User {
        val addedUser = transaction {
            UserEntity.new {
                name = user.name
                birthDate = user.birthDate
                creationDate = user.creationDate
                email = user.email
                gender = user.gender.toString()
                password = user.password
                modificationDate = user.modificationDate
                phone = user.phone
                token = user.token
                role = user.role.toString()
            }
        }
        return transaction { addedUser.toUser() }
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
                else -> return transaction { UserEntity[id].toUser() }
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
        try {
            return transaction {
                UserEntity[id].toUser()
            }
        } catch (exception: EntityNotFoundException) {
            throw UserNotFoundException()
        }
    }
}

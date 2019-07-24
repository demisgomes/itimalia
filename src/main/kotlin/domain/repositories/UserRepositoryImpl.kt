package domain.repositories

import domain.entities.Gender
import domain.entities.Roles
import domain.entities.UserDTO
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction
import org.joda.time.DateTime
import resources.storage.entities.UserMap

class UserRepositoryImpl:UserRepository{
    override fun findByEmail(email: String):UserDTO {
        return transaction {
            UserMap.select { UserMap.email eq email }.map { resultRow ->
                buildUserDTO(resultRow)
            }.first()
        }

    }

    override fun findByCredentials(email: String, password: String): UserDTO {
        return transaction {
            UserMap.select { UserMap.email.eq(email) and UserMap.password.eq(password) }.map { resultRow ->
                buildUserDTO(resultRow)
            }.first()
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
        return userDTO
    }

    override fun update(id: Int, userDTO: UserDTO): UserDTO {
        transaction {
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
        return userDTO
    }

    override fun delete(id: Int): UserDTO {
        val originalUser=get(id)
        transaction {
            UserMap.deleteWhere { UserMap.id eq id }
        }
        return originalUser
    }

    override fun get(id: Int): UserDTO {
        return transaction {
            UserMap.select { UserMap.id eq id }.map { resultRow ->
                buildUserDTO(resultRow)
            }.first()
        }
    }

    private fun buildUserDTO(resultRow: ResultRow): UserDTO {
        return UserDTO(
            resultRow[UserMap.id],
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
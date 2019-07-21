package domain.repositories

import domain.entities.Gender
import domain.entities.Roles
import domain.entities.UserDTO
import domain.exceptions.UserNotFoundException
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction
import resources.storage.entities.AnimalMap
import resources.storage.entities.UserMap

class UserRepositoryImpl:UserRepository{
    override fun findByEmail(email: String):UserDTO {

        return transaction {
            UserMap.select { UserMap.email eq email }.map { resultRow ->
                UserDTO(
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
            }.first()
        }

    }

    override fun findByCredentials(email: String, password: String): UserDTO {
        try{
            return userList.values.filter {
                it.email == email &&
                        it.password == password
            }[0]
        }
        catch (exception: IndexOutOfBoundsException){
            throw UserNotFoundException()
        }

    }

    companion object {
        var userList:HashMap<Int, UserDTO> = HashMap()
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
        userList[id]=userDTO
        return userDTO
    }

    override fun delete(id: Int): UserDTO {
        val originalUser=get(id)
        userList.remove(id)
        return originalUser
    }

    override fun get(id: Int): UserDTO {
        if(userList.containsKey(id)){
            return userList[id]!!
        }
        else{
            throw UserNotFoundException()
        }
    }

}
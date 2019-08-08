package application.config

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import domain.entities.Gender
import domain.entities.Roles
import domain.entities.user.UserDTO
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction
import org.joda.time.DateTime
import resources.storage.entities.AnimalMap
import resources.storage.entities.UserMap

object DatabaseConfig{

    private val tables = arrayOf(AnimalMap, UserMap)

    fun connect(jdbcUrl: String, username: String, password: String){
        val hikariConfig = HikariConfig()

        hikariConfig.jdbcUrl = jdbcUrl
        hikariConfig.username = username
        hikariConfig.password = password

        val hikariDataSource = HikariDataSource(hikariConfig)

        Database.connect(hikariDataSource)
    }

    fun createTables(){
        transaction {
            addLogger(StdOutSqlLogger)
            //Do stuff
            SchemaUtils.create(*tables)
            val adminUser = UserDTO(
                id = null,
                email = "admin@itimalia.org",
                password = "admin",
                birthDate = DateTime.now(),
                gender = Gender.UNDEFINED,
                name = "Admin",
                phone = "8199999999",
                role = Roles.ADMIN,
                creationDate = DateTime.now(),
                modificationDate = DateTime.now(),
                token = "initial token"
            )
            UserMap.insert {
                it[UserMap.name] = adminUser.name
                it[UserMap.birthDate] = adminUser.birthDate
                it[UserMap.creationDate] = adminUser.creationDate
                it[UserMap.email] = adminUser.email
                it[UserMap.gender] = adminUser.gender.toString()
                it[UserMap.password] = adminUser.password
                it[UserMap.modificationDate] = adminUser.modificationDate
                it[UserMap.phone] = adminUser.phone
                it[UserMap.token] = adminUser.token!!
                it[UserMap.role] = adminUser.role.toString()
            }
        }
    }

    fun dropTables(){
        transaction {
            addLogger(StdOutSqlLogger)
            //Do stuff
            SchemaUtils.drop(*tables)
        }
    }
}


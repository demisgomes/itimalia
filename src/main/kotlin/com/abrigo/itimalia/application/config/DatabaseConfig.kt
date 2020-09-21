package com.abrigo.itimalia.application.config

import com.abrigo.itimalia.domain.entities.user.Gender
import com.abrigo.itimalia.domain.entities.user.Roles
import com.abrigo.itimalia.domain.entities.user.UserDTO
import com.abrigo.itimalia.resources.storage.entities.AnimalMap
import com.abrigo.itimalia.resources.storage.entities.UserMap
import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import org.apache.logging.log4j.LogManager
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.StdOutSqlLogger
import org.jetbrains.exposed.sql.addLogger
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction
import org.joda.time.DateTime

object DatabaseConfig{
    private val logger = LogManager.getLogger()
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
                gender = Gender.NOT_DECLARED,
                name = "Admin",
                phone = "8199999999",
                role = Roles.ADMIN,
                creationDate = DateTime.now(),
                modificationDate = DateTime.now(),
                token = "initial token"
            )

            var resultRow: ResultRow? = null

            try{
                resultRow = UserMap.select{
                    UserMap.email eq "admin@itimalia.org"
                }.first()

            }catch (exception: NoSuchElementException){
                logger.warn("The database not have any rows")
            }

            if (resultRow == null){
                UserMap.insert {
                    it[name] = adminUser.name
                    it[birthDate] = adminUser.birthDate
                    it[creationDate] = adminUser.creationDate
                    it[email] = adminUser.email
                    it[gender] = adminUser.gender.toString()
                    it[password] = adminUser.password
                    it[modificationDate] = adminUser.modificationDate
                    it[phone] = adminUser.phone
                    it[token] = adminUser.token!!
                    it[role] = adminUser.role.toString()
                }
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


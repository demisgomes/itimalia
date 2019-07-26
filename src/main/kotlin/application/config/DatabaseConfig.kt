package application.config

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.StdOutSqlLogger
import org.jetbrains.exposed.sql.addLogger
import org.jetbrains.exposed.sql.transactions.transaction
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


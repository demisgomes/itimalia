package holder

import application.config.DatabaseConfig
import org.h2.tools.Server
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.StdOutSqlLogger
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.addLogger
import org.jetbrains.exposed.sql.transactions.transaction
import resources.storage.entities.AnimalMap
import resources.storage.entities.UserMap

object DatabaseHolder{

    private val tables = arrayOf(AnimalMap, UserMap)

    init {
        DatabaseConfig.connect("jdbc:h2:mem:test;MODE=MySQL;DB_CLOSE_DELAY=-1","sa", "")
        transaction {
            addLogger(StdOutSqlLogger)
            //Do stuff
            SchemaUtils.create(*tables)
        }
    }

    fun start(){
        Server.createPgServer().start()
    }

    fun tearDown(){
        transaction {
            addLogger(StdOutSqlLogger)
            SchemaUtils.drop(*tables)
            SchemaUtils.create(*tables)
        }
    }

    fun stop(){
        Server.createPgServer().stop()
    }

}
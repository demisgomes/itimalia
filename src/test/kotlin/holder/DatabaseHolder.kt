package holder

import application.config.DatabaseConfig
import org.h2.tools.Server
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.StdOutSqlLogger
import org.jetbrains.exposed.sql.addLogger
import org.jetbrains.exposed.sql.transactions.transaction
import resources.storage.entities.AnimalMap

object DatabaseHolder{

    init {
        DatabaseConfig.connect("jdbc:h2:mem:test;MODE=MySQL;DB_CLOSE_DELAY=-1","sa", "")
        transaction {
            addLogger(StdOutSqlLogger)
            //Do stuff
            SchemaUtils.create(AnimalMap)
        }
    }

    fun start(){
        Server.createPgServer().start()
    }

    fun stop(){
        Server.createPgServer().stop()
    }

}
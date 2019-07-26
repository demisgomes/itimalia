package holder

import application.config.DatabaseConfig
import org.h2.tools.Server

object DatabaseHolder{


    init {
        DatabaseConfig.connect("jdbc:h2:mem:test;MODE=MySQL;DB_CLOSE_DELAY=-1","sa", "")
        DatabaseConfig.createTables()
    }

    fun start(){
        Server.createPgServer().start()
    }

    fun tearDown(){
        DatabaseConfig.dropTables()
        DatabaseConfig.createTables()
    }

    fun stop(){
        Server.createPgServer().stop()
    }

}
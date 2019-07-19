package commons.koin

import application.config.DatabaseConfig
import application.config.RouteConfig
import org.koin.dsl.module.module

val configModule=module{
    single { RouteConfig(get(), get(), get()) }
    single { DatabaseConfig.connect("jdbc:h2:mem:test;MODE=MySQL;", "sa", "") }
}
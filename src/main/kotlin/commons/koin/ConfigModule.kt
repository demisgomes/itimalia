package commons.koin

import application.config.ItimaliaJsonSerializer
import application.config.RouteConfig
import org.koin.dsl.module.module

val configModule=module{
    single { RouteConfig(get(), get(), get()) }
    single { ItimaliaJsonSerializer.build() }
}
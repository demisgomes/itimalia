package application.web

import commons.koin.serviceModule
import commons.koin.validationModule
import io.javalin.Javalin
import org.koin.standalone.StandAloneContext

fun main(args: Array<String>) {
    val app = Javalin.create().start(7000)

    StandAloneContext.startKoin(
        listOf(
            validationModule,
            serviceModule
        )
    )

    app.get("/") {
            ctx -> ctx.result("Hello World")
    }
}
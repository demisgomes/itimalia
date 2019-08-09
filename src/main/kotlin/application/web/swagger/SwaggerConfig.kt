package application.web.swagger

import domain.entities.Roles
import io.javalin.core.JavalinConfig
import io.javalin.core.security.SecurityUtil.roles
import io.javalin.plugin.openapi.OpenApiOptions
import io.javalin.plugin.openapi.OpenApiPlugin
import io.swagger.v3.oas.models.info.Info

object SwaggerConfig{

    fun registerPlugin(config: JavalinConfig): JavalinConfig {
        return config.registerPlugin(OpenApiPlugin(getOpenApiOptions()))
    }

    private fun getOpenApiOptions(): OpenApiOptions {
        val applicationInfo = Info()
            .version("1.0")
            .description("My Application")
        return OpenApiOptions(applicationInfo)
            .path("/docs")
            .roles(roles(Roles.ANYONE))
    }
}
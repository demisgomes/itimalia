package com.abrigo.itimalia.application.web.swagger

import com.abrigo.itimalia.domain.entities.Roles
import io.javalin.core.JavalinConfig
import io.javalin.core.security.SecurityUtil.roles
import io.javalin.plugin.openapi.OpenApiOptions
import io.javalin.plugin.openapi.OpenApiPlugin
import io.javalin.plugin.openapi.ui.SwaggerOptions
import io.swagger.v3.oas.models.info.Info

const val CONTENT_TYPE = "application/json"

object SwaggerConfig{

    fun registerPlugin(config: JavalinConfig): JavalinConfig {
        return config.registerPlugin(OpenApiPlugin(getOpenApiOptions()))
    }

    private fun getOpenApiOptions(): OpenApiOptions {
        val applicationInfo = Info()
            .version("1.0")
            .description("Itimalia Application")

        return OpenApiOptions(applicationInfo)
            .path("/swagger-docs")
            .swagger(SwaggerOptions("/swagger"))
            .roles(roles(Roles.ANYONE))
    }
}
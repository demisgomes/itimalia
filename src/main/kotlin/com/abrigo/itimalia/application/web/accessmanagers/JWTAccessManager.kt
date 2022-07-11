package com.abrigo.itimalia.application.web.accessmanagers

import com.abrigo.itimalia.application.web.accessmanagers.entities.RouteRole
import com.abrigo.itimalia.domain.entities.user.UserRole
import com.abrigo.itimalia.domain.exceptions.InvalidTokenException
import com.abrigo.itimalia.domain.exceptions.UnauthorizedAdminRoleException
import com.abrigo.itimalia.domain.exceptions.UnauthorizedUserRoleException
import com.abrigo.itimalia.domain.jwt.JWTService
import io.javalin.core.security.AccessManager
import io.javalin.http.Context
import io.javalin.http.Handler
import java.util.Optional

class JWTAccessManager(
    private val rolesMapping: Map<String, UserRole>,
    private val defaultRole: UserRole,
    private val jwtService: JWTService
) : AccessManager {
    companion object {
        const val ROLE_FIELD = "role"
        const val EMAIL_FIELD = "email"
        const val AUTHORIZATION = "Authorization"
        const val BEARER = "Bearer"
    }

    override fun manage(handler: Handler, ctx: Context, permittedRoles: MutableSet<io.javalin.core.security.RouteRole>) {
        // if token has been expired and this handler requires admin or user permissions, return invalid token exception
        // add and find does not requires token (Roles.ANYONE)

        if (ctx.matchedPath() == "/swagger") {
            handler.handle(ctx)
            return
        }

        val role = extractRole(ctx)

        if (permittedRoles.contains(RouteRole.valueOf(role.toString()))) {
            handler.handle(ctx)
        } else {
            if (permittedRoles.contains(RouteRole.ADMIN)) {
                ctx.json(UnauthorizedAdminRoleException().createErrorResponse())
                    .status(UnauthorizedAdminRoleException().httpStatus())
            } else {
                ctx.json(UnauthorizedUserRoleException().createErrorResponse())
                    .status(UnauthorizedUserRoleException().httpStatus())
            }
        }
    }

    fun extractRole(context: Context): UserRole {
        val optToken = getTokenFromHeader(context)
        if (optToken == Optional.empty<String>()) {
            return defaultRole
        }

        val decodedJWT = jwtService.decode(optToken.get())
        val userLevel = decodedJWT[ROLE_FIELD]?.lowercase()

        return Optional.ofNullable(rolesMapping[userLevel]).orElse(defaultRole)
    }

    fun extractEmail(context: Context): String {
        val optToken = getTokenFromHeader(context)
        if (optToken == Optional.empty<String>()) {
            return ""
        }

        val decodedJWT = jwtService.decode(optToken.get())

        return decodedJWT[EMAIL_FIELD] ?: throw InvalidTokenException()
    }

    private fun getTokenFromHeader(context: Context): Optional<String> {
        return Optional.ofNullable(context.header(AUTHORIZATION))
            .flatMap { header ->
                val split = header.split(" ")
                if (split.size != 2 || split[0] != BEARER) {
                    return@flatMap Optional.empty<String>()
                }
                return@flatMap Optional.of(split[1])
            }
    }
}

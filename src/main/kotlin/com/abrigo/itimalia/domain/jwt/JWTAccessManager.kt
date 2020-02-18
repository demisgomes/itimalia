package com.abrigo.itimalia.domain.jwt


import com.abrigo.itimalia.domain.entities.user.Roles
import com.abrigo.itimalia.domain.exceptions.InvalidTokenException
import com.abrigo.itimalia.domain.exceptions.UnauthorizedAdminRoleException
import com.abrigo.itimalia.domain.exceptions.UnauthorizedUserRoleException
import com.auth0.jwt.JWT
import io.javalin.core.security.AccessManager
import io.javalin.core.security.Role
import io.javalin.http.Context
import io.javalin.http.Handler
import java.util.*


class JWTAccessManager(
    private val userRoleClaim: String,
    private val rolesMapping: Map<String, Roles>,
    private val defaultRole: Roles
) : AccessManager {

    override fun manage(handler: Handler, ctx: Context, permittedRoles: MutableSet<Role>) {
        //if token has been expired and this handler requires admin or user permissions, return invalid token exception
        //add and find does not requires token (Roles.ANYONE)
        if(!verifyDate(ctx) && !permittedRoles.contains(defaultRole)){
            if(ctx.matchedPath() == "/swagger"){
                handler.handle(ctx)
                return
            }
            else{
                ctx.json(InvalidTokenException().createErrorResponse()).status(InvalidTokenException().httpStatus())
                return
            }
        }

        val role = extractRole(ctx)

        if (permittedRoles.contains(role)) {
            handler.handle(ctx)
        } else {
            if(permittedRoles.contains(Roles.ADMIN)){
                ctx.json(UnauthorizedAdminRoleException().createErrorResponse()).status(UnauthorizedAdminRoleException().httpStatus())
            }
            else{
                ctx.json(UnauthorizedUserRoleException().createErrorResponse()).status(UnauthorizedUserRoleException().httpStatus())
            }
        }

    }

    fun extractRole(context: Context): Roles {
        val string = getTokenFromHeader(context)
        if(string== Optional.empty<String>()){
            return defaultRole
        }

        val decodedJWT=JWT.decode(string.get())

        val userLevel = decodedJWT.getClaim(userRoleClaim).asString().toLowerCase()

        return Optional.ofNullable(rolesMapping[userLevel]).orElse(defaultRole)
    }

    fun verifyDate(context:Context):Boolean {
        val string = getTokenFromHeader(context)
        if (string == Optional.empty<String>()) {
            return false
        }

        val decodedJWT=JWT.decode(string.get())

        return decodedJWT.expiresAt.after(Date(Calendar.getInstance().timeInMillis))
    }

    fun extractEmail(context: Context):String{
        val string = getTokenFromHeader(context)
        if (string == Optional.empty<String>()) {
            return ""
        }

        val decodedJWT=JWT.decode(string.get())

        return decodedJWT.getClaim("email").asString()
    }

    private fun getTokenFromHeader(context: Context): Optional<String> {
        return Optional.ofNullable(context.header("Authorization"))
            .flatMap { header ->
                val split = header.split(" ")
                if (split.size != 2 || split[0] != "Bearer") {
                    return@flatMap Optional.empty<String>()
                }
                return@flatMap Optional.of(split[1])
            }
    }
}
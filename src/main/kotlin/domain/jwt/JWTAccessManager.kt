package domain.jwt


import com.auth0.jwt.JWT
import domain.entities.Roles
import domain.exceptions.InvalidTokenException
import domain.exceptions.UnauthorizedAdminRoleException
import domain.exceptions.UnauthorizedUserRoleException
import io.javalin.Context
import io.javalin.Handler
import io.javalin.security.AccessManager
import io.javalin.security.Role
import java.util.*


class JWTAccessManager(
    private val userRoleClaim: String,
    private val rolesMapping: Map<String, Roles>,
    private val defaultRole: Roles
) : AccessManager {

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

    override fun manage(handler: Handler, context: Context, permittedRoles: Set<Role>) {

        //if token has been expired and this handler requires admin or user permissions, return invalid token exception
        //add and find does not requires token (Roles.ANYONE)
        if(!verifyDate(context) && !permittedRoles.contains(defaultRole)){
            context.json(InvalidTokenException().createErrorResponse()).status(InvalidTokenException().httpStatus())
            return
        }

        val role = extractRole(context)

        if (permittedRoles.contains(role)) {
            handler.handle(context)
        } else {
            if(permittedRoles.contains(Roles.ADMIN)){
                context.json(UnauthorizedAdminRoleException().createErrorResponse()).status(UnauthorizedAdminRoleException().httpStatus())
            }
            else{
                context.json(UnauthorizedUserRoleException().createErrorResponse()).status(UnauthorizedUserRoleException().httpStatus())
            }
        }
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
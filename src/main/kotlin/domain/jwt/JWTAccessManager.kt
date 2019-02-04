package domain.jwt


import com.auth0.jwt.JWT
import com.auth0.jwt.interfaces.DecodedJWT
import domain.exceptions.InvalidCredentialsException
import io.javalin.Context
import io.javalin.Handler
import io.javalin.security.AccessManager
import io.javalin.security.Role
import org.eclipse.jetty.http.HttpStatus
import java.util.*
import java.util.Optional.empty
import java.util.Optional.ofNullable



class JWTAccessManager(
    private val userRoleClaim: String,
    private val rolesMapping: Map<String, Role>,
    private val defaultRole: Role
) : AccessManager {

    private fun extractRole(context: Context): Role {


        val string = getTokenFromHeader(context)
        if(string== Optional.empty<String>()){
            return defaultRole

        }

        val decodedJWT=JWT.decode(string.get())

        val userLevel = decodedJWT.getClaim(userRoleClaim).asString().toLowerCase()

        return Optional.ofNullable(rolesMapping[userLevel]).orElse(defaultRole)
    }

    @Throws(Exception::class)
    override fun manage(handler: Handler, context: Context, permittedRoles: Set<Role>) {
        val role = extractRole(context)

        if (permittedRoles.contains(role)) {
            handler.handle(context)
        } else {
            context.json(InvalidCredentialsException().createErrorResponse()).status(HttpStatus.UNAUTHORIZED_401)
        }
    }

    fun getTokenFromHeader(context: Context): Optional<String> {
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
package domain.jwt

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import domain.entities.UserDTO
import io.javalin.security.Role
import java.util.*


class JWTUtils{

    fun sign(email:String, role: Role, maxAgeInMinutes:Int):String{

        val algorithm = Algorithm.HMAC256("fabulous_password")
        return JWT
            .create()
            .withIssuer("Itimalia")
            .withClaim("email", email)
            .withClaim("role",role.toString())
            .withExpiresAt(convertToDate(maxAgeInMinutes)).sign(algorithm)
    }

    private fun convertToDate(minutes:Int):Date{
        val calendar=Calendar.getInstance()
        return Date(calendar.timeInMillis+minutes*60000)
    }

}
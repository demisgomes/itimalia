package domain.jwt

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import domain.entities.UserDTO
import java.util.*


object JWTUtils{

    fun sign(userDTO: UserDTO, maxAgeInMinutes:Int):String{

        val algorithm = Algorithm.HMAC256("fabulous_password")
        return JWT
            .create()
            .withIssuer("Itimalia")
            .withClaim("id", userDTO.id.toString())
            .withClaim("role",userDTO.role.toString())
            .withExpiresAt(convertToDate(maxAgeInMinutes)).sign(algorithm)

    }

    private fun convertToDate(minutes:Int):Date{
        val calendar=Calendar.getInstance()
        return Date(calendar.timeInMillis+minutes*60000)
    }
}
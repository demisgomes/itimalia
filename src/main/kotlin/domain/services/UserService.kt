package domain.services

import domain.entities.NewUser
import domain.entities.UserDTO
import domain.entities.UserLogin
import io.javalin.security.Role

interface UserService{
    fun add(newUser: NewUser) : UserDTO
    fun get(id:Int):UserDTO
    fun delete(id:Int, role: Role, email:String):UserDTO
    fun login(newUserLogin: UserLogin):UserDTO
    fun update(id: Int, userDTO: UserDTO, role: Role, email:String): UserDTO
}
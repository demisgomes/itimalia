package domain.services

import domain.entities.user.NewUser
import domain.entities.user.UserDTO
import domain.entities.user.UserLogin
import io.javalin.security.Role

interface UserService{
    fun add(newUser: NewUser) : UserDTO
    fun get(id:Int): UserDTO
    fun delete(id:Int, role: Role, email:String)
    fun login(newUserLogin: UserLogin): UserDTO
    fun update(id: Int, userDTO: UserDTO, role: Role, email:String): UserDTO
}
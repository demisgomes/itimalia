package com.abrigo.itimalia.domain.services

import com.abrigo.itimalia.domain.entities.user.NewUser
import com.abrigo.itimalia.domain.entities.user.UserDTO
import com.abrigo.itimalia.domain.entities.user.UserLogin
import io.javalin.core.security.Role

interface UserService{
    fun add(newUser: NewUser) : UserDTO
    fun get(id:Int): UserDTO
    fun delete(id:Int, role: Role, email:String)
    fun login(newUserLogin: UserLogin): UserDTO
    fun update(id: Int, userDTO: UserDTO, role: Role, email:String): UserDTO
}
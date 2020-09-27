package com.abrigo.itimalia.domain.services

import com.abrigo.itimalia.domain.entities.user.NewUserRequest
import com.abrigo.itimalia.domain.entities.user.UserDTO
import com.abrigo.itimalia.domain.entities.user.UserDTORequest
import com.abrigo.itimalia.domain.entities.user.UserLoginRequest
import io.javalin.core.security.Role

interface UserService{
    fun add(newUserRequest: NewUserRequest) : UserDTO
    fun get(id:Int): UserDTO
    fun delete(id:Int, role: Role, email:String)
    fun login(userLoginRequest: UserLoginRequest): UserDTO
    fun update(id: Int, userDTORequest: UserDTORequest, role: Role, email:String): UserDTO
    fun getIdByToken(token: String) : Int
}
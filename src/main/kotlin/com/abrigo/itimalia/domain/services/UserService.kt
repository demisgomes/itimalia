package com.abrigo.itimalia.domain.services

import com.abrigo.itimalia.domain.entities.user.NewUserRequest
import com.abrigo.itimalia.domain.entities.user.User
import com.abrigo.itimalia.domain.entities.user.UserLoginRequest
import com.abrigo.itimalia.domain.entities.user.UserRequest
import com.abrigo.itimalia.domain.entities.user.UserRole

interface UserService{
    fun add(newUserRequest: NewUserRequest) : User
    fun get(id:Int): User
    fun delete(id:Int, role: UserRole, email:String)
    fun login(userLoginRequest: UserLoginRequest): User
    fun update(id: Int, userRequest: UserRequest, role: UserRole, email:String): User
    fun findByEmail(email: String) : User
}
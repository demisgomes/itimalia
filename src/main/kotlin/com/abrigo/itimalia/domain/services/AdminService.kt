package com.abrigo.itimalia.domain.services

import com.abrigo.itimalia.domain.entities.user.NewUser
import com.abrigo.itimalia.domain.entities.user.User
import com.abrigo.itimalia.domain.entities.user.UserRole

interface AdminService{
    fun add(newUser: NewUser, role: UserRole): User
    fun get(id:Int): User
}
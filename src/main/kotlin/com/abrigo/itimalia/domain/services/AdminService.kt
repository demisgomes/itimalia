package com.abrigo.itimalia.domain.services

import com.abrigo.itimalia.domain.entities.Roles
import com.abrigo.itimalia.domain.entities.user.NewUser
import com.abrigo.itimalia.domain.entities.user.UserDTO

interface AdminService{
    fun add(newUser: NewUser, role: Roles): UserDTO
    fun get(id:Int): UserDTO
}
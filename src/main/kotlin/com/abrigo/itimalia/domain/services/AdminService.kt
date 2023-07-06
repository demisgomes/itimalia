package com.abrigo.itimalia.domain.services

import com.abrigo.itimalia.domain.entities.user.NewUser
import com.abrigo.itimalia.domain.entities.user.User

interface AdminService {
    fun add(newUser: NewUser): User
    fun get(id: Int): User
}

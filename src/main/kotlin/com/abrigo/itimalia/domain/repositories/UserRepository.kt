package com.abrigo.itimalia.domain.repositories

import com.abrigo.itimalia.domain.entities.user.User

interface UserRepository {
    fun add(user: User): User
    fun update(id: Int, user: User): User
    fun get(id: Int): User
    fun delete(id: Int)
    fun findByCredentials(email: String, password: String): User
    fun findByEmail(email: String): User
}

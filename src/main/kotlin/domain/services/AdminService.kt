package domain.services

import domain.entities.user.NewUser
import domain.entities.Roles
import domain.entities.user.UserDTO

interface AdminService{
    fun add(newUser: NewUser, role: Roles): UserDTO
    fun get(id:Int): UserDTO
}
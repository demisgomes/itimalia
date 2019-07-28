package domain.services

import domain.entities.NewUser
import domain.entities.Roles
import domain.entities.UserDTO

interface AdminService{
    fun add(newUser: NewUser, role: Roles):UserDTO
    fun get(id:Int):UserDTO
}
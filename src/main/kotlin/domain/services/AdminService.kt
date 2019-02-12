package domain.services

import domain.entities.NewUser
import domain.entities.UserDTO

interface AdminService{
    fun add(newUser: NewUser):UserDTO
    fun get(id:Int):UserDTO
}
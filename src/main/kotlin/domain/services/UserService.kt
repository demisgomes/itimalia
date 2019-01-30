package domain.services

import domain.entities.NewUser
import domain.entities.UserDTO

interface UserService{
    fun add(newUser: NewUser) : UserDTO
    fun update(id: Int, userDTO: UserDTO) : UserDTO
    fun get(id:Int):UserDTO
    fun delete(id:Int):UserDTO
}
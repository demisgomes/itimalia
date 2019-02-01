package domain.services

import domain.entities.NewUser
import domain.entities.UserDTO
import domain.entities.UserLogin

interface UserService{
    fun add(newUser: NewUser) : UserDTO
    fun update(id: Int, userDTO: UserDTO) : UserDTO
    fun get(id:Int):UserDTO
    fun delete(id:Int):UserDTO
    fun login(newUserLogin: UserLogin):UserDTO
}
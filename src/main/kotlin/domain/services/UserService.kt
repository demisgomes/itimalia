package domain.services

import domain.entities.NewUser
import domain.entities.UserDTO

interface UserService{
    fun add(newUser: NewUser) : UserDTO
}
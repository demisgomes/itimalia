package domain.repositories

import domain.entities.user.UserDTO

interface UserRepository{
    fun add(userDTO: UserDTO): UserDTO
    fun update(id: Int,userDTO: UserDTO) : UserDTO
    fun get(id:Int): UserDTO
    fun delete(id:Int)
    fun findByCredentials(email:String,password:String): UserDTO
    fun findByEmail(email: String): UserDTO
}
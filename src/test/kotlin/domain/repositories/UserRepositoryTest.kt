package domain.repositories

import domain.entities.UserDTO
import domain.repositories.factories.UserFactory
import holder.DatabaseHolder
import org.junit.AfterClass
import org.junit.Before
import org.junit.BeforeClass
import org.junit.Test
import kotlin.test.assertEquals

class UserRepositoryTest{
    lateinit var email:String
    lateinit var userRepository: UserRepositoryImpl

    @Before
    fun setup(){
        email = "meuUser@email.com"
        userRepository= UserRepositoryImpl()
    }
    companion object {
        @JvmStatic
        @BeforeClass
        fun startDB(){
            DatabaseHolder.start()
        }

        @JvmStatic
        @AfterClass
        fun stopDB(){
            DatabaseHolder.stop()
        }
    }

    @Test
    fun `given an valid email, when request it via findByEmail, should return the user`(){
        //given userDTO
        val userDTO = UserFactory.sampleDTO(email = email)
        userRepository.add(userDTO)

        //when
        val user = userRepository.findByEmail(email)

        userDTO.id = 1

        //then
        assertEquals(userDTO, user)
    }
}
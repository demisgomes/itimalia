package domain.repositories

import domain.exceptions.UserNotFoundException
import domain.repositories.factories.UserFactory
import holder.DatabaseHolder
import org.junit.AfterClass
import org.junit.Before
import org.junit.BeforeClass
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals

class UserRepositoryTest{
    lateinit var email:String
    lateinit var password: String
    private val userRepository = UserRepositoryImpl()

    @Before
    fun setup(){
        email = "meuUser@email.com"
        password = "minhaSenha"
        DatabaseHolder.tearDown()
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

    @Test(expected = UserNotFoundException::class)
    fun `given an nonexistent email, when request it via findByEmail, should expect a UserNotFoundException`(){
        //given none

        //when
        userRepository.findByEmail(email)

        //then exception
    }


    @Test
    fun `given an valid user in database, when correctly find by credentials, should return the user`(){
        //given userDTO
        val userDTO = UserFactory.sampleDTO(email = email, password = password)
        userRepository.add(userDTO)

        //when
        val user = userRepository.findByCredentials(email, password)

        userDTO.id = 1

        //then
        assertEquals(userDTO, user)
    }

    @Test(expected = UserNotFoundException::class)
    fun `given an valid user in database, when did not find by credentials, should return UserNotFoundException`(){
        //given userDTO
        val userDTO = UserFactory.sampleDTO(email = email, password = password)
        userRepository.add(userDTO)

        //when
        userRepository.findByCredentials(email, "n")

        //then exception
    }

    @Test
    fun `given a valid user, when updates it, should update it updating the modification date`(){
        //given userDTO
        val userDTO = UserFactory.sampleDTO()
        userRepository.add(userDTO)
        val userAddedDTO = userRepository.get(1)

        val newUserDTO = UserFactory.sampleDTO(email = "myNewEmail@email.com")
        //when
        userRepository.update(1, newUserDTO)

        //then
        val updatedUserDTO = userRepository.get(1)
        assertEquals(userAddedDTO.password, updatedUserDTO.password)
        assertEquals(userAddedDTO.gender, updatedUserDTO.gender)
        assertNotEquals(userAddedDTO.birthDate, updatedUserDTO.birthDate)
        assertEquals(userAddedDTO.creationDate, updatedUserDTO.creationDate)
        assertNotEquals(userAddedDTO.modificationDate, updatedUserDTO.modificationDate)
        assertNotEquals(userAddedDTO.email, updatedUserDTO.email)
    }

    @Test(expected = UserNotFoundException::class)
    fun `given a non existent user, when try updates it, should expect UserNotFoundException`(){
        //given none
        val newUserDTO = UserFactory.sampleDTO(email = "myNewEmail@email.com")
        //when
        userRepository.update(1, newUserDTO)

        //then exception
    }

    @Test(expected = UserNotFoundException::class)
    fun `given a user that exists in database, when call a delete operation, should remove the user`(){
        //given userDTO
        val userDTO = UserFactory.sampleDTO()
        userRepository.add(userDTO)

        //when
        userRepository.delete(1)

        //then
        userRepository.get(1)
        //throws exception
    }
}
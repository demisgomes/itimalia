package resources.storage.exposed.gateways

import com.abrigo.itimalia.domain.exceptions.EmailAlreadyExistsException
import com.abrigo.itimalia.domain.exceptions.UserNotFoundException
import com.abrigo.itimalia.factories.AnimalFactory
import com.abrigo.itimalia.factories.UserFactory
import com.abrigo.itimalia.holder.DatabaseHolder
import com.abrigo.itimalia.resources.storage.exposed.entities.AnimalEntity
import com.abrigo.itimalia.resources.storage.exposed.entities.UserEntity
import com.abrigo.itimalia.resources.storage.exposed.entities.UserMap
import com.abrigo.itimalia.resources.storage.exposed.gateways.UserRepositoryImpl
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.sql.transactions.transaction
import org.junit.AfterClass
import org.junit.Before
import org.junit.BeforeClass
import org.junit.Test
import java.util.Optional
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals

class UserRepositorImplTest {
    lateinit var email: String
    private lateinit var password: String
    private val userRepository = UserRepositoryImpl()

    @Before
    fun setup() {
        email = "meuUser@email.com"
        password = "minhaSenha"
        DatabaseHolder.tearDown()
    }
    companion object {
        @JvmStatic
        @BeforeClass
        fun startDB() {
            DatabaseHolder.start()
        }

        @JvmStatic
        @AfterClass
        fun stopDB() {
            DatabaseHolder.stop()
        }
    }

    @Test
    fun `given an valid email, when request it via findByEmail, should return the user`() {
        // given userDTO
        val userDTO = UserFactory.sampleDTO(email = email)
        userRepository.add(userDTO)

        // when
        val user = userRepository.findByEmail(email)

        val expectedUser = userDTO.copy(id = 2)

        // then
        assertEquals(expectedUser, user.get())
    }

    @Test
    fun `given an nonexistent email, when request it via findByEmail, should expect a Optional empty()`() {
        // given none

        // when
        assertEquals(Optional.empty(), userRepository.findByEmail(email))

        // then exception
    }

    @Test
    fun `given an valid user in database, when find by its id and have an adopted animal, should return the user`() {
        // given userDTO
        val userDTO = UserFactory.sampleDTO()
        val addedUser = userRepository.add(userDTO)
        val animal = AnimalFactory.sampleDTO(name = "adopted animal")

        transaction {
            AnimalEntity.new {
                name = animal.name
                age = animal.age
                timeUnit = animal.timeUnit?.toString()
                specie = animal.specie.toString()
                description = animal.description
                creationDate = animal.creationDate
                modificationDate = animal.modificationDate
                status = animal.status.toString()
                sex = animal.sex.toString()
                size = animal.size.toString()
                castrated = animal.castrated
                createdById = EntityID(animal.createdById, UserMap)
                adoptedBy = addedUser.id?.let { UserEntity[it] }
            }
        }

        // when
        val user = addedUser.id?.let { userRepository.get(it) }

        // then
        assertEquals(1, user?.adoptedAnimals?.size)
        assertEquals("adopted animal", user?.adoptedAnimals?.first()?.name)
    }

    @Test
    fun `given a valid user, when updates it, should update it updating the modification date`() {
        // given userDTO
        val userDTO = UserFactory.sampleDTO()
        userRepository.add(userDTO)
        val userAddedDTO = userRepository.get(2)

        val newUserDTO = UserFactory.sampleDTO(email = "myNewEmail@email.com")
        // when
        userRepository.update(2, newUserDTO)

        // then
        val updatedUserDTO = userRepository.get(2)
        assertEquals(userAddedDTO.password, updatedUserDTO.password)
        assertEquals(userAddedDTO.gender, updatedUserDTO.gender)
        assertNotEquals(userAddedDTO.birthDate, updatedUserDTO.birthDate)
        assertEquals(userAddedDTO.creationDate, updatedUserDTO.creationDate)
        assertNotEquals(userAddedDTO.modificationDate, updatedUserDTO.modificationDate)
        assertNotEquals(userAddedDTO.email, updatedUserDTO.email)
    }

    @Test(expected = UserNotFoundException::class)
    fun `given a non existent user, when try updates it, should expect UserNotFoundException`() {
        // given none
        val newUserDTO = UserFactory.sampleDTO(email = "myNewEmail@email.com")
        // when
        userRepository.update(2, newUserDTO)

        // then exception
    }

    @Test(expected = UserNotFoundException::class)
    fun `given a user that exists in database, when call a delete operation, should remove the user`() {
        // given userDTO
        val userDTO = UserFactory.sampleDTO()
        userRepository.add(userDTO)

        // when
        userRepository.delete(1)

        // then
        userRepository.get(1)
        // throws exception
    }

    @Test(expected = EmailAlreadyExistsException::class)
    fun `given a user that exists in database, when call a add operation with same email, should return unique exception`() {
        // given userDTO
        val userDTO = UserFactory.sampleDTO()
        userRepository.add(userDTO)

        // when
        userRepository.add(userDTO)

        // then throws exception
    }

    @Test(expected = EmailAlreadyExistsException::class)
    fun `given two users in database, when call a update operation changing the first user email to same email of the second user, should return email exists exception`() {
        // given userDTO
        val secondUserEmail = "secondUser@mail.com"

        val firstUser = UserFactory.sampleDTO()
        val addedFirstUser = userRepository.add(firstUser)

        val secondUser = UserFactory.sampleDTO(email = secondUserEmail)

        userRepository.add(secondUser)

        // when
        userRepository.update(addedFirstUser.id!!, addedFirstUser.copy(email = secondUser.email))

        // then throws exception
    }
}

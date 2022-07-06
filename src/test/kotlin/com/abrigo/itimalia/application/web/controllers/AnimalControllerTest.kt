package com.abrigo.itimalia.application.web.controllers

import com.abrigo.itimalia.application.web.accessmanagers.JWTAccessManager
import com.abrigo.itimalia.domain.entities.animal.Animal
import com.abrigo.itimalia.domain.entities.animal.AnimalRequest
import com.abrigo.itimalia.domain.entities.animal.AnimalStatus
import com.abrigo.itimalia.domain.entities.animal.NewAnimalRequest
import com.abrigo.itimalia.domain.entities.animal.TimeUnit
import com.abrigo.itimalia.domain.services.AnimalService
import com.abrigo.itimalia.domain.services.UserService
import com.abrigo.itimalia.factories.AnimalFactory
import com.abrigo.itimalia.factories.UserFactory
import io.javalin.http.Context
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.eclipse.jetty.http.HttpStatus
import org.joda.time.DateTime
import org.junit.Before
import org.junit.Ignore
import org.junit.Test

@Ignore
// a particular behavior is breaking the tests: https://github.com/mockk/mockk/issues/502
class AnimalControllerTest {

    private lateinit var animalServiceMock: AnimalService
    private lateinit var actualDateTime: DateTime
    private lateinit var newAnimalRequest: NewAnimalRequest
    private lateinit var expectedAnimal: Animal
    private lateinit var animalController: AnimalController
    private lateinit var jwtAccessManager: JWTAccessManager
    private lateinit var userService: UserService

    private val contextMock: Context = mockk(relaxed = true)

    @Before
    fun setup() {
        animalServiceMock = mockk(relaxed = true)
        jwtAccessManager = mockk(relaxed = true)
        userService = mockk(relaxed = true)

        actualDateTime = DateTime.now()

        newAnimalRequest = AnimalFactory.sampleNewRequest()
        expectedAnimal = AnimalFactory.sampleDTO(creationDate = actualDateTime, modificationDate = actualDateTime)

        animalController = AnimalController(animalServiceMock, jwtAccessManager, userService)
    }

    @Test
    fun `when an admin tries to register a valid animal with name, age (can be null), specie, and description, should return the animal with status 200 OK`() {
        // given newAnimalRequest

        // when
        every { contextMock.body<NewAnimalRequest>() }.returns(newAnimalRequest)
        every { animalServiceMock.add(newAnimalRequest, any()) }.returns(expectedAnimal)
        animalController.addAnimal(contextMock)

        // then
        verify { contextMock.json(expectedAnimal).status(HttpStatus.CREATED_201) }
    }

    @Test
    fun `when an admin tries to register an animal with name, age null, time unit non null, specie, and description, should return the created animal with null age and time unit with status 201 CREATED`() {
        // given newAnimalRequest
        val newAnimalWithNullAge = AnimalFactory.sampleNewRequest(age = null, timeUnit = TimeUnit.YEAR)
        val expectedAnimalWithNullAgeAndTimeUnitDTO = AnimalFactory.sampleDTO(
            age = null,
            timeUnit = null,
            creationDate = actualDateTime,
            modificationDate = actualDateTime
        )
        // when
        every { contextMock.body<NewAnimalRequest>() }.returns(newAnimalWithNullAge)
        every { animalServiceMock.add(newAnimalWithNullAge, any()) }.returns(expectedAnimalWithNullAgeAndTimeUnitDTO)
        animalController.addAnimal(contextMock)

        // then
        verify { contextMock.json(expectedAnimalWithNullAgeAndTimeUnitDTO).status(HttpStatus.CREATED_201) }
    }

    @Test
    fun `when an admin tries to register an animal with name, valid age, time unit null, specie, and description, should return the created animal with time unit in years with status 201 CREATED`() {
        // given newAnimalRequest
        val newAnimalWithNullTimeUnit = AnimalFactory.sampleNewRequest(timeUnit = null)
        val expectedAnimalWithTimeUnitInYears = AnimalFactory.sampleDTO(
            age = 3,
            timeUnit = TimeUnit.YEAR,
            creationDate = actualDateTime,
            modificationDate = actualDateTime
        )
        // when
        every { contextMock.body<NewAnimalRequest>() }.returns(newAnimalWithNullTimeUnit)
        every { animalServiceMock.add(newAnimalWithNullTimeUnit, any()) }.returns(expectedAnimalWithTimeUnitInYears)
        animalController.addAnimal(contextMock)

        // then
        verify { contextMock.json(expectedAnimalWithTimeUnitInYears).status(HttpStatus.CREATED_201) }
    }

    @Test
    fun `when any user requests an animal in which the id exists, should return the expected animal with status 200 OK`() {
        // given id = 1

        // when
        every { contextMock.pathParam<Int>("id").get() }.returns(1)
        every { animalServiceMock.get(1) }.returns(expectedAnimal)
        animalController.findAnimal(contextMock)

        // then
        verify { contextMock.json(expectedAnimal).status(HttpStatus.OK_200) }
    }

    @Test
    fun `when an admin tries modify an animal that exists with valid fields, should return the modified animal with status 200 OK`() {
        // given id =1
        val updatedAnimal = AnimalRequest(
            1,
            expectedAnimal.name,
            expectedAnimal.age!! + 1,
            expectedAnimal.timeUnit,
            expectedAnimal.specie,
            expectedAnimal.description,
            expectedAnimal.creationDate,
            expectedAnimal.modificationDate,
            expectedAnimal.status,
            expectedAnimal.deficiencies,
            expectedAnimal.sex,
            expectedAnimal.size,
            expectedAnimal.castrated,
            expectedAnimal.createdById
        )
        val expectedModifiedAnimalDTO = Animal(
            1,
            expectedAnimal.name,
            expectedAnimal.age!! + 1,
            expectedAnimal.timeUnit,
            expectedAnimal.specie,
            expectedAnimal.description,
            expectedAnimal.creationDate,
            actualDateTime,
            expectedAnimal.status,
            expectedAnimal.deficiencies,
            expectedAnimal.sex,
            expectedAnimal.size,
            expectedAnimal.castrated,
            expectedAnimal.createdById
        )

        // when
        every { contextMock.pathParam<Int>("id").get() }.returns(1)
        every { contextMock.body<AnimalRequest>() }.returns(updatedAnimal)
        every { animalServiceMock.get(1) }.returns(expectedAnimal)
        every { animalServiceMock.update(1, updatedAnimal) }.returns(expectedModifiedAnimalDTO)
        animalController.updateAnimal(contextMock)

        // then
        verify { contextMock.json(expectedModifiedAnimalDTO).status(HttpStatus.OK_200) }
    }

    @Test
    fun `when an admin tries to delete an animal that exists, should return no content with status 204`() {
        // given id = 1

        // when
        every { contextMock.pathParam<Int>("id").get() }.returns(1)
        every { animalServiceMock.get(1) }.returns(expectedAnimal)
        animalController.deleteAnimal(contextMock)

        // then
        verify { contextMock.status(HttpStatus.NO_CONTENT_204) }
    }

    @Test
    fun `when an user tries to adopt an animal that is available for adoption, return the animal with AnimalStatus adopted and status 200 OK`() {
        // given id=1
        val id = 1
        val expectedAdoptedAnimalDTO = Animal(
            expectedAnimal.id,
            expectedAnimal.name,
            expectedAnimal.age,
            expectedAnimal.timeUnit,
            expectedAnimal.specie,
            expectedAnimal.description,
            expectedAnimal.creationDate,
            actualDateTime,
            AnimalStatus.ADOPTED,
            expectedAnimal.deficiencies,
            expectedAnimal.sex,
            expectedAnimal.size,
            expectedAnimal.castrated,
            expectedAnimal.createdById
        )

        // when
        every { contextMock.pathParam<Int>("id").get() }.returns(1)
        every { jwtAccessManager.extractEmail(contextMock) }.returns("email")
        every { userService.findByEmail("email") }.returns(UserFactory.sampleDTO(id = 1))
        every { animalServiceMock.adopt(id, 1) }.returns(expectedAdoptedAnimalDTO)

        animalController.adopt(contextMock)

        // then
        verify { contextMock.json(expectedAdoptedAnimalDTO).status(HttpStatus.OK_200) }
    }
}

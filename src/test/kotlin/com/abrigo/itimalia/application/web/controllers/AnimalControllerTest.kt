package com.abrigo.itimalia.application.web.controllers

import com.abrigo.itimalia.domain.entities.animal.AnimalDTO
import com.abrigo.itimalia.domain.entities.animal.AnimalDTORequest
import com.abrigo.itimalia.domain.entities.animal.AnimalStatus
import com.abrigo.itimalia.domain.entities.animal.NewAnimalRequest
import com.abrigo.itimalia.domain.entities.animal.TimeUnit
import com.abrigo.itimalia.domain.repositories.factories.AnimalFactory
import com.abrigo.itimalia.domain.services.AnimalService
import io.javalin.http.Context
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.eclipse.jetty.http.HttpStatus
import org.joda.time.DateTime
import org.junit.Before
import org.junit.Test

class AnimalControllerTest {

    private lateinit var contextMock: Context
    private lateinit var animalServiceMock: AnimalService
    private lateinit var actualDateTime: DateTime
    private lateinit var newAnimalRequest: NewAnimalRequest
    private lateinit var expectedAnimalDTO: AnimalDTO

    private val defaultToken = "Bearer defaultToken"

    @Before
    fun setup() {
        animalServiceMock = mockk(relaxed = true)
        contextMock = mockk(relaxed = true)
        actualDateTime = DateTime.now()

        newAnimalRequest = AnimalFactory.sampleNewRequest()
        expectedAnimalDTO = AnimalFactory.sampleDTO(creationDate = actualDateTime, modificationDate = actualDateTime)

        every { contextMock.header("Authorization") } returns defaultToken

    }

    @Test
    fun `when an admin tries to register a valid animal with name, age (can be null), specie, and description, should return the animal with status 200 OK`() {
        //given newAnimalRequest

        //when
        every { contextMock.body<NewAnimalRequest>() }.returns(newAnimalRequest)
        every { animalServiceMock.add(newAnimalRequest, any()) }.returns(expectedAnimalDTO)
        AnimalController(animalServiceMock).addAnimal(contextMock)

        //then
        verify { contextMock.json(expectedAnimalDTO).status(HttpStatus.CREATED_201) }
    }

    @Test
    fun `when an admin tries to register an animal with name, age null, time unit non null, specie, and description, should return the created animal with null age and time unit with status 201 CREATED`() {
        //given newAnimalRequest
        val newAnimalWithNullAge = AnimalFactory.sampleNewRequest(age = null, timeUnit = TimeUnit.YEAR)
        val expectedAnimalWithNullAgeAndTimeUnitDTO = AnimalFactory.sampleDTO(
            age = null,
            timeUnit = null,
            creationDate = actualDateTime,
            modificationDate = actualDateTime
        )
        //when
        every { contextMock.body<NewAnimalRequest>() }.returns(newAnimalWithNullAge)
        every { animalServiceMock.add(newAnimalWithNullAge, any()) }.returns(expectedAnimalWithNullAgeAndTimeUnitDTO)
        AnimalController(animalServiceMock).addAnimal(contextMock)

        //then
        verify { contextMock.json(expectedAnimalWithNullAgeAndTimeUnitDTO).status(HttpStatus.CREATED_201) }
    }

    @Test
    fun `when an admin tries to register an animal with name, valid age, time unit null, specie, and description, should return the created animal with time unit in years with status 201 CREATED`() {
        //given newAnimalRequest
        val newAnimalWithNullTimeUnit = AnimalFactory.sampleNewRequest(timeUnit = null)
        val expectedAnimalWithTimeUnitInYears = AnimalFactory.sampleDTO(
            age = 3,
            timeUnit = TimeUnit.YEAR,
            creationDate = actualDateTime,
            modificationDate = actualDateTime
        )
        //when
        every { contextMock.body<NewAnimalRequest>() }.returns(newAnimalWithNullTimeUnit)
        every { animalServiceMock.add(newAnimalWithNullTimeUnit, any()) }.returns(expectedAnimalWithTimeUnitInYears)
        AnimalController(animalServiceMock).addAnimal(contextMock)

        //then
        verify { contextMock.json(expectedAnimalWithTimeUnitInYears).status(HttpStatus.CREATED_201) }
    }

    @Test
    fun `when any user requests an animal in which the id exists, should return the expected animal with status 200 OK`() {
        //given id = 1

        //when
        every { contextMock.pathParam("id") }.returns("1")
        every { animalServiceMock.get(1) }.returns(expectedAnimalDTO)
        AnimalController(animalServiceMock).findAnimal(contextMock)

        //then
        verify { contextMock.json(expectedAnimalDTO).status(HttpStatus.OK_200) }
    }

    @Test
    fun `when an admin tries modify an animal that exists with valid fields, should return the modified animal with status 200 OK`() {
        //given id =1
        val updatedAnimal = AnimalDTORequest(
            1,
            expectedAnimalDTO.name,
            expectedAnimalDTO.age!! + 1,
            expectedAnimalDTO.timeUnit,
            expectedAnimalDTO.specie,
            expectedAnimalDTO.description,
            expectedAnimalDTO.creationDate,
            expectedAnimalDTO.modificationDate,
            expectedAnimalDTO.status,
            expectedAnimalDTO.deficiencies,
            expectedAnimalDTO.sex,
            expectedAnimalDTO.size,
            expectedAnimalDTO.castrated,
            expectedAnimalDTO.createdById
        )
        val expectedModifiedAnimalDTO = AnimalDTO(
            1,
            expectedAnimalDTO.name,
            expectedAnimalDTO.age!! + 1,
            expectedAnimalDTO.timeUnit,
            expectedAnimalDTO.specie,
            expectedAnimalDTO.description,
            expectedAnimalDTO.creationDate,
            actualDateTime,
            expectedAnimalDTO.status,
            expectedAnimalDTO.deficiencies,
            expectedAnimalDTO.sex,
            expectedAnimalDTO.size,
            expectedAnimalDTO.castrated,
            expectedAnimalDTO.createdById
        )

        //when
        every { contextMock.pathParam("id") }.returns("1")
        every { contextMock.body<AnimalDTORequest>() }.returns(updatedAnimal)
        every { animalServiceMock.get(1) }.returns(expectedAnimalDTO)
        every { animalServiceMock.update(1, updatedAnimal) }.returns(expectedModifiedAnimalDTO)
        AnimalController(animalServiceMock).updateAnimal(contextMock)

        //then
        verify { contextMock.json(expectedModifiedAnimalDTO).status(HttpStatus.OK_200) }
    }

    @Test
    fun `when an admin tries to delete an animal that exists, should return no content with status 204`() {
        //given id = 1

        //when
        every { contextMock.pathParam("id") }.returns("1")
        every { animalServiceMock.get(1) }.returns(expectedAnimalDTO)
        AnimalController(animalServiceMock).deleteAnimal(contextMock)

        //then
        verify { contextMock.status(HttpStatus.NO_CONTENT_204) }
    }

    @Test
    fun `when an user tries to adopt an animal that is available for adoption, return the animal with AnimalStatus adopted and status 200 OK`() {
        //given id=1
        val id = 1
        val expectedAdoptedAnimalDTO = AnimalDTO(
            expectedAnimalDTO.id,
            expectedAnimalDTO.name,
            expectedAnimalDTO.age,
            expectedAnimalDTO.timeUnit,
            expectedAnimalDTO.specie,
            expectedAnimalDTO.description,
            expectedAnimalDTO.creationDate,
            actualDateTime,
            AnimalStatus.ADOPTED,
            expectedAnimalDTO.deficiencies,
            expectedAnimalDTO.sex,
            expectedAnimalDTO.size,
            expectedAnimalDTO.castrated,
            expectedAnimalDTO.createdById
        )

        //when
        every { contextMock.pathParam("id") }.returns("1")
        every { animalServiceMock.adopt(id) }.returns(expectedAdoptedAnimalDTO)

        AnimalController(animalServiceMock).adopt(contextMock)

        //then
        verify { contextMock.json(expectedAdoptedAnimalDTO).status(HttpStatus.OK_200) }
    }

}
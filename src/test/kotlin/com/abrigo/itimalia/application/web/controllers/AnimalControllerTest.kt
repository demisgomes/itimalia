package com.abrigo.itimalia.application.web.controllers

import com.abrigo.itimalia.domain.entities.AnimalDTO
import com.abrigo.itimalia.domain.entities.AnimalStatus
import com.abrigo.itimalia.domain.entities.NewAnimal
import com.abrigo.itimalia.domain.entities.TimeUnit
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
    private lateinit var newAnimal: NewAnimal
    private lateinit var expectedAnimalDTO: AnimalDTO

    @Before
    fun setup() {
        animalServiceMock = mockk(relaxed = true)
        contextMock = mockk(relaxed = true)
        actualDateTime = DateTime.now()

        newAnimal = AnimalFactory.sampleNew()
        expectedAnimalDTO = AnimalFactory.sampleDTO(creationDate = actualDateTime, modificationDate = actualDateTime)
    }

    @Test
    fun `when an admin tries to register a valid animal with name, age (can be null), specie, and description, should return the animal with status 200 OK`() {
        //given newAnimal

        //when
        every { contextMock.body<NewAnimal>() }.returns(newAnimal)
        every { animalServiceMock.add(newAnimal) }.returns(expectedAnimalDTO)
        AnimalController(animalServiceMock).addAnimal(contextMock)

        //then
        verify { contextMock.json(expectedAnimalDTO).status(HttpStatus.CREATED_201) }
    }

    @Test
    fun `when an admin tries to register an animal with name, age null, time unit non null, specie, and description, should return the created animal with null age and time unit with status 201 CREATED`(){
        //given newAnimal
        val newAnimalWithNullAge=AnimalFactory.sampleNew(age = null, timeUnit = TimeUnit.YEAR)
        val expectedAnimalWithNullAgeAndTimeUnitDTO = AnimalFactory.sampleDTO(age = null, timeUnit = null, creationDate = actualDateTime, modificationDate = actualDateTime)
        //when
        every { contextMock.body<NewAnimal>() }.returns(newAnimalWithNullAge)
        every { animalServiceMock.add(newAnimalWithNullAge) }.returns(expectedAnimalWithNullAgeAndTimeUnitDTO)
        AnimalController(animalServiceMock).addAnimal(contextMock)

        //then
        verify { contextMock.json(expectedAnimalWithNullAgeAndTimeUnitDTO).status(HttpStatus.CREATED_201) }
    }

    @Test
    fun `when an admin tries to register an animal with name, valid age, time unit null, specie, and description, should return the created animal with time unit in years with status 201 CREATED`(){
        //given newAnimal
        val newAnimalWithNullTimeUnit=AnimalFactory.sampleNew(timeUnit = null)
        val expectedAnimalWithTimeUnitInYears = AnimalFactory.sampleDTO(age = 3, timeUnit = TimeUnit.YEAR, creationDate = actualDateTime, modificationDate = actualDateTime)
        //when
        every { contextMock.body<NewAnimal>() }.returns(newAnimalWithNullTimeUnit)
        every { animalServiceMock.add(newAnimalWithNullTimeUnit) }.returns(expectedAnimalWithTimeUnitInYears)
        AnimalController(animalServiceMock).addAnimal(contextMock)

        //then
        verify { contextMock.json(expectedAnimalWithTimeUnitInYears).status(HttpStatus.CREATED_201) }
    }

    @Test
    fun `when any user requests an animal in which the id exists, should return the expected animal with status 200 OK`(){
        //given id = 1

        //when
        every { contextMock.pathParam("id") }.returns("1")
        every { animalServiceMock.get(1) }.returns(expectedAnimalDTO)
        AnimalController(animalServiceMock).findAnimal(contextMock)

        //then
        verify { contextMock.json(expectedAnimalDTO).status(HttpStatus.OK_200) }
    }

    @Test
    fun `when an admin tries modify an animal that exists with valid fields, should return the modified animal with status 200 OK`(){
        //given id =1
        val updatedAnimal=AnimalDTO(1,expectedAnimalDTO.name, expectedAnimalDTO.age!!+1, expectedAnimalDTO.timeUnit, expectedAnimalDTO.specie, expectedAnimalDTO.description, expectedAnimalDTO.creationDate, expectedAnimalDTO.modificationDate, expectedAnimalDTO.status)
        val expectedModifiedAnimalDTO=AnimalDTO(1,expectedAnimalDTO.name, expectedAnimalDTO.age!!+1, expectedAnimalDTO.timeUnit, expectedAnimalDTO.specie, expectedAnimalDTO.description, expectedAnimalDTO.creationDate, actualDateTime, expectedAnimalDTO.status)

        //when
        every { contextMock.pathParam("id") }.returns("1")
        every { contextMock.body<AnimalDTO>() }.returns(updatedAnimal)
        every { animalServiceMock.get(1) }.returns(expectedAnimalDTO)
        every { animalServiceMock.update(1,updatedAnimal) }.returns(expectedModifiedAnimalDTO)
        AnimalController(animalServiceMock).updateAnimal(contextMock)

        //then
        verify { contextMock.json(expectedModifiedAnimalDTO).status(HttpStatus.OK_200) }
    }

    @Test
    fun `when an admin tries to delete an animal that exists, should return no content with status 204`(){
        //given id = 1

        //when
        every { contextMock.pathParam("id") }.returns("1")
        every { animalServiceMock.get(1) }.returns(expectedAnimalDTO)
        AnimalController(animalServiceMock).deleteAnimal(contextMock)

        //then
        verify { contextMock.status(HttpStatus.NO_CONTENT_204) }
    }

    @Test
    fun `when an user tries to adopt an animal that is available for adoption, return the animal with AnimalStatus adopted and status 200 OK`(){
        //given id=1
        val id=1
        val expectedAdoptedAnimalDTO=AnimalDTO(
            expectedAnimalDTO.id,
            expectedAnimalDTO.name,
            expectedAnimalDTO.age,
            expectedAnimalDTO.timeUnit,
            expectedAnimalDTO.specie,
            expectedAnimalDTO.description,
            expectedAnimalDTO.creationDate,
            actualDateTime,
            AnimalStatus.ADOPTED
        )

        //when
        every { contextMock.pathParam("id") }.returns("1")
        every { animalServiceMock.adopt(id) }.returns(expectedAdoptedAnimalDTO)

        AnimalController(animalServiceMock).adopt(contextMock)

        //then
        verify { contextMock.json(expectedAdoptedAnimalDTO).status(HttpStatus.OK_200) }
    }

}
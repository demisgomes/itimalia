package application.web.controllers

import domain.entities.AnimalDTO
import domain.entities.AnimalStatus
import domain.entities.NewAnimal
import domain.entities.Specie
import domain.exceptions.AnimalNotFoundException
import domain.exceptions.ValidationException
import domain.services.AnimalService
import io.javalin.Context
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.eclipse.jetty.http.HttpStatus
import org.junit.Before
import org.junit.Test
import java.util.*

class AnimalControllerTest {

    private lateinit var contextMock: Context
    private lateinit var animalServiceMock: AnimalService
    private lateinit var actualCalendar: Calendar
    private lateinit var newAnimal:NewAnimal
    private lateinit var expectedAnimalDTO:AnimalDTO

    @Before
    fun setup() {
        animalServiceMock = mockk(relaxed = true)
        contextMock = mockk(relaxed = true)
        actualCalendar = Calendar.getInstance()

        newAnimal = NewAnimal("animal", 3, Calendar.MONTH, Specie.CAT, "An animal that needs attention")
        expectedAnimalDTO = AnimalDTO(
            "animal",
            3,
            Calendar.MONTH,
            Specie.CAT,
            "An animal that needs attention",
            actualCalendar.time,
            actualCalendar.time,
            AnimalStatus.AVAILABLE
        )

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
    fun `when an admin tries to register a valid animal with name, age (can be null), specie null, and description, should expect ValidationException and return BAD request with status 400`() {
        //given
        val validationException= ValidationException(hashMapOf("specie" to mutableListOf("Invalid specie. You must choose cat or dog.")))
        val newAnimalWithInvalidSpecie=NewAnimal(newAnimal.name, newAnimal.age, newAnimal.timeUnit, null, "")

        //when
        every { contextMock.body<NewAnimal>() }.returns(newAnimalWithInvalidSpecie)
        every { animalServiceMock.add(newAnimalWithInvalidSpecie) }.throws(validationException)
        AnimalController(animalServiceMock).addAnimal(contextMock)

        //then
        verify { contextMock.json(validationException.createErrorResponse()).status(HttpStatus.BAD_REQUEST_400) }

    }

    @Test
    fun `when an admin tries to register a valid animal with blank name, age (can be null), specie, and description, should expect ValidationException and return BAD request with status 400`() {
        //given
        val validationException = ValidationException(hashMapOf("name" to mutableListOf("Invalid name. Only accept names with letters.")))
        val newAnimalWithInvalidName=NewAnimal("", newAnimal.age, newAnimal.timeUnit, newAnimal.specie, "")

        //when
        every { contextMock.body<NewAnimal>() }.returns(newAnimalWithInvalidName)
        every { animalServiceMock.add(newAnimalWithInvalidName) }.throws(validationException)
        AnimalController(animalServiceMock).addAnimal(contextMock)

        //then
        verify { contextMock.json(validationException.createErrorResponse()).status(HttpStatus.BAD_REQUEST_400) }
    }

    @Test
    fun `when an admin tries to register an animal with name, age null, time unit non null, specie, and description, should return the created animal with null age and time unit with status 201 CREATED`(){
        //given newAnimal
        val newAnimalWithNullAge=NewAnimal("Name", null, newAnimal.timeUnit, newAnimal.specie, "An animal that needs attention")
        val expectedAnimalWithNullAgeAndTimeUnitDTO = AnimalDTO(
            "animal",
            null,
            null,
            Specie.CAT,
            "An animal that needs attention",
            actualCalendar.time,
            actualCalendar.time,
            AnimalStatus.AVAILABLE
        )
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
        val newAnimalWithNullTimeUnit=NewAnimal("Name", 3, null, newAnimal.specie, "An animal that needs attention")
        val expectedAnimalWithTimeUnitInYears = AnimalDTO(
            "animal",
            3,
            Calendar.YEAR,
            Specie.CAT,
            "An animal that needs attention",
            actualCalendar.time,
            actualCalendar.time,
            AnimalStatus.AVAILABLE
        )
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
    fun `when any user requests an animal in which the id not exists, should expect the AnimalNotFoundException with status 404 Not found`(){
        //given id = 1
        val animalNotFoundException= AnimalNotFoundException()

        //when
        every { contextMock.pathParam("id") }.returns("1")
        every { animalServiceMock.get(1) }.throws(animalNotFoundException)
        AnimalController(animalServiceMock).findAnimal(contextMock)

        //then
        verify { contextMock.json(animalNotFoundException.createErrorResponse()).status(HttpStatus.NOT_FOUND_404) }
    }

    @Test
    fun `when an admin tries modify an animal that exists with valid fields, should return the modified animal with status 200 OK`(){
        //given id =1
        val updatedAnimal=AnimalDTO(expectedAnimalDTO.name, expectedAnimalDTO.age!!+1, expectedAnimalDTO.timeUnit, expectedAnimalDTO.specie, expectedAnimalDTO.description, expectedAnimalDTO.creationDate, expectedAnimalDTO.modificationDate, expectedAnimalDTO.status)
        val expectedModifiedAnimalDTO=AnimalDTO(expectedAnimalDTO.name, expectedAnimalDTO.age!!+1, expectedAnimalDTO.timeUnit, expectedAnimalDTO.specie, expectedAnimalDTO.description, expectedAnimalDTO.creationDate, actualCalendar.time, expectedAnimalDTO.status)

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
    fun `when an admin tries modify an animal that not exists with valid fields, should expect AnimalNotFoundException and return status 404 NOT FOUND`(){
        //given id =1
        val animalNotFoundException=AnimalNotFoundException()
        val updatedAnimal=AnimalDTO(expectedAnimalDTO.name, expectedAnimalDTO.age!!+1, expectedAnimalDTO.timeUnit, expectedAnimalDTO.specie, expectedAnimalDTO.description, expectedAnimalDTO.creationDate, expectedAnimalDTO.modificationDate, expectedAnimalDTO.status)

        //when
        every { contextMock.pathParam("id") }.returns("1")
        every { contextMock.body<AnimalDTO>() }.returns(updatedAnimal)
        every { animalServiceMock.get(1) }.throws(animalNotFoundException)
        AnimalController(animalServiceMock).updateAnimal(contextMock)

        //then
        verify { contextMock.json(animalNotFoundException.createErrorResponse()).status(HttpStatus.NOT_FOUND_404) }

    }

    @Test
    fun `when an admin tries to modify a animal with blank name, age (can be null), specie, and description, should expect ValidationException and return BAD request with status 400`() {
        //given
        val validationException = ValidationException(hashMapOf("name" to mutableListOf("Invalid name. Only accept names with letters.")))
        val updatedAnimalWithBlankName=AnimalDTO("", expectedAnimalDTO.age!!+1, expectedAnimalDTO.timeUnit, expectedAnimalDTO.specie, expectedAnimalDTO.description, expectedAnimalDTO.creationDate, expectedAnimalDTO.modificationDate, expectedAnimalDTO.status)

        //when
        every { contextMock.pathParam("id") }.returns("1")
        every { animalServiceMock.get(1) }.returns(expectedAnimalDTO)
        every { contextMock.body<AnimalDTO>() }.returns(updatedAnimalWithBlankName)
        every { animalServiceMock.update(1,updatedAnimalWithBlankName) }.throws(validationException)
        AnimalController(animalServiceMock).updateAnimal(contextMock)

        //then
        verify { contextMock.json(validationException.createErrorResponse()).status(HttpStatus.BAD_REQUEST_400) }
    }


    @Test
    fun `when an admin tries to delete an animal that exists, should return no content with status 204`(){
        //given id = 1

        //when
        every { contextMock.pathParam("id") }.returns("1")
        every { animalServiceMock.get(1) }.returns(expectedAnimalDTO)
        every { animalServiceMock.delete(1) }.returns(expectedAnimalDTO)
        AnimalController(animalServiceMock).deleteAnimal(contextMock)

        //then
        verify { contextMock.status(HttpStatus.NO_CONTENT_204) }

    }

    @Test
    fun `when an admin tries to delete an animal that not exists, should expect an AnimalNotFoundExceptio and return the status 404 NOT FOUND`(){
        //given id = 1
        val animalNotFoundException=AnimalNotFoundException()

        //when
        every { contextMock.pathParam("id") }.returns("1")
        every { animalServiceMock.get(1) }.throws(animalNotFoundException)
        AnimalController(animalServiceMock).deleteAnimal(contextMock)

        //then
        verify { contextMock.json(animalNotFoundException.createErrorResponse()).status(HttpStatus.NOT_FOUND_404) }

    }
}
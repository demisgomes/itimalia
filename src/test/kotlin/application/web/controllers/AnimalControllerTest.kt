package application.web.controllers

import domain.entities.AnimalDTO
import domain.entities.AnimalStatus
import domain.entities.NewAnimal
import domain.entities.Specie
import domain.exceptions.InvalidNameException
import domain.exceptions.InvalidSpecieException
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
    fun `when an admin tries to register a valid animal with name, age (can be null), specie null, and description, should expect InvalidSpecieException and return BAD request with status 400`() {
        //given
        val invalidSpecieException= InvalidSpecieException()
        val newAnimalWithInvalidSpecie=NewAnimal(newAnimal.name, newAnimal.age, newAnimal.timeUnit, null, "")

        //when
        every { contextMock.body<NewAnimal>() }.returns(newAnimalWithInvalidSpecie)
        every { animalServiceMock.add(newAnimalWithInvalidSpecie) }.throws(invalidSpecieException)
        AnimalController(animalServiceMock).addAnimal(contextMock)

        //then
        verify { contextMock.json(invalidSpecieException.createErrorResponse()).status(HttpStatus.BAD_REQUEST_400) }

    }

    @Test
    fun `when an admin tries to register a valid animal with blank name, age (can be null), specie, and description, should expect InvalidNameException and return BAD request with status 400`() {
        //given
        val invalidNameException = InvalidNameException()
        val newAnimalWithInvalidName=NewAnimal("", newAnimal.age, newAnimal.timeUnit, newAnimal.specie, "")

        //when
        every { contextMock.body<NewAnimal>() }.returns(newAnimalWithInvalidName)
        every { animalServiceMock.add(newAnimalWithInvalidName) }.throws(invalidNameException)
        AnimalController(animalServiceMock).addAnimal(contextMock)

        //then
        verify { contextMock.json(invalidNameException.createErrorResponse()).status(HttpStatus.BAD_REQUEST_400) }
    }

    @Test
    fun `when an admin tries to register an animal with name, age null, time unit non null, specie, and description, should return the created animal with null age and time unit with status 200 OK`(){
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
    fun `when an admin tries to register an animal with name, valid age, time unit null, specie, and description, should return the created animal with time unit in years with status 200 OK`(){
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
}
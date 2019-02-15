package application.web.controllers

import domain.entities.AnimalDTO
import domain.entities.AnimalStatus
import domain.entities.NewAnimal
import domain.entities.Specie
import domain.services.AnimalService
import io.javalin.Context
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.eclipse.jetty.http.HttpStatus
import org.junit.Before
import org.junit.Test
import java.util.*
import kotlin.test.assertEquals

class AnimalControllerTest{

    private lateinit var contextMock:Context
    private lateinit var animalServiceMock: AnimalService
    private lateinit var actualCalendar:Calendar

    @Before
    fun setup(){
        animalServiceMock=mockk(relaxed = true)
        contextMock= mockk(relaxed = true)
        actualCalendar= Calendar.getInstance()
    }

    @Test
    fun `when an admin tries to register a valid animal with name, age (can be null), specie, and description, should return the animal with status 200 OK`(){
        //given
        val newAnimal = NewAnimal("animal",3, Calendar.MONTH, Specie.CAT, "An animal that needs attention")

        //when
        every { contextMock.body<NewAnimal>() }.returns(newAnimal)
        val expectedAnimalDTO = AnimalDTO(
            "animal",
            3,
            Calendar.MONTH,
            Specie.CAT,
            "An animal that needs attention",
            actualCalendar.time,
            actualCalendar.time,
            AnimalStatus.AVAILABLE
        )
        //every { Calendar.getInstance() }.returns(ac)
        every { animalServiceMock.add(newAnimal) }.returns(expectedAnimalDTO)
        val animalDTO=AnimalController(animalServiceMock).addAnimal(contextMock)

        //then

        verify { contextMock.json(expectedAnimalDTO).status(HttpStatus.CREATED_201) }
    }
}
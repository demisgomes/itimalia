package domain.services

import domain.entities.AnimalDTO
import domain.entities.AnimalStatus
import domain.entities.NewAnimal
import domain.entities.Specie
import domain.exceptions.AnimalNotFoundException
import domain.exceptions.ValidationException
import domain.repositories.AnimalRepository
import io.mockk.every
import io.mockk.mockk
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.ExpectedException
import java.util.*
import kotlin.test.assertEquals

class AnimalServiceTest{

    private lateinit var animalRepositoryMock: AnimalRepository
    private lateinit var actualCalendar: Calendar
    private lateinit var expectedAnimalDTO: AnimalDTO
    private lateinit var newAnimal: NewAnimal

    @get:Rule
    val expectedEx = ExpectedException.none()

    @Before
    fun setup(){
        actualCalendar= Calendar.getInstance()
        animalRepositoryMock= mockk(relaxed = true)
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

    //GET METHOD TESTS

    @Test
    fun `when request an animal where its id exists, should return the animal`(){
        //given
        val id=1

        //when
        every { animalRepositoryMock.get(id) }.returns(expectedAnimalDTO)
        val animalDTO= AnimalServiceImpl(animalRepositoryMock).get(id)

        //then
        assertEquals(expectedAnimalDTO,animalDTO)
    }

    @Test(expected = AnimalNotFoundException::class)
    fun `when request an animal where its id not exists, should expect an AnimalNotFoundException`(){
        //given
        val id=1
        val animalNotFoundException=AnimalNotFoundException()

        //when
        every { animalRepositoryMock.get(id) }.throws(animalNotFoundException)
        AnimalServiceImpl(animalRepositoryMock).get(id)

        //then expect AnimalNotFoundException
    }

    //POST METHODS

    @Test
    fun `when register a valid animal with whole fields filled correctly, should register it returning the animal with creation, modification, and status AVAILABLE`(){
        //given newAnimal

        //when
        every { animalRepositoryMock.add(newAnimal) }.returns(expectedAnimalDTO)
        val animalDTO= AnimalServiceImpl(animalRepositoryMock).add(newAnimal)

        //then
        assertEquals(expectedAnimalDTO,animalDTO)
    }

    @Test
    fun `when register a valid animal with blank name and other fields filled correctly, should expect ValidationException with message 'invalid Name the name cannot be blank or contain numbers'`(){
        //given
        val newAnimalWithInvalidName=NewAnimal("", newAnimal.age, newAnimal.timeUnit, newAnimal.specie, "")


        //when
        expectedEx.expect(ValidationException::class.java)
        expectedEx.expectMessage("The validation does not successful in following field(s): {name=[Invalid name. The name cannot be blank or contain numbers]}")

        AnimalServiceImpl(animalRepositoryMock).add(newAnimalWithInvalidName)

        //then expect ValidationException with message:The validation does not successful in following field(s): {name=[Invalid Name. The name cannot be blank or contain numbers]
    }
}
package domain.services

import domain.entities.AnimalDTO
import domain.entities.AnimalStatus
import domain.entities.Specie
import domain.exceptions.AnimalNotFoundException
import domain.repositories.AnimalRepository
import io.mockk.every
import io.mockk.mockk
import org.junit.Before
import org.junit.Test
import java.util.*
import kotlin.test.assertEquals

class AnimalServiceTest{

    private lateinit var animalRepositoryMock: AnimalRepository
    private lateinit var actualCalendar: Calendar
    private lateinit var expectedAnimalDTO: AnimalDTO


    @Before
    fun setup(){
        actualCalendar= Calendar.getInstance()
        animalRepositoryMock= mockk(relaxed = true)
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
        val animalDTO=AnimalServiceImpl(animalRepositoryMock).get(id)

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

}
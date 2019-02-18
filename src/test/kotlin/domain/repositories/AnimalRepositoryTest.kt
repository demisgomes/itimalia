package domain.repositories

import domain.entities.AnimalDTO
import domain.entities.AnimalStatus
import domain.entities.Specie
import domain.entities.TimeUnit
import domain.exceptions.AnimalNotFoundException
import io.mockk.every
import io.mockk.mockkStatic
import org.junit.Before
import org.junit.Test
import java.util.*
import kotlin.test.assertEquals

class AnimalRepositoryTest{
    private lateinit var expectedAnimalDTO: AnimalDTO
    private lateinit var actualCalendar: Calendar

    @Before
    fun setup(){
        actualCalendar= Calendar.getInstance()
        mockkStatic(Calendar::class)

        expectedAnimalDTO = AnimalDTO(
            "animal",
            3,
            TimeUnit.MONTH,
            Specie.CAT,
            "An animal that needs attention",
            actualCalendar.time,
            actualCalendar.time,
            AnimalStatus.AVAILABLE
        )
    }

    @Test
    fun `when adds an animal in database, return it`(){
        //given expectedAnimalDTO

        //when
        val animalDTO=AnimalRepositoryImpl().add(expectedAnimalDTO)

        //then
        assertEquals(expectedAnimalDTO, animalDTO)
    }

}
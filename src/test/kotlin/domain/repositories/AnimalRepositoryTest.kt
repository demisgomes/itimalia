package domain.repositories

import domain.entities.AnimalDTO
import domain.entities.AnimalStatus
import domain.entities.Specie
import domain.entities.TimeUnit
import holder.DatabaseHolder
import io.mockk.mockkStatic
import org.joda.time.DateTime
import org.junit.*
import java.util.*
import kotlin.test.assertEquals

class AnimalRepositoryTest{
    private lateinit var expectedAnimalDTO: AnimalDTO
    private lateinit var actualDateTime: DateTime

    @Before
    fun setup(){
        actualDateTime= DateTime.now()
        expectedAnimalDTO = AnimalDTO(
            "animal",
            3,
            TimeUnit.MONTH,
            Specie.CAT,
            "An animal that needs attention",
            actualDateTime,
            actualDateTime,
            AnimalStatus.AVAILABLE
        )
    }

    companion object {
        @JvmStatic
        @BeforeClass
        fun startDB(){
            DatabaseHolder.start()
        }

        @JvmStatic
        @AfterClass
        fun stopDB(){
            DatabaseHolder.stop()
        }
    }




    @Test
    fun `when adds an animal in database, return it`(){
        //given expectedAnimalDTO

        //when
        val animalDTO=AnimalRepositoryImpl().add(expectedAnimalDTO)

        //then
        assertEquals(expectedAnimalDTO, animalDTO)
    }

    @Test
    fun `when gets an previous added animal in database, return it`(){
        //given expectedAnimalDTO
        AnimalRepositoryImpl().add(expectedAnimalDTO)

        //when
        val returnedAnimalDTO=AnimalRepositoryImpl().get(1)

        //then
        assertEquals(expectedAnimalDTO, returnedAnimalDTO)
    }

}
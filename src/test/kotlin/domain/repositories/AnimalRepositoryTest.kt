package domain.repositories

import domain.entities.AnimalDTO
import domain.entities.AnimalStatus
import domain.entities.Specie
import domain.entities.TimeUnit
import domain.repositories.factories.AnimalFactory
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
        expectedAnimalDTO = AnimalFactory.sample()
        DatabaseHolder.tearDown()
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

    @Test
    fun `when add many animals in database, should return all via getAll()`(){
        //given
        val mia = AnimalFactory.sample(name = "Mia")
        val lala = AnimalFactory.sample(name = "Lala", age = 8, timeUnit = TimeUnit.YEAR)
        val emy = AnimalFactory.sample(name = "Emy", age = 5, timeUnit = TimeUnit.YEAR)

        AnimalRepositoryImpl().add(mia)
        AnimalRepositoryImpl().add(lala)
        AnimalRepositoryImpl().add(emy)

        val listAnimals = listOf(mia, lala, emy)

        //when
        val returnedAnimals=AnimalRepositoryImpl().getAll()

        //then
        assertEquals(listAnimals, returnedAnimals)
    }

}
package com.abrigo.itimalia.domain.repositories

import com.abrigo.itimalia.domain.entities.AnimalDTO
import com.abrigo.itimalia.domain.entities.TimeUnit
import com.abrigo.itimalia.domain.exceptions.AnimalNotFoundException
import com.abrigo.itimalia.domain.repositories.factories.AnimalFactory
import com.abrigo.itimalia.holder.DatabaseHolder
import org.joda.time.DateTime
import org.junit.AfterClass
import org.junit.Before
import org.junit.BeforeClass
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals

class AnimalRepositoryTest{
    private lateinit var expectedAnimalDTO: AnimalDTO
    private lateinit var actualDateTime: DateTime
    private val animalRepository = AnimalRepositoryImpl() 

    @Before
    fun setup(){
        actualDateTime= DateTime.now()
        expectedAnimalDTO = AnimalFactory.sampleDTO()
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
        val animalDTO=animalRepository.add(expectedAnimalDTO)

        //then
        assertEquals(expectedAnimalDTO, animalDTO)
    }

    @Test
    fun `when gets an previous added animal in database, return it`(){
        //given expectedAnimalDTO
        animalRepository.add(expectedAnimalDTO)

        //when
        val returnedAnimalDTO=animalRepository.get(1)

        //then
        assertEquals(expectedAnimalDTO, returnedAnimalDTO)
    }

    @Test
    fun `when add many animals in database, should return all via getAll()`(){
        //given
        val mia = AnimalFactory.sampleDTO(id = 1, name = "Mia")
        val lala = AnimalFactory.sampleDTO(id = 2, name = "Lala", age = 8, timeUnit = TimeUnit.YEAR)
        val emy = AnimalFactory.sampleDTO(id = 3, name = "Emy", age = 5, timeUnit = TimeUnit.YEAR)

        animalRepository.add(mia)
        animalRepository.add(lala)
        animalRepository.add(emy)

        val listAnimals = listOf(mia, lala, emy)

        //when
        val returnedAnimals=animalRepository.getAll()

        //then
        assertEquals(listAnimals, returnedAnimals)
    }

    @Test(expected = AnimalNotFoundException::class)
    fun `when does not exist animal, an update call should return AnimalNotFoundException`(){
        //given none

        //when
        animalRepository.update(1, expectedAnimalDTO)

        //then
        //AnimalNotFoundException
    }

    @Test(expected = AnimalNotFoundException::class)
    fun `when does not exist animal, a delete call should return AnimalNotFoundException`(){
        //given none

        //when
        animalRepository.delete(1)

        //then
        //AnimalNotFoundException
    }

    @Test
    fun `when updates a previous animal should return the animal`(){
        //given
        val mia = AnimalFactory.sampleDTO(name = "Mia", creationDate = actualDateTime, modificationDate = actualDateTime)
        val lala = AnimalFactory.sampleDTO(name = "Lala", age = 8, timeUnit = TimeUnit.YEAR, creationDate = actualDateTime , modificationDate = actualDateTime)

        //when
        animalRepository.add(mia)
        animalRepository.update(1,lala)

        val updatedAnimal = animalRepository.get(1)

        //then
        assertEquals("Lala", updatedAnimal.name)
        assertEquals(8, updatedAnimal.age)
        assertEquals(TimeUnit.YEAR, updatedAnimal.timeUnit)
        assertNotEquals(actualDateTime, updatedAnimal.modificationDate)
        assertEquals(actualDateTime, updatedAnimal.creationDate)
    }

    @Test(expected = AnimalNotFoundException::class)
    fun `when remove an existent animal, remove it`(){
        //given
        val mia = AnimalFactory.sampleDTO(name = "Mia", creationDate = actualDateTime, modificationDate = actualDateTime)
        animalRepository.add(mia)

        //when
        animalRepository.delete(1)

        //then
        animalRepository.get(1)
    }

}
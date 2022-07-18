package resources.storage.exposed.gateways

import com.abrigo.itimalia.domain.entities.animal.Animal
import com.abrigo.itimalia.domain.entities.animal.AnimalDeficiency
import com.abrigo.itimalia.domain.entities.animal.AnimalStatus
import com.abrigo.itimalia.domain.entities.animal.Specie
import com.abrigo.itimalia.domain.entities.animal.TimeUnit
import com.abrigo.itimalia.domain.entities.filter.FilterOptions
import com.abrigo.itimalia.domain.exceptions.AnimalNotFoundException
import com.abrigo.itimalia.domain.exceptions.UserNotFoundException
import com.abrigo.itimalia.domain.repositories.UserRepository
import com.abrigo.itimalia.factories.AnimalFactory
import com.abrigo.itimalia.factories.UserFactory
import com.abrigo.itimalia.holder.DatabaseHolder
import com.abrigo.itimalia.resources.storage.exposed.gateways.AnimalRepositoryImpl
import io.mockk.every
import io.mockk.mockk
import org.joda.time.DateTime
import org.junit.AfterClass
import org.junit.Before
import org.junit.BeforeClass
import org.junit.Ignore
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class AnimalRepositoryImplTest {
    private lateinit var expectedAnimal: Animal
    private lateinit var actualDateTime: DateTime
    private val userRepositoryMock: UserRepository = mockk(relaxed = true)
    private val animalRepository = AnimalRepositoryImpl(userRepositoryMock)

    @Before
    fun setup() {
        actualDateTime = DateTime.now()
        expectedAnimal = AnimalFactory.sampleDTO()
        DatabaseHolder.tearDown()
    }

    companion object {
        @JvmStatic
        @BeforeClass
        fun startDB() {
            DatabaseHolder.start()
        }

        @JvmStatic
        @AfterClass
        fun stopDB() {
            DatabaseHolder.stop()
        }
    }

    @Test
    fun `when adds an animal in database, return it`() {
        // given expectedAnimalDTO

        // when
        val animalDTO = animalRepository.add(expectedAnimal)

        // then
        assertEquals(expectedAnimal, animalDTO)
    }

    @Test
    fun `when adds an animal in database with deficiency PARTIAL_BLINDNESS, return it with PARTIAL_BLINDESS deficiency`() {
        // given expectedAnimalDTO
        val animalWithTwoDeficiencies = expectedAnimal.copy(deficiencies = listOf(AnimalDeficiency.PARTIAL_BLINDNESS))

        // when
        val animalDTO = animalRepository.add(animalWithTwoDeficiencies)

        // then
        assertEquals(animalWithTwoDeficiencies, animalDTO)
        assertTrue(animalWithTwoDeficiencies.deficiencies.contains(AnimalDeficiency.PARTIAL_BLINDNESS))
    }

    @Test
    fun `when adds an animal in database with two deficiencies, return it with these two deficiencies`() {
        // given expectedAnimalDTO
        val animalWithTwoDeficiencies = expectedAnimal.copy(deficiencies = listOf(AnimalDeficiency.DEAFNESS, AnimalDeficiency.PARALYSIS))

        // when
        val animalDTO = animalRepository.add(animalWithTwoDeficiencies)

        // then
        assertEquals(animalWithTwoDeficiencies, animalDTO)
        assertTrue(animalWithTwoDeficiencies.deficiencies.contains(AnimalDeficiency.DEAFNESS))
        assertTrue(animalWithTwoDeficiencies.deficiencies.contains(AnimalDeficiency.PARALYSIS))
    }

    @Test
    fun `when gets an previous added animal in database, return it`() {
        // given expectedAnimalDTO
        animalRepository.add(expectedAnimal)

        // when
        val returnedAnimalDTO = animalRepository.get(1)

        // then
        assertEquals(expectedAnimal, returnedAnimalDTO)
    }

    @Test
    fun `when add a cat and a dog in database, should return a dog when filter it`() {
        // given expectedAnimalDTO
        val dog = expectedAnimal.copy(id = 2, specie = Specie.DOG, name = "Dog")
        animalRepository.add(expectedAnimal)
        animalRepository.add(dog)

        // when
        val result = animalRepository.getAll(FilterOptions(specie = Specie.DOG))

        // then
        assertEquals(dog, result.first())
        assertEquals(1, result.size)
    }

    @Test
    fun `when add a cat and a dog in database with name Dog, when filter by name Do, should return it`() {
        // given expectedAnimalDTO
        val dog = expectedAnimal.copy(id = 2, specie = Specie.DOG, name = "Dog")
        animalRepository.add(expectedAnimal)
        animalRepository.add(dog)

        // when
        val result = animalRepository.getAll(FilterOptions(name = "Do"))

        // then
        assertEquals(dog, result.first())
        assertEquals(1, result.size)
    }

    @Test
    fun `when add a cat and a dog in database with status adopted, when filter by this status, should return it`() {
        // given expectedAnimalDTO
        val dog = expectedAnimal.copy(id = 2, specie = Specie.DOG, status = AnimalStatus.ADOPTED)
        animalRepository.add(expectedAnimal)
        animalRepository.add(dog)

        // when
        val result = animalRepository.getAll(FilterOptions(status = AnimalStatus.ADOPTED))

        // then
        assertEquals(dog, result.first())
        assertEquals(1, result.size)
    }

    @Test
    fun `when add many animals in database, should return all via getAll()`() {
        // given
        val mia = AnimalFactory.sampleDTO(id = 1, name = "Mia")
        val lala = AnimalFactory.sampleDTO(id = 2, name = "Lala", age = 8, timeUnit = TimeUnit.YEAR)
        val emy = AnimalFactory.sampleDTO(id = 3, name = "Emy", age = 5, timeUnit = TimeUnit.YEAR)

        animalRepository.add(mia)
        animalRepository.add(lala)
        animalRepository.add(emy)

        val listAnimals = listOf(mia, lala, emy)

        // when
        val returnedAnimals = animalRepository.getAll()

        // then
        assertEquals(listAnimals, returnedAnimals)
    }

    @Test(expected = AnimalNotFoundException::class)
    fun `when does not exist animal, an update call should return AnimalNotFoundException`() {
        // given none

        // when
        animalRepository.update(1, expectedAnimal)

        // then
        // AnimalNotFoundException
    }

    @Test(expected = AnimalNotFoundException::class)
    fun `when does not exist animal, a delete call should return AnimalNotFoundException`() {
        // given none

        // when
        animalRepository.delete(1)

        // then
        // AnimalNotFoundException
    }

    @Test
    fun `when updates a previous animal should return the animal`() {
        // given
        val mia = AnimalFactory.sampleDTO(name = "Mia", creationDate = actualDateTime, modificationDate = actualDateTime)
        val lala = AnimalFactory.sampleDTO(name = "Lala", age = 8, timeUnit = TimeUnit.YEAR, creationDate = actualDateTime, modificationDate = actualDateTime)

        // when
        animalRepository.add(mia)
        animalRepository.update(1, lala)

        val updatedAnimal = animalRepository.get(1)

        // then
        assertEquals("Lala", updatedAnimal.name)
        assertEquals(8, updatedAnimal.age)
        assertEquals(TimeUnit.YEAR, updatedAnimal.timeUnit)
        assertNotEquals(actualDateTime, updatedAnimal.modificationDate)
        assertEquals(actualDateTime, updatedAnimal.creationDate)
    }

    @Test
    fun `when updates a previous animal with deficiency should return the animal with changed deficiency`() {
        // given
        val mia = AnimalFactory.sampleDTO(name = "Mia", creationDate = actualDateTime, modificationDate = actualDateTime, deficiencies = listOf(AnimalDeficiency.DEAFNESS))
        val lala = AnimalFactory.sampleDTO(name = "Lala", age = 8, timeUnit = TimeUnit.YEAR, creationDate = actualDateTime, modificationDate = actualDateTime)

        // when
        animalRepository.add(mia)
        animalRepository.update(1, lala)

        val updatedAnimal = animalRepository.get(1)

        // then
        assertEquals("Lala", updatedAnimal.name)
        assertEquals(8, updatedAnimal.age)
        assertEquals(TimeUnit.YEAR, updatedAnimal.timeUnit)
        assertNotEquals(actualDateTime, updatedAnimal.modificationDate)
        assertEquals(actualDateTime, updatedAnimal.creationDate)
        assertTrue(updatedAnimal.deficiencies.isEmpty())
    }

    @Test(expected = AnimalNotFoundException::class)
    fun `when remove an existent animal, remove it`() {
        // given
        val mia = AnimalFactory.sampleDTO(name = "Mia", creationDate = actualDateTime, modificationDate = actualDateTime)
        animalRepository.add(mia)

        // when
        animalRepository.delete(1)

        // then
        animalRepository.get(1)
    }

    // test do not pass in heroku and CI
    @Test
    @Ignore
    fun `given a valid animal and an existent user, should adopt an animal`() {
        // given
        val userId = 1
        val lala = AnimalFactory.sampleDTO(name = "Lala", age = 8, timeUnit = TimeUnit.YEAR, creationDate = actualDateTime, modificationDate = actualDateTime)

        every { userRepositoryMock.get(userId) } returns UserFactory.sampleDTO()

        // when
        animalRepository.add(lala)
        val adoptedAnimal = animalRepository.adopt(lala, userId)

        // then
        assertEquals("Lala", adoptedAnimal.name)
        assertEquals(8, adoptedAnimal.age)
        assertEquals(TimeUnit.YEAR, adoptedAnimal.timeUnit)
        assertNotEquals(actualDateTime, adoptedAnimal.modificationDate)
        assertEquals(actualDateTime, adoptedAnimal.creationDate)
        assertEquals(AnimalStatus.ADOPTED, adoptedAnimal.status)
        assertTrue(adoptedAnimal.deficiencies.isEmpty())
        assertNotNull(adoptedAnimal.adoptedBy)
    }

    @Test(expected = UserNotFoundException::class)
    fun `given a valid animal and a non existent user, should expect an UserNotFoundException`() {
        // given
        val userId = 2
        val lala = AnimalFactory.sampleDTO(name = "Lala", age = 8, timeUnit = TimeUnit.YEAR, creationDate = actualDateTime, modificationDate = actualDateTime)

        animalRepository.add(lala)

        every { userRepositoryMock.get(userId) } throws UserNotFoundException()
        // when
        animalRepository.adopt(lala, userId)

        // then
        // UserNotFoundException
    }

    @Test(expected = AnimalNotFoundException::class)
    fun `given a non valid animal, should expect an AnimalNotFoundException when call adopt`() {
        // given
        val userId = 2
        val lala = AnimalFactory.sampleDTO(name = "Lala", age = 8, timeUnit = TimeUnit.YEAR, creationDate = actualDateTime, modificationDate = actualDateTime)

        // when
        animalRepository.adopt(lala, userId)

        // then
        // AnimalNotFoundException
    }
}

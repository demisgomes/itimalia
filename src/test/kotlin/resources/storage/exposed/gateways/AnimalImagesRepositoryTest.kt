package resources.storage.exposed.gateways

import com.abrigo.itimalia.domain.entities.image.Image
import com.abrigo.itimalia.domain.exceptions.AnimalNotFoundException
import com.abrigo.itimalia.domain.repositories.UserRepository
import com.abrigo.itimalia.factories.AnimalFactory
import com.abrigo.itimalia.holder.DatabaseHolder
import com.abrigo.itimalia.resources.storage.exposed.gateways.AnimalImagesRepositoryImpl
import com.abrigo.itimalia.resources.storage.exposed.gateways.AnimalRepositoryImpl
import io.mockk.mockk
import org.junit.AfterClass
import org.junit.Before
import org.junit.BeforeClass
import org.junit.Test
import kotlin.test.assertEquals

class AnimalImagesRepositoryTest {

    private val expectedAnimal = AnimalFactory.sampleDTO()
    private val animalImagesRepository = AnimalImagesRepositoryImpl()

    private val userRepositoryMock: UserRepository = mockk(relaxed = true)
    private val animalRepository = AnimalRepositoryImpl(userRepositoryMock)

    @Before
    fun setup() {
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
    fun `when adds an image to a valid animal in database, return it`() {
        // given expectedAnimal
        animalRepository.add(expectedAnimal)
        val image = Image("test", "path", "png", 12345L)
        val animalId = 1

        // when
        animalImagesRepository.addAll(listOf(image), animalId)

        // then
        assertEquals(image, animalRepository.get(animalId).images.first())
    }

    @Test(expected = AnimalNotFoundException::class)
    fun `when adds an image to a not existent animal in database, should return AnimalNotFoundException`() {
        // given no animal

        val image = Image("test", "path", "png", 12345L)
        val animalId = 1

        // when
        animalImagesRepository.addAll(listOf(image), animalId)

        // then exception
    }
}

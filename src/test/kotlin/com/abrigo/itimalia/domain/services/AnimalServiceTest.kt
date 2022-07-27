package com.abrigo.itimalia.domain.services

import com.abrigo.itimalia.domain.entities.animal.Animal
import com.abrigo.itimalia.domain.entities.animal.AnimalRequest
import com.abrigo.itimalia.domain.entities.animal.AnimalStatus
import com.abrigo.itimalia.domain.entities.animal.NewAnimal
import com.abrigo.itimalia.domain.entities.animal.NewAnimalRequest
import com.abrigo.itimalia.domain.entities.animal.Specie
import com.abrigo.itimalia.domain.entities.animal.TimeUnit
import com.abrigo.itimalia.domain.entities.filter.FilterOptions
import com.abrigo.itimalia.domain.entities.paging.Direction
import com.abrigo.itimalia.domain.entities.paging.OrderBy
import com.abrigo.itimalia.domain.entities.paging.Page
import com.abrigo.itimalia.domain.entities.paging.Pagination
import com.abrigo.itimalia.domain.entities.paging.PagingOptions
import com.abrigo.itimalia.domain.exceptions.AnimalAlreadyAdoptedException
import com.abrigo.itimalia.domain.exceptions.AnimalDeadException
import com.abrigo.itimalia.domain.exceptions.AnimalGoneException
import com.abrigo.itimalia.domain.exceptions.AnimalNotFoundException
import com.abrigo.itimalia.domain.exceptions.ValidationException
import com.abrigo.itimalia.domain.repositories.AnimalRepository
import com.abrigo.itimalia.domain.validation.Request
import com.abrigo.itimalia.domain.validation.ValidatorRequest
import com.abrigo.itimalia.factories.AnimalFactory
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.verify
import org.joda.time.DateTime
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.ExpectedException
import kotlin.test.assertEquals

class AnimalServiceTest {

    private lateinit var animalRepositoryMock: AnimalRepository
    private lateinit var actualDateTime: DateTime
    private lateinit var expectedAnimal: Animal
    private lateinit var newAnimal: NewAnimal
    private lateinit var newAnimalRequest: NewAnimalRequest
    private lateinit var animalService: AnimalService
    private lateinit var userServiceMock: UserService
    private lateinit var validatorRequest: ValidatorRequest<Request>
    private lateinit var expectedAnimalRequest: AnimalRequest
    private val defaultPagination = Pagination(1, 10, null, 1, 1, OrderBy.ID, Direction.DESC)

    private val animalsList = listOf(
        AnimalFactory.sampleDTO(),
        AnimalFactory.sampleDTO(name = "Lala", specie = Specie.DOG),
        AnimalFactory.sampleDTO(specie = Specie.CAT, status = AnimalStatus.ADOPTED),
        AnimalFactory.sampleDTO(specie = Specie.DOG, status = AnimalStatus.GONE)
    )

    private val defaultId = 1

    @get:Rule
    val expectedEx = ExpectedException.none()

    @Before
    fun setup() {
        actualDateTime = DateTime.now()
        mockkStatic(DateTime::class)
        animalRepositoryMock = mockk(relaxed = true)
        userServiceMock = mockk(relaxed = true)
        validatorRequest = mockk(relaxed = true)
        newAnimal = AnimalFactory.sampleNew()
        newAnimalRequest = AnimalFactory.sampleNewRequest()
        expectedAnimal = AnimalFactory.sampleDTO(creationDate = actualDateTime, modificationDate = actualDateTime)
        expectedAnimalRequest =
            AnimalFactory.sampleRequest(creationDate = actualDateTime, modificationDate = actualDateTime)
        animalService = AnimalServiceImpl(animalRepositoryMock, validatorRequest)
    }

    @Test
    fun `when request an animal where its id exists, should return the animal`() {
        // given
        val id = 1

        // when
        every { animalRepositoryMock.get(id) }.returns(expectedAnimal)
        val animalDTO = animalService.get(id)

        // then
        assertEquals(expectedAnimal, animalDTO)
    }

    @Test
    fun `when request an animal by specie dog, should return the animal`() {
        // given filterOptions
        val filterOptions = FilterOptions(specie = Specie.DOG)
        val lala = AnimalFactory.sampleDTO(name = "Lala", specie = Specie.DOG)

        // when
        every { animalRepositoryMock.getAll(filterOptions) } returns Page(listOf(lala), defaultPagination)

        val returnedList = animalService.getAll(filterOptions)

        // then
        assertEquals(1, returnedList.content.size)
        assertEquals(Specie.DOG, returnedList.content.first().specie)
    }

    @Test
    fun `when request an animal passing paging options, should return the animal`() {
        // given animal
        val lala = AnimalFactory.sampleDTO(name = "Lala", specie = Specie.DOG)
        val pagingOptions = PagingOptions(1, 1)

        // when
        every { animalRepositoryMock.getAll(pagingOptions = pagingOptions) } returns Page(listOf(lala), defaultPagination)

        val returnedList = animalService.getAll(pagingOptions = pagingOptions)

        // then
        assertEquals(1, returnedList.content.size)
        assertEquals(Specie.DOG, returnedList.content.first().specie)
    }

    @Test
    fun `when request all animals, should return all animals`() {
        // given animalsList

        // when
        every { animalRepositoryMock.getAll() } returns Page(animalsList, defaultPagination)
        val animals = animalService.getAll()
        // then
        assertEquals(animals.content, animalsList)
    }

    @Test(expected = AnimalNotFoundException::class)
    fun `when request an animal where its id not exists, should expect an AnimalNotFoundException`() {
        // given
        val id = 1
        val animalNotFoundException = AnimalNotFoundException()

        // when
        every { animalRepositoryMock.get(id) }.throws(animalNotFoundException)
        animalService.get(id)

        // then expect NoSuchElementException
    }

    // POST METHODS

    @Test
    fun `when register a valid animal with whole fields filled correctly, should register it returning the animal with creation, modification, and status AVAILABLE`() {
        // given newAnimal, expectedAnimalDTO

        // when
        every { animalRepositoryMock.add(expectedAnimal.copy(id = null)) }.returns(expectedAnimal)
        every { DateTime.now() }.returns(actualDateTime)
        val animalDTO = animalService.add(newAnimalRequest, defaultId)

        // then
        assertEquals(expectedAnimal, animalDTO)
    }

    @Test
    fun `when register an animal with null age, valid timeUnit and other fields filled correctly, should register the animal with age and timeUnit with null`() {
        // given
        val newAnimalWithAgeNullAndTimeUnitValid = AnimalFactory.sampleNewRequest(age = null)
        val expectedAnimalWithAgeNullAndTimeUnitValidDTO =
            expectedAnimal.copy(id = null, age = null, timeUnit = null)

        // when
        every { animalRepositoryMock.add(expectedAnimalWithAgeNullAndTimeUnitValidDTO) }.returns(
            expectedAnimalWithAgeNullAndTimeUnitValidDTO
        )
        every { DateTime.now() }.returns(actualDateTime)
        val animalDTO = animalService.add(newAnimalWithAgeNullAndTimeUnitValid, defaultId)

        // then
        assertEquals(expectedAnimalWithAgeNullAndTimeUnitValidDTO, animalDTO)
    }

    @Test
    fun `when register an animal with valid age, null timeUnit and other fields filled correctly, should register the animal with specified age and timeUnit in years`() {
        // given
        val newAnimalWithValidAgeAndTimeUnitNull = AnimalFactory.sampleNewRequest(timeUnit = null)
        val expectedAnimalWithAgeNullAndTimeUnitValidDTO =
            expectedAnimal.copy(id = null, age = 3, timeUnit = TimeUnit.YEAR)
        // when
        every { animalRepositoryMock.add(expectedAnimalWithAgeNullAndTimeUnitValidDTO) }.returns(
            expectedAnimalWithAgeNullAndTimeUnitValidDTO
        )
        every { DateTime.now() }.returns(actualDateTime)
        val animalDTO = animalService.add(newAnimalWithValidAgeAndTimeUnitNull, defaultId)

        // then
        assertEquals(expectedAnimalWithAgeNullAndTimeUnitValidDTO, animalDTO)
    }

    // PUT METHODS
    @Test
    fun `when an animal with valid fields and it exists, requests a modification, modify it changing the modification date`() {
        // given
        val id = 1
        val modifiedAnimalDTO = expectedAnimal.copy(description = "An animal that needs more attention")
        val modifiedAnimalDTORequest =
            expectedAnimalRequest.copy(description = "An animal that needs more attention")

        // when
        every { animalRepositoryMock.get(id) }.returns(expectedAnimal)
        every { animalRepositoryMock.update(id, modifiedAnimalDTO) }.returns(modifiedAnimalDTO)
        every { DateTime.now() }.returns(actualDateTime)
        val animalDTO = animalService.update(id, modifiedAnimalDTORequest)

        // then
        assertEquals(modifiedAnimalDTO, animalDTO)
    }

    @Test(expected = AnimalNotFoundException::class)
    fun `when an animal with valid fields and it not exists requests a modification, should expect a AnimalNotFoundException`() {
        // given
        val id = 1
        val modifiedAnimalDTO = expectedAnimalRequest

        // when
        every { animalRepositoryMock.get(id) }.throws(AnimalNotFoundException())
        animalService.update(id, modifiedAnimalDTO)

        // then expect AnimalNotFoundException
    }

    @Test
    fun `when an animal with invalid name, other fields OK, it exists, and requests a modification, should expect a ValidationException with message 'Invalid name the name cannot be blank or contain numbers'`() {
        // given
        val id = 1
        val modifiedAnimalDTORequest = expectedAnimalRequest.copy(name = "")

        every { validatorRequest.validate(modifiedAnimalDTORequest) } throws ValidationException(
            hashMapOf(
                "name: " to mutableListOf(
                    "please fill with a name"
                )
            )
        )
        every { DateTime.now() }.returns(actualDateTime)
        // when
        expectedEx.expect(ValidationException::class.java)
        expectedEx.expectMessage("The constraintValidator does not successful in following field(s): {name: =[please fill with a name]}")

        animalService.update(id, modifiedAnimalDTORequest)
    }

    @Test
    fun `when an animal with invalid specie, other fields OK, it exists, and requests a modification, should expect ValidationException with message 'Invalid specie the specie must be cat or dog'`() {
        // given
        val id = 1
        val modifiedAnimalDTORequest = expectedAnimalRequest.copy(specie = null)

        // when
        expectedEx.expect(ValidationException::class.java)
        expectedEx.expectMessage("The constraintValidator does not successful in following field(s): {specie: null=[please fill with a valid specie: cat or dog]}")

        every { validatorRequest.validate(modifiedAnimalDTORequest) } throws ValidationException(
            hashMapOf(
                "specie: null" to mutableListOf(
                    "please fill with a valid specie: cat or dog"
                )
            )
        )
        every { DateTime.now() }.returns(actualDateTime)
        animalService.update(id, modifiedAnimalDTORequest)
    }

    // DELETE METHODS
    @Test
    fun `when delete an animal where its id exists, should return the animal`() {
        // given
        val id = 1

        // when
        every { animalRepositoryMock.get(id) }.returns(expectedAnimal)

        // then
        animalService.delete(id)
        verify { animalRepositoryMock.delete(id) }
    }

    @Test(expected = AnimalNotFoundException::class)
    fun `when delete an animal where its id not exists, should expect an AnimalNotFoundException`() {
        // given
        val id = 1

        every { animalRepositoryMock.delete(id) }.throws(AnimalNotFoundException())

        // when
        animalService.delete(id)

        // then expect AnimalNotFoundException
    }

    @Test
    fun `when adopt an animal that has AnimalStatus available return the adopted animal`() {
        // given
        val id = 1
        val adopterId = 1
        val expectedAdoptedAnimalDTO = expectedAnimal.copy(status = AnimalStatus.ADOPTED)

        // when
        every { animalRepositoryMock.get(id) }.returns(expectedAnimal)
        every { DateTime.now() }.returns(actualDateTime)
        every { animalRepositoryMock.adopt(expectedAnimal, adopterId) }.returns(expectedAdoptedAnimalDTO)

        val animalDTO = animalService.adopt(id, adopterId)

        // then
        assertEquals(expectedAdoptedAnimalDTO, animalDTO)
    }

    @Test(expected = AnimalAlreadyAdoptedException::class)
    fun `when adopt an animal that has AnimalStatus adopted should expect an AnimalAlreadyAdotpedException`() {
        // given
        val id = 1
        val adopterId = 1
        val expectedAdoptedAnimalDTO = expectedAnimal.copy(status = AnimalStatus.ADOPTED)
        // when
        every { animalRepositoryMock.get(id) }.returns(expectedAdoptedAnimalDTO)
        every { DateTime.now() }.returns(actualDateTime)

        animalService.adopt(id, adopterId)

        // then expect AnimalAlreadyAdoptedException
    }

    @Test(expected = AnimalDeadException::class)
    fun `when adopt an animal that has AnimalStatus dead should expect an AnimalDeadException`() {
        // given
        val id = 1
        val adopterId = 1
        val expectedDeadAnimalDTO = expectedAnimal.copy(status = AnimalStatus.DEAD)

        // when
        every { animalRepositoryMock.get(id) }.returns(expectedDeadAnimalDTO)
        every { DateTime.now() }.returns(actualDateTime)

        animalService.adopt(id, adopterId)

        // then expect AnimalDeadException
    }

    @Test(expected = AnimalGoneException::class)
    fun `when adopt an animal that has AnimalStatus adopted should expect an AnimalAlreadyAdotpedException and return the error with status UNAUTHORIZED 401`() {
        // given
        val id = 1
        val adopterId = 1
        val expectedGoneAnimalDTO = expectedAnimal.copy(status = AnimalStatus.GONE)

        // when
        every { animalRepositoryMock.get(id) }.returns(expectedGoneAnimalDTO)
        every { DateTime.now() }.returns(actualDateTime)

        animalService.adopt(id, adopterId)

        // then expect AnimalGoneException
    }
}

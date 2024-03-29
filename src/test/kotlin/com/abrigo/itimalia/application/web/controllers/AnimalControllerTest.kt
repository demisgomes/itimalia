package com.abrigo.itimalia.application.web.controllers

import com.abrigo.itimalia.application.config.EnvironmentConfig
import com.abrigo.itimalia.application.config.ItimaliaDotenv
import com.abrigo.itimalia.application.web.accessmanagers.JWTAccessManager
import com.abrigo.itimalia.domain.entities.animal.Animal
import com.abrigo.itimalia.domain.entities.animal.AnimalRequest
import com.abrigo.itimalia.domain.entities.animal.AnimalSex
import com.abrigo.itimalia.domain.entities.animal.AnimalSize
import com.abrigo.itimalia.domain.entities.animal.AnimalStatus
import com.abrigo.itimalia.domain.entities.animal.NewAnimalRequest
import com.abrigo.itimalia.domain.entities.animal.Specie
import com.abrigo.itimalia.domain.entities.animal.TimeUnit
import com.abrigo.itimalia.domain.entities.filter.FilterOptions
import com.abrigo.itimalia.domain.entities.paging.Direction
import com.abrigo.itimalia.domain.entities.paging.OrderBy
import com.abrigo.itimalia.domain.entities.paging.Page
import com.abrigo.itimalia.domain.entities.paging.Pagination
import com.abrigo.itimalia.domain.entities.paging.PagingOptions
import com.abrigo.itimalia.domain.exceptions.ValidationException
import com.abrigo.itimalia.domain.services.AnimalService
import com.abrigo.itimalia.domain.services.UserService
import com.abrigo.itimalia.factories.AnimalFactory
import com.abrigo.itimalia.factories.UserFactory
import io.javalin.http.Context
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.eclipse.jetty.http.HttpStatus
import org.joda.time.DateTime
import org.junit.Before
import org.junit.Test

class AnimalControllerTest {

    private lateinit var animalServiceMock: AnimalService
    private lateinit var actualDateTime: DateTime
    private lateinit var newAnimalRequest: NewAnimalRequest
    private lateinit var expectedAnimal: Animal
    private lateinit var animalController: AnimalController
    private lateinit var jwtAccessManager: JWTAccessManager
    private lateinit var userService: UserService
    private lateinit var environmentConfig: EnvironmentConfig
    private val defaultPagination = Pagination(1, 10, null, 1, 1, OrderBy.ID, Direction.ASC)

    private val contextMock: Context = mockk(relaxed = true)

    @Before
    fun setup() {
        animalServiceMock = mockk(relaxed = true)
        jwtAccessManager = mockk(relaxed = true)
        userService = mockk(relaxed = true)
        environmentConfig = EnvironmentConfig(ItimaliaDotenv().build())

        actualDateTime = DateTime.now()

        newAnimalRequest = AnimalFactory.sampleNewRequest()
        expectedAnimal = AnimalFactory.sampleDTO(creationDate = actualDateTime, modificationDate = actualDateTime)

        animalController = AnimalController(animalServiceMock, jwtAccessManager, userService, environmentConfig)
    }

    @Test
    fun `when an admin tries to register a valid animal with name, age (can be null), specie, and description, should return the animal with status 200 OK`() {
        // given newAnimalRequest

        // when
        every { contextMock.bodyAsClass<NewAnimalRequest>() }.returns(newAnimalRequest)
        every { animalServiceMock.add(newAnimalRequest, any()) }.returns(expectedAnimal)
        animalController.addAnimal(contextMock)

        // then
        verify { contextMock.json(expectedAnimal).status(HttpStatus.CREATED_201) }
    }

    @Test
    fun `when an admin tries to register an animal with name, age null, time unit non null, specie, and description, should return the created animal with null age and time unit with status 201 CREATED`() {
        // given newAnimalRequest
        val newAnimalWithNullAge = AnimalFactory.sampleNewRequest(age = null, timeUnit = TimeUnit.YEAR)
        val expectedAnimalWithNullAgeAndTimeUnitDTO = AnimalFactory.sampleDTO(
            age = null,
            timeUnit = null,
            creationDate = actualDateTime,
            modificationDate = actualDateTime
        )
        // when
        every { contextMock.bodyAsClass<NewAnimalRequest>() }.returns(newAnimalWithNullAge)
        every { animalServiceMock.add(newAnimalWithNullAge, any()) }.returns(expectedAnimalWithNullAgeAndTimeUnitDTO)
        animalController.addAnimal(contextMock)

        // then
        verify { contextMock.json(expectedAnimalWithNullAgeAndTimeUnitDTO).status(HttpStatus.CREATED_201) }
    }

    @Test
    fun `when an admin tries to register an animal with name, valid age, time unit null, specie, and description, should return the created animal with time unit in years with status 201 CREATED`() {
        // given newAnimalRequest
        val newAnimalWithNullTimeUnit = AnimalFactory.sampleNewRequest(timeUnit = null)
        val expectedAnimalWithTimeUnitInYears = AnimalFactory.sampleDTO(
            age = 3,
            timeUnit = TimeUnit.YEAR,
            creationDate = actualDateTime,
            modificationDate = actualDateTime
        )
        // when
        every { contextMock.bodyAsClass<NewAnimalRequest>() }.returns(newAnimalWithNullTimeUnit)
        every { animalServiceMock.add(newAnimalWithNullTimeUnit, any()) }.returns(expectedAnimalWithTimeUnitInYears)
        animalController.addAnimal(contextMock)

        // then
        verify { contextMock.json(expectedAnimalWithTimeUnitInYears).status(HttpStatus.CREATED_201) }
    }

    @Test
    fun `when any user requests an animal in which the id exists, should return the expected animal with status 200 OK`() {
        // given id = 1

        // when
        every { contextMock.pathParam("id") }.returns("1")
        every { animalServiceMock.get(1) }.returns(expectedAnimal)
        animalController.findAnimal(contextMock)

        // then
        verify { contextMock.json(expectedAnimal).status(HttpStatus.OK_200) }
    }

    @Test
    fun `when an admin tries modify an animal that exists with valid fields, should return the modified animal with status 200 OK`() {
        // given id =1
        val updatedAnimal = AnimalRequest(
            1,
            expectedAnimal.name,
            expectedAnimal.age!! + 1,
            expectedAnimal.timeUnit,
            expectedAnimal.specie,
            expectedAnimal.description,
            expectedAnimal.creationDate,
            expectedAnimal.modificationDate,
            expectedAnimal.status,
            expectedAnimal.deficiencies,
            expectedAnimal.sex,
            expectedAnimal.size,
            expectedAnimal.castrated,
            expectedAnimal.createdById
        )
        val expectedModifiedAnimalDTO = Animal(
            1,
            expectedAnimal.name,
            expectedAnimal.age!! + 1,
            expectedAnimal.timeUnit,
            expectedAnimal.specie,
            expectedAnimal.description,
            expectedAnimal.creationDate,
            actualDateTime,
            expectedAnimal.status,
            expectedAnimal.deficiencies,
            expectedAnimal.sex,
            expectedAnimal.size,
            expectedAnimal.castrated,
            expectedAnimal.createdById
        )

        // when
        every { contextMock.pathParam("id") }.returns("1")
        every { contextMock.bodyAsClass<AnimalRequest>() }.returns(updatedAnimal)
        every { animalServiceMock.get(1) }.returns(expectedAnimal)
        every { animalServiceMock.update(1, updatedAnimal) }.returns(expectedModifiedAnimalDTO)
        animalController.updateAnimal(contextMock)

        // then
        verify { contextMock.json(expectedModifiedAnimalDTO).status(HttpStatus.OK_200) }
    }

    @Test
    fun `when an admin tries to delete an animal that exists, should return no content with status 204`() {
        // given id = 1

        // when
        every { contextMock.pathParam("id") }.returns("1")
        every { animalServiceMock.get(1) }.returns(expectedAnimal)
        animalController.deleteAnimal(contextMock)

        // then
        verify { contextMock.status(HttpStatus.NO_CONTENT_204) }
    }

    @Test
    fun `when an user tries to adopt an animal that is available for adoption, return the animal with AnimalStatus adopted and status 200 OK`() {
        // given id=1
        val id = 1
        val expectedAdoptedAnimalDTO = Animal(
            expectedAnimal.id,
            expectedAnimal.name,
            expectedAnimal.age,
            expectedAnimal.timeUnit,
            expectedAnimal.specie,
            expectedAnimal.description,
            expectedAnimal.creationDate,
            actualDateTime,
            AnimalStatus.ADOPTED,
            expectedAnimal.deficiencies,
            expectedAnimal.sex,
            expectedAnimal.size,
            expectedAnimal.castrated,
            expectedAnimal.createdById
        )

        // when
        every { contextMock.pathParam("id") }.returns("1")
        every { jwtAccessManager.extractEmail(contextMock) }.returns("email")
        every { userService.findByEmail("email") }.returns(UserFactory.sampleDTO(id = 1))
        every { animalServiceMock.adopt(id, 1) }.returns(expectedAdoptedAnimalDTO)

        animalController.adopt(contextMock)

        // then
        verify { contextMock.json(expectedAdoptedAnimalDTO).status(HttpStatus.OK_200) }
    }

    @Test
    fun `when request all animals, should return them`() {
        val animalsList = listOf(AnimalFactory.sampleDTO())
        val pageAnimals = Page(animalsList, defaultPagination)

        every { animalServiceMock.getAll() } returns pageAnimals
        animalController.findAllAnimals(contextMock)

        verify { contextMock.json(pageAnimals).status(HttpStatus.OK_200) }
    }

    @Test
    fun `when request all animals with status filter, should return filtered`() {
        val animalsList = listOf(AnimalFactory.sampleDTO())
        val pageAnimals = Page(animalsList, defaultPagination)
        val filterOptions = FilterOptions(status = AnimalStatus.AVAILABLE)

        every { animalServiceMock.getAll(filterOptions) } returns pageAnimals

        every { contextMock.queryParam("status") } returns "available"

        animalController.findAllAnimals(contextMock)

        verify { contextMock.json(pageAnimals).status(HttpStatus.OK_200) }
    }

    @Test
    fun `when request all animals with specie filter, should return filtered`() {
        val animalsList = listOf(AnimalFactory.sampleDTO())
        val pageAnimals = Page(animalsList, defaultPagination)
        val filterOptions = FilterOptions(specie = Specie.DOG)

        every { animalServiceMock.getAll(filterOptions) } returns pageAnimals

        every { contextMock.queryParam("specie") } returns "dog"

        animalController.findAllAnimals(contextMock)

        verify { contextMock.json(pageAnimals).status(HttpStatus.OK_200) }
    }

    @Test
    fun `when request all animals with name filter, should return filtered`() {
        val animalsList = listOf(AnimalFactory.sampleDTO())
        val pageAnimals = Page(animalsList, defaultPagination)
        val filterOptions = FilterOptions(name = "animal")

        every { animalServiceMock.getAll(filterOptions) } returns pageAnimals

        every { contextMock.queryParam("name") } returns "animal"

        animalController.findAllAnimals(contextMock)

        verify { contextMock.json(pageAnimals).status(HttpStatus.OK_200) }
    }

    @Test
    fun `when request all animals with sex filter, should return filtered`() {
        val animalsList = listOf(AnimalFactory.sampleDTO())
        val pageAnimals = Page(animalsList, defaultPagination)
        val filterOptions = FilterOptions(sex = AnimalSex.FEMALE)

        every { animalServiceMock.getAll(filterOptions) } returns pageAnimals

        every { contextMock.queryParam("sex") } returns "female"

        animalController.findAllAnimals(contextMock)

        verify { contextMock.json(pageAnimals).status(HttpStatus.OK_200) }
    }

    @Test
    fun `when request all animals with size filter, should return filtered`() {
        val animalsList = listOf(AnimalFactory.sampleDTO())
        val pageAnimals = Page(animalsList, defaultPagination)
        val filterOptions = FilterOptions(size = AnimalSize.MEDIUM)

        every { animalServiceMock.getAll(filterOptions) } returns pageAnimals

        every { contextMock.queryParam("size") } returns "medium"

        animalController.findAllAnimals(contextMock)

        verify { contextMock.json(pageAnimals).status(HttpStatus.OK_200) }
    }

    @Test
    fun `when request all animals with castrated filter, should return filtered`() {
        val animalsList = listOf(AnimalFactory.sampleDTO())
        val pageAnimals = Page(animalsList, defaultPagination)
        val filterOptions = FilterOptions(castrated = true)

        every { animalServiceMock.getAll(filterOptions) } returns pageAnimals

        every { contextMock.queryParam("castrated") } returns "true"

        animalController.findAllAnimals(contextMock)

        verify { contextMock.json(pageAnimals).status(HttpStatus.OK_200) }
    }

    // the next methods not validate the message due to future upgrade to JUnit 5
    @Test(expected = ValidationException::class)
    fun `when request all animals with an invalid specie filter, should throw ValidationException`() {
        every { contextMock.queryParam("specie") } returns "invalid"

        animalController.findAllAnimals(contextMock)
    }

    @Test(expected = ValidationException::class)
    fun `when request all animals with an invalid status filter, should throw ValidationException`() {
        every { contextMock.queryParam("status") } returns "invalid"

        animalController.findAllAnimals(contextMock)
    }

    @Test(expected = ValidationException::class)
    fun `when request all animals with an invalid sex filter, should throw ValidationException`() {
        every { contextMock.queryParam("sex") } returns "invalid"

        animalController.findAllAnimals(contextMock)
    }

    @Test(expected = ValidationException::class)
    fun `when request all animals with an invalid size filter, should throw ValidationException`() {
        every { contextMock.queryParam("size") } returns "invalid"

        animalController.findAllAnimals(contextMock)
    }

    @Test
    fun `when request all animals without paging limits, should call animalService passing default values`() {
        val animalsList = listOf(AnimalFactory.sampleDTO())
        val pageAnimals = Page(animalsList, defaultPagination)

        every { animalServiceMock.getAll() } returns pageAnimals

        animalController.findAllAnimals(contextMock)

        verify { contextMock.json(pageAnimals).status(HttpStatus.OK_200) }
    }

    @Test
    fun `when request all animals with page limit = 1, should call return one animal`() {
        val animalsList = listOf(AnimalFactory.sampleDTO())
        val pageAnimals = Page(animalsList, Pagination(1, 1, null, 1, 1, OrderBy.ID, Direction.ASC))
        val pagingOptions = PagingOptions(1)

        every { contextMock.queryParam("limit") } returns "1"
        every { animalServiceMock.getAll(pagingOptions = pagingOptions) } returns pageAnimals

        animalController.findAllAnimals(contextMock)

        verify { contextMock.json(pageAnimals).status(HttpStatus.OK_200) }
    }

    @Test
    fun `when request all animals with page = 2 and limit = 1, should return the page with one animal`() {
        val animalsList = listOf(AnimalFactory.sampleDTO())
        val pageAnimals = Page(animalsList, Pagination(2, 1, null, 1, 2, OrderBy.ID, Direction.ASC))
        val pagingOptions = PagingOptions(1, 2)

        every { contextMock.queryParam("limit") } returns "1"
        every { contextMock.queryParam("page") } returns "2"

        every { animalServiceMock.getAll(pagingOptions = pagingOptions) } returns pageAnimals

        animalController.findAllAnimals(contextMock)

        verify { contextMock.json(pageAnimals).status(HttpStatus.OK_200) }
    }

    @Test(expected = ValidationException::class)
    fun `when request all animals with page less than 1, should call validation exception`() {
        every { contextMock.queryParam("page") } returns "0"

        animalController.findAllAnimals(contextMock)
    }

    @Test(expected = ValidationException::class)
    fun `when request all animals with limit less than 1, should call validation exception`() {
        every { contextMock.queryParam("limit") } returns "0"

        animalController.findAllAnimals(contextMock)
    }

    @Test(expected = ValidationException::class)
    fun `when request all animals with limit higher than MAX_LIMIT, should call validation exception`() {
        every { contextMock.queryParam("limit") } returns (environmentConfig.maxPageLimit().toInt() + 1).toString()

        animalController.findAllAnimals(contextMock)
    }

    @Test
    fun `when request all animals with direction desc should return list in desc order`() {
        val animalsList = listOf(AnimalFactory.sampleDTO())
        val pageAnimals = Page(animalsList, Pagination(2, 1, null, 1, 2, OrderBy.ID, Direction.ASC))
        val pagingOptions = PagingOptions(1, 2, direction = Direction.DESC)

        every { contextMock.queryParam("limit") } returns "1"
        every { contextMock.queryParam("page") } returns "2"
        every { contextMock.queryParam("direction") } returns "desc"

        every { animalServiceMock.getAll(pagingOptions = pagingOptions) } returns pageAnimals

        animalController.findAllAnimals(contextMock)

        verify { contextMock.json(pageAnimals).status(HttpStatus.OK_200) }
    }

    @Test
    fun `when request all animals with orderBy name should return list in order`() {
        val animalsList = listOf(AnimalFactory.sampleDTO())
        val pageAnimals = Page(animalsList, defaultPagination.copy(orderBy = OrderBy.NAME))
        val pagingOptions = PagingOptions(orderBy = OrderBy.NAME)

        every { contextMock.queryParam("order_by") } returns "name"

        every { animalServiceMock.getAll(pagingOptions = pagingOptions) } returns pageAnimals

        animalController.findAllAnimals(contextMock)

        verify { contextMock.json(pageAnimals).status(HttpStatus.OK_200) }
    }

    @Test
    fun `when request all animals with orderBy modificationDate should return list in order`() {
        val animalsList = listOf(AnimalFactory.sampleDTO())
        val pageAnimals = Page(animalsList, defaultPagination.copy(orderBy = OrderBy.MODIFICATION_DATE))
        val pagingOptions = PagingOptions(orderBy = OrderBy.MODIFICATION_DATE)

        every { contextMock.queryParam("order_by") } returns "modification_date"

        every { animalServiceMock.getAll(pagingOptions = pagingOptions) } returns pageAnimals

        animalController.findAllAnimals(contextMock)

        verify { contextMock.json(pageAnimals).status(HttpStatus.OK_200) }
    }
}

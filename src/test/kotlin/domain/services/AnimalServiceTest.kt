package domain.services

import domain.entities.*
import domain.exceptions.*
import domain.repositories.AnimalRepository
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
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
        mockkStatic(Calendar::class)
        animalRepositoryMock= mockk(relaxed = true)
        newAnimal = NewAnimal("animal", 3, TimeUnit.MONTH, Specie.CAT, "An animal that needs attention")
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
        //given newAnimal, expectedAnimalDTO

        //when
        every { animalRepositoryMock.add(expectedAnimalDTO) }.returns(expectedAnimalDTO)
        every { Calendar.getInstance() }.returns(actualCalendar)
        val animalDTO= AnimalServiceImpl(animalRepositoryMock).add(newAnimal)

        //then
        assertEquals(expectedAnimalDTO,animalDTO)
    }

    @Test
    fun `when register an animal with blank name and other fields filled correctly, should expect ValidationException with message 'invalid Name the name cannot be blank or contain numbers'`(){
        //given
        val newAnimalWithInvalidName=NewAnimal("", newAnimal.age, newAnimal.timeUnit, newAnimal.specie, "")

        //when
        expectedEx.expect(ValidationException::class.java)
        expectedEx.expectMessage("The validation does not successful in following field(s): {name=[Invalid name. The name cannot be blank or contain numbers]}")

        AnimalServiceImpl(animalRepositoryMock).add(newAnimalWithInvalidName)

        //then expect ValidationException with message:The validation does not successful in following field(s): {name=[Invalid Name. The name cannot be blank or contain numbers]
    }

    @Test
    fun `when register an animal with null specie and other fields filled correctly, should expect ValidationException with message 'Invalid specie the specie must be cat or dog'`(){
        //given
        val newAnimalWithInvalidSpecie=NewAnimal(newAnimal.name, newAnimal.age, newAnimal.timeUnit, null, "")

        //when
        expectedEx.expect(ValidationException::class.java)
        expectedEx.expectMessage("The validation does not successful in following field(s): {specie=[Invalid specie. The specie must be cat or dog]}")

        AnimalServiceImpl(animalRepositoryMock).add(newAnimalWithInvalidSpecie)

        //then expect ValidationException with message:The validation does not successful in following field(s): {name=[Invalid Name. The name cannot be blank or contain numbers]

    }

    @Test
    fun `when register an animal with null age, valid timeUnit and other fields filled correctly, should register the animal with age and timeUnit with null`(){
        //given
        val newAnimalWithAgeNullAndTimeUnitValid=NewAnimal(newAnimal.name, null, newAnimal.timeUnit, Specie.CAT, newAnimal.description)
        val expectedAnimalWithAgeNullAndTimeUnitValidDTO=AnimalDTO(
            expectedAnimalDTO.name,
            null,
            null,
            expectedAnimalDTO.specie,
            expectedAnimalDTO.description,
            expectedAnimalDTO.creationDate,
            expectedAnimalDTO.modificationDate,
            AnimalStatus.AVAILABLE)

        //when
        every { animalRepositoryMock.add(expectedAnimalWithAgeNullAndTimeUnitValidDTO) }.returns(expectedAnimalWithAgeNullAndTimeUnitValidDTO)
        every { Calendar.getInstance() }.returns(actualCalendar)
        val animalDTO=AnimalServiceImpl(animalRepositoryMock).add(newAnimalWithAgeNullAndTimeUnitValid)

        //then
        assertEquals(expectedAnimalWithAgeNullAndTimeUnitValidDTO, animalDTO)
    }

    @Test
    fun `when register an animal with valid age, null timeUnit and other fields filled correctly, should register the animal with specified age and timeUnit in years`(){
        //given
        val newAnimalWithValidAgeAndTimeUnitNull=NewAnimal(newAnimal.name, 3, null, Specie.CAT, newAnimal.description)
        val expectedAnimalWithAgeNullAndTimeUnitValidDTO=AnimalDTO(
            expectedAnimalDTO.name,
            3,
            TimeUnit.YEAR,
            expectedAnimalDTO.specie,
            expectedAnimalDTO.description,
            expectedAnimalDTO.creationDate,
            expectedAnimalDTO.modificationDate,
            AnimalStatus.AVAILABLE)

        //when
        every { animalRepositoryMock.add(expectedAnimalWithAgeNullAndTimeUnitValidDTO) }.returns(expectedAnimalWithAgeNullAndTimeUnitValidDTO)
        every { Calendar.getInstance() }.returns(actualCalendar)
        val animalDTO=AnimalServiceImpl(animalRepositoryMock).add(newAnimalWithValidAgeAndTimeUnitNull)

        //then
        assertEquals(expectedAnimalWithAgeNullAndTimeUnitValidDTO, animalDTO)
    }

    //PUT METHODS
    @Test
    fun `when an animal with valid fields and it exists, requests a modification, modify it changing the modification date`(){
        //given
        val id=1
        val modifiedAnimalDTO=AnimalDTO(
            expectedAnimalDTO.name,
            expectedAnimalDTO.age,
            expectedAnimalDTO.timeUnit,
            expectedAnimalDTO.specie,
            "An animal that needs more attention",
            expectedAnimalDTO.creationDate,
            actualCalendar.time,
            AnimalStatus.AVAILABLE
        )

        //when
        every { animalRepositoryMock.get(id) }.returns(expectedAnimalDTO)
        every { animalRepositoryMock.update(id,modifiedAnimalDTO) }.returns(modifiedAnimalDTO)
        every { Calendar.getInstance() }.returns(actualCalendar)
        val animalDTO=AnimalServiceImpl(animalRepositoryMock).update(id,modifiedAnimalDTO)

        //then
        assertEquals(modifiedAnimalDTO, animalDTO)
    }

    @Test(expected = AnimalNotFoundException::class)
    fun `when an animal with valid fields and it not exists requests a modification, should expect a AnimalNotFoundException`(){
        //given
        val id=1
        val modifiedAnimalDTO=AnimalDTO(
            expectedAnimalDTO.name,
            expectedAnimalDTO.age,
            expectedAnimalDTO.timeUnit,
            expectedAnimalDTO.specie,
            "An animal that needs more attention",
            expectedAnimalDTO.creationDate,
            actualCalendar.time,
            AnimalStatus.AVAILABLE
        )

        //when
        every { animalRepositoryMock.get(id) }.throws(AnimalNotFoundException())
        AnimalServiceImpl(animalRepositoryMock).update(id,modifiedAnimalDTO)

        //then expect AnimalNotFoundException
    }

    @Test
    fun `when an animal with invalid name, other fields OK, it exists, and requests a modification, should expect a ValidationException with message 'Invalid name the name cannot be blank or contain numbers'`(){
        //given
        val id=1
        val modifiedAnimalDTO=AnimalDTO(
            "",
            expectedAnimalDTO.age,
            expectedAnimalDTO.timeUnit,
            expectedAnimalDTO.specie,
            "An animal that needs more attention",
            expectedAnimalDTO.creationDate,
            actualCalendar.time,
            AnimalStatus.AVAILABLE
        )

        //when
        expectedEx.expect(ValidationException::class.java)
        expectedEx.expectMessage("The validation does not successful in following field(s): {name=[Invalid name. The name cannot be blank or contain numbers]}")

        every { animalRepositoryMock.get(id) }.returns(expectedAnimalDTO)
        every { Calendar.getInstance() }.returns(actualCalendar)
        val animalDTO=AnimalServiceImpl(animalRepositoryMock).update(id,modifiedAnimalDTO)

        //then
        assertEquals(modifiedAnimalDTO, animalDTO)
    }

    @Test
    fun `when an animal with invalid specie, other fields OK, it exists, and requests a modification, should expect ValidationException with message 'Invalid specie the specie must be cat or dog'`(){
        //given
        val id=1
        val modifiedAnimalDTO=AnimalDTO(
            expectedAnimalDTO.name,
            expectedAnimalDTO.age,
            expectedAnimalDTO.timeUnit,
            null,
            "An animal that needs more attention",
            expectedAnimalDTO.creationDate,
            actualCalendar.time,
            AnimalStatus.AVAILABLE
        )

        //when
        expectedEx.expect(ValidationException::class.java)
        expectedEx.expectMessage("The validation does not successful in following field(s): {specie=[Invalid specie. The specie must be cat or dog]}")

        every { animalRepositoryMock.get(id) }.returns(expectedAnimalDTO)
        every { Calendar.getInstance() }.returns(actualCalendar)
        val animalDTO=AnimalServiceImpl(animalRepositoryMock).update(id,modifiedAnimalDTO)

        //then
        assertEquals(modifiedAnimalDTO, animalDTO)
    }

    //DELETE METHODS
    @Test
    fun `when delete an animal where its id exists, should return the animal`(){
        //given
        val id=1

        //when
        every { animalRepositoryMock.get(id) }.returns(expectedAnimalDTO)
        every{(animalRepositoryMock).delete(id)}.returns(expectedAnimalDTO)
        val animalDTO= AnimalServiceImpl(animalRepositoryMock).delete(id)

        //then
        assertEquals(expectedAnimalDTO,animalDTO)
    }

    @Test(expected = AnimalNotFoundException::class)
    fun `when delete an animal where its id not exists, should expect an AnimalNotFoundException`(){
        //given
        val id=1
        val animalNotFoundException=AnimalNotFoundException()

        //when
        every { animalRepositoryMock.get(id) }.throws(animalNotFoundException)
        AnimalServiceImpl(animalRepositoryMock).delete(id)

        //then expect AnimalNotFoundException
    }

    @Test
    fun `when adopt an animal that has AnimalStatus available return the adopted animal`(){
        //given
        val id=1
        val expectedAdoptedAnimalDTO=AnimalDTO(
            expectedAnimalDTO.name,
            expectedAnimalDTO.age,
            expectedAnimalDTO.timeUnit,
            expectedAnimalDTO.specie,
            expectedAnimalDTO.description,
            expectedAnimalDTO.creationDate,
            actualCalendar.time,
            AnimalStatus.ADOPTED
        )

        //when
        every { animalRepositoryMock.get(id) }.returns(expectedAnimalDTO)
        every { Calendar.getInstance() }.returns(actualCalendar)
        every { animalRepositoryMock.update(id, expectedAdoptedAnimalDTO) }.returns(expectedAdoptedAnimalDTO)

        val animalDTO=AnimalServiceImpl(animalRepositoryMock).adopt(id)

        //then
        assertEquals(expectedAdoptedAnimalDTO,animalDTO)
    }

    @Test(expected = AnimalAlreadyAdoptedException::class)
    fun `when adopt an animal that has AnimalStatus adopted should expect an AnimalAlreadyAdotpedException`(){
        //given
        val id=1
        val expectedAdoptedAnimalDTO=AnimalDTO(
            expectedAnimalDTO.name,
            expectedAnimalDTO.age,
            expectedAnimalDTO.timeUnit,
            expectedAnimalDTO.specie,
            expectedAnimalDTO.description,
            expectedAnimalDTO.creationDate,
            actualCalendar.time,
            AnimalStatus.ADOPTED
        )

        //when
        every { animalRepositoryMock.get(id) }.returns(expectedAdoptedAnimalDTO)
        every { Calendar.getInstance() }.returns(actualCalendar)

        AnimalServiceImpl(animalRepositoryMock).adopt(id)

        //then expect AnimalAlreadyAdoptedException
    }

    @Test(expected = AnimalDeadException::class)
    fun `when adopt an animal that has AnimalStatus dead should expect an AnimalDeadException`(){
        //given
        val id=1
        val expectedDeadAnimalDTO=AnimalDTO(
            expectedAnimalDTO.name,
            expectedAnimalDTO.age,
            expectedAnimalDTO.timeUnit,
            expectedAnimalDTO.specie,
            expectedAnimalDTO.description,
            expectedAnimalDTO.creationDate,
            actualCalendar.time,
            AnimalStatus.DEAD
        )

        //when
        every { animalRepositoryMock.get(id) }.returns(expectedDeadAnimalDTO)
        every { Calendar.getInstance() }.returns(actualCalendar)

        AnimalServiceImpl(animalRepositoryMock).adopt(id)

        //then expect AnimalDeadException
    }

    @Test(expected = AnimalGoneException::class)
    fun `when adopt an animal that has AnimalStatus adopted should expect an AnimalAlreadyAdotpedException and return the error with status UNAUTHORIZED 401`(){
        //given
        val id=1
        val expectedGoneAnimalDTO=AnimalDTO(
            expectedAnimalDTO.name,
            expectedAnimalDTO.age,
            expectedAnimalDTO.timeUnit,
            expectedAnimalDTO.specie,
            expectedAnimalDTO.description,
            expectedAnimalDTO.creationDate,
            actualCalendar.time,
            AnimalStatus.GONE
        )

        //when
        every { animalRepositoryMock.get(id) }.returns(expectedGoneAnimalDTO)
        every { Calendar.getInstance() }.returns(actualCalendar)

        AnimalServiceImpl(animalRepositoryMock).adopt(id)

        //then expect AnimalGoneException
    }
}
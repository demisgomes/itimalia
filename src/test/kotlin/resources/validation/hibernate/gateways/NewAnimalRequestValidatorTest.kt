package resources.validation.hibernate.gateways

import com.abrigo.itimalia.domain.entities.animal.AnimalSex
import com.abrigo.itimalia.domain.entities.animal.AnimalSize
import com.abrigo.itimalia.domain.entities.animal.Specie
import com.abrigo.itimalia.domain.entities.animal.TimeUnit
import com.abrigo.itimalia.resources.validation.hibernate.entities.NewAnimalRequestModel
import org.junit.Test
import javax.validation.Validation
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class NewAnimalRequestValidatorTest {
    private val validator = Validation.buildDefaultValidatorFactory().validator

    private val validNewAnimalRequest = NewAnimalRequestModel(
        "Animal",
        3,
        TimeUnit.YEAR,
        Specie.DOG,
        "An animal that needs attention",
        emptyList(),
        AnimalSex.FEMALE,
        AnimalSize.MEDIUM,
        true
    )

    @Test
    fun `given a valid new animal request, when try to validate it, then should pass`(){
        val constraints = validator.validate(validNewAnimalRequest)
        assert(constraints.isEmpty())
    }

    @Test
    fun `given a new animal request with null name, when try to validate it, then should have a constraint with message 'please fill with a name'`(){
        val emptyName = validNewAnimalRequest.copy(name = null)
        val constraints = validator.validate(emptyName)
        assertEquals(1, constraints.size)
        assertEquals("please fill with a name", constraints.first().message)
    }

    @Test
    fun `given a new animal request with null specie, when try to validate it, then should have a constraint with message 'please fill with a valid specie cat or dog'`(){
        val emptyName = validNewAnimalRequest.copy(specie = null)
        val constraints = validator.validate(emptyName)
        assertEquals(1, constraints.size)
        assertEquals("please fill with a valid specie: cat or dog", constraints.first().message)
    }

    @Test
    fun `given a new animal request with null description, when try to validate it, then should have a single constraint with message 'please fill with a description'`(){
        val emptyName = validNewAnimalRequest.copy(description = null)
        val constraints = validator.validate(emptyName)
        assertEquals(1, constraints.size)
        assertEquals("please fill with a description", constraints.first().message)
    }

    @Test
    fun `given a new animal request with null sex, when try to validate it, then should have a single constraint with message 'please fill with a sex male or female'`(){
        val emptyName = validNewAnimalRequest.copy(sex = null)
        val constraints = validator.validate(emptyName)
        assertEquals(1, constraints.size)
        assertEquals("please fill with a sex: male or female", constraints.first().message)
    }

    @Test
    fun `given a new animal request with null size, when try to validate it, then should have a single constraint with message '"please fill with a size tiny, small, medium, or large"'`(){
        val emptyName = validNewAnimalRequest.copy(size = null)
        val constraints = validator.validate(emptyName)
        assertEquals(1, constraints.size)
        assertEquals("please fill with a size: tiny, small, medium, or large", constraints.first().message)
    }

    @Test
    fun `given a new animal request with null castrated, when try to validate it, then should have a single constraint with message 'please fill castrated with true or false'`(){
        val emptyName = validNewAnimalRequest.copy(castrated = null)
        val constraints = validator.validate(emptyName)
        assertEquals(1, constraints.size)
        assertEquals("please fill castrated with true or false", constraints.first().message)
    }

    @Test
    fun `given a new animal request with null name and specie, when try to validate it, then should have two constraints with message 'please fill with a name', 'please fill with a valid specie cat or dog'`(){
        val emptyNameAndSpecie = validNewAnimalRequest.copy(name = null, specie = null)
        val constraints = validator.validate(emptyNameAndSpecie)
        val constraintMessages = mutableListOf<String>()

        constraints.forEach { constraint ->
            constraintMessages.add(constraint.message)
        }

        assertEquals(2, constraints.size)
        assertTrue(constraintMessages.contains("please fill with a name"))
        assertTrue(constraintMessages.contains("please fill with a valid specie: cat or dog"))
    }

    @Test
    fun `given a new animal request with null name, specie, and description when try to validate it, then should have three constraints with message 'please fill with a name', 'please fill with a valid specie cat or dog', 'please fill with a description'`(){
        val emptyNameSpecieAndDescription = validNewAnimalRequest.copy(name = null, specie = null, description = null)
        val constraints = validator.validate(emptyNameSpecieAndDescription)
        val constraintMessages = mutableListOf<String>()

        constraints.forEach { constraint ->
            constraintMessages.add(constraint.message)
        }

        assertEquals(3, constraints.size)
        assertTrue(constraintMessages.contains("please fill with a name"))
        assertTrue(constraintMessages.contains("please fill with a valid specie: cat or dog"))
        assertTrue(constraintMessages.contains("please fill with a description"))
    }

}
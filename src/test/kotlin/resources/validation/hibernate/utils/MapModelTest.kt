package resources.validation.hibernate.utils

import com.abrigo.itimalia.domain.exceptions.ValidationException
import com.abrigo.itimalia.factories.AnimalFactory
import com.abrigo.itimalia.factories.UserFactory
import com.abrigo.itimalia.resources.validation.hibernate.entities.AnimalRequestModel
import com.abrigo.itimalia.resources.validation.hibernate.entities.NewAnimalRequestModel
import com.abrigo.itimalia.resources.validation.hibernate.entities.NewUserRequestModel
import com.abrigo.itimalia.resources.validation.hibernate.entities.UserLoginRequestModel
import com.abrigo.itimalia.resources.validation.hibernate.entities.UserRequestModel
import com.abrigo.itimalia.resources.validation.hibernate.utils.MapModel
import org.junit.Test
import kotlin.test.assertTrue

class MapModelTest {

    @Test
    fun `when getModel is called with an instance of AnimalRequest, call AnimalRequestModel`() {
        val animalRequest = AnimalFactory.sampleRequest()
        val animalRequestModel = MapModel.getModel(animalRequest)

        assertTrue { animalRequestModel is AnimalRequestModel }
    }

    @Test
    fun `when getModel is called with an instance of NewAnimalRequest, call NewAnimalRequestModel`() {
        val newAnimalRequest = AnimalFactory.sampleNewRequest()
        val newAnimalRequestModel = MapModel.getModel(newAnimalRequest)

        assertTrue { newAnimalRequestModel is NewAnimalRequestModel }
    }

    @Test
    fun `when getModel is called with an instance of NewUserRequest, call NewUserRequestModel`() {
        val newUserRequest = UserFactory.sampleNewRequest()
        val newUserRequestModel = MapModel.getModel(newUserRequest)

        assertTrue { newUserRequestModel is NewUserRequestModel }
    }

    @Test
    fun `when getModel is called with an instance of UserLoginRequest, call UserLoginRequestModel`() {
        val userLoginRequest = UserFactory.sampleLoginRequest()
        val userLoginRequestModel = MapModel.getModel(userLoginRequest)

        assertTrue { userLoginRequestModel is UserLoginRequestModel }
    }

    @Test
    fun `when getModel is called with an instance of UserRequest, call UserRequestModel`() {
        val userRequest = UserFactory.sampleDTORequest()
        val userRequestModel = MapModel.getModel(userRequest)

        assertTrue { userRequestModel is UserRequestModel }
    }

    @Test(expected = ValidationException::class)
    fun `when getModel is called with an unmapped instance, expect IllegalArgumentException`() {
        val userRequest = Object()
        MapModel.getModel(userRequest)
    }
}

package com.abrigo.itimalia.domain.validation

import com.abrigo.itimalia.domain.entities.Gender
import com.abrigo.itimalia.domain.entities.user.NewUserRequest
import org.joda.time.DateTime
import org.junit.Test
import javax.validation.Validation
import kotlin.test.assertEquals

class NewUserRequestValidatorTest{
    private val validator = Validation.buildDefaultValidatorFactory().validator!!

    @Test
    fun `given an user with null email, should return one constraint`(){
        val newUserRequest = NewUserRequest(null, "password", DateTime.now(), Gender.FEMALE, "name", "81")
        val constraints = validator.validate(newUserRequest)
        assertEquals(1,constraints.size)
    }

    @Test
    fun `given an user with null name, should return one constraint`(){
        val newUserRequest = NewUserRequest("email@email.com", "password", DateTime.now(), Gender.FEMALE, null, "81")
        val constraints = validator.validate(newUserRequest)
        assertEquals(1,constraints.size)
    }
}
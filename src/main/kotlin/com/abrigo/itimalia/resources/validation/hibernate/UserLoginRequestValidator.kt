package com.abrigo.itimalia.resources.validation.hibernate

import com.abrigo.itimalia.domain.entities.user.UserLoginRequest
import com.abrigo.itimalia.domain.exceptions.ValidationException
import com.abrigo.itimalia.domain.validation.Validator
import com.abrigo.itimalia.resources.validation.hibernate.entities.UserLoginRequestModel

data class UserLoginRequestValidator(private val validation: javax.validation.Validator): Validator<UserLoginRequest> {
    override fun validate(t: UserLoginRequest) {
        val userLoginRequestModel = UserLoginRequestModel(t.email, t.password)
        val constraints = validation.validate(userLoginRequestModel)
        if(constraints.size>0){
            val errors = hashMapOf<String, MutableList<String>>()
            constraints.forEach { violation ->
                val key = "${violation.propertyPath}: ${violation.invalidValue}"
                if(errors.containsKey(key)){
                    errors[key]?.add(violation.message)
                }
                else{
                    errors[key] = mutableListOf(violation.message)
                }
            }
            throw ValidationException(errors)
        }
    }
}
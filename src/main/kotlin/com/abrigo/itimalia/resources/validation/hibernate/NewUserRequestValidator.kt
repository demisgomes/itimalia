package com.abrigo.itimalia.resources.validation.hibernate

import com.abrigo.itimalia.domain.entities.user.NewUserRequest
import com.abrigo.itimalia.domain.exceptions.ValidationException
import com.abrigo.itimalia.domain.validation.Validator
import com.abrigo.itimalia.resources.validation.hibernate.entities.NewUserRequestModel

class NewUserRequestValidator(private val validation: javax.validation.Validator):
    Validator<NewUserRequest> {
    override fun validate(t: NewUserRequest) {
        val newRequestModel = NewUserRequestModel(t.email, t.password, t.birthDate, t.gender, t.name, t.phone)
        val constraints = validation.validate(newRequestModel)
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
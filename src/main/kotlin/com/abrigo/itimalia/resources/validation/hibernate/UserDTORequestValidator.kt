package com.abrigo.itimalia.resources.validation.hibernate

import com.abrigo.itimalia.domain.entities.user.UserDTORequest
import com.abrigo.itimalia.domain.exceptions.ValidationException
import com.abrigo.itimalia.domain.validation.Validator
import com.abrigo.itimalia.resources.validation.hibernate.entities.UserDTORequestModel

class UserDTORequestValidator(private val validation: javax.validation.Validator):
Validator<UserDTORequest>{
    override fun validate(t: UserDTORequest) {
        val userDTORequestModel = UserDTORequestModel(t.id, t.email, t.password, t.birthDate, t.gender, t.name, t.phone, t.role, t.creationDate, t.modificationDate, t.token)
        val constraints = validation.validate(userDTORequestModel)
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
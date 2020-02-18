package com.abrigo.itimalia.resources.validation.hibernate

import com.abrigo.itimalia.domain.entities.animal.NewAnimalRequest
import com.abrigo.itimalia.domain.exceptions.ValidationException
import com.abrigo.itimalia.resources.validation.hibernate.entities.NewAnimalRequestModel

class NewAnimalRequestValidator(val validation: javax.validation.Validator): com.abrigo.itimalia.domain.validation.Validator<NewAnimalRequest>{
    override fun validate(t: NewAnimalRequest) {
        val newAnimalRequestModel =  NewAnimalRequestModel(t.name, t.age, t.timeUnit, t.specie, t.description)
        val constraints = validation.validate(newAnimalRequestModel)
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
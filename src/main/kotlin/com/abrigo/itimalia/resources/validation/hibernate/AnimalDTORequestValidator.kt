package com.abrigo.itimalia.resources.validation.hibernate

import com.abrigo.itimalia.domain.entities.animal.AnimalDTORequest
import com.abrigo.itimalia.domain.exceptions.ValidationException
import com.abrigo.itimalia.domain.validation.Validator
import com.abrigo.itimalia.resources.validation.hibernate.entities.AnimalDTORequestModel

class AnimalDTORequestValidator(val validation: javax.validation.Validator):Validator<AnimalDTORequest>{
    override fun validate(t: AnimalDTORequest) {
        val animalDTORequestModel = AnimalDTORequestModel(t.id, t.name, t.age, t.timeUnit, t.specie, t.description, t.creationDate, t.modificationDate, t.status)
        val constraints = validation.validate(animalDTORequestModel)
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
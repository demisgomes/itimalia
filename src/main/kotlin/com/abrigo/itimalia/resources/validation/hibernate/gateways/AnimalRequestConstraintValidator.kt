package com.abrigo.itimalia.resources.validation.hibernate.gateways

import com.abrigo.itimalia.domain.entities.animal.AnimalRequest
import com.abrigo.itimalia.domain.validation.ConstraintValidator
import com.abrigo.itimalia.resources.validation.hibernate.entities.AnimalRequestModel
import com.abrigo.itimalia.resources.validation.hibernate.utils.MapMounter
import javax.validation.Validator

class AnimalRequestConstraintValidator(private val javaxValidator: Validator, private val mapMounter: MapMounter) :
    ConstraintValidator<AnimalRequest> {
    override fun getConstraints(t: AnimalRequest): HashMap<String, MutableList<String>> {
        val animalDTORequestModel = AnimalRequestModel(
            t.id,
            t.name,
            t.age,
            t.timeUnit,
            t.specie,
            t.description,
            t.creationDate,
            t.modificationDate,
            t.status,
            t.deficiencies,
            t.sex,
            t.size,
            t.castrated,
            t.createdById
        )
        val constraints = javaxValidator.validate(animalDTORequestModel)
        return mapMounter.mount(constraints)
    }
}

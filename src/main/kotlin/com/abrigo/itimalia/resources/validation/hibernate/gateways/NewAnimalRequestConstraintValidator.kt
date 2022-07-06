package com.abrigo.itimalia.resources.validation.hibernate.gateways

import com.abrigo.itimalia.domain.entities.animal.NewAnimalRequest
import com.abrigo.itimalia.domain.validation.ConstraintValidator
import com.abrigo.itimalia.resources.validation.hibernate.entities.NewAnimalRequestModel
import com.abrigo.itimalia.resources.validation.hibernate.utils.MapMounter
import javax.validation.Validator

class NewAnimalRequestConstraintValidator(private val javaxValidator: Validator, private val mapMounter: MapMounter) :
    ConstraintValidator<NewAnimalRequest> {
    override fun getConstraints(t: NewAnimalRequest): HashMap<String, MutableList<String>> {
        val newAnimalRequestModel = NewAnimalRequestModel(
            t.name,
            t.age,
            t.timeUnit,
            t.specie,
            t.description,
            t.deficiencies,
            t.sex,
            t.size,
            t.castrated
        )
        val constraints = javaxValidator.validate(newAnimalRequestModel)
        return mapMounter.mount(constraints)
    }
}

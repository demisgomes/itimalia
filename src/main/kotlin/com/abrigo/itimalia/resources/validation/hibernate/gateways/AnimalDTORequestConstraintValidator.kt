package com.abrigo.itimalia.resources.validation.hibernate.gateways
import com.abrigo.itimalia.domain.entities.animal.AnimalDTORequest
import com.abrigo.itimalia.domain.validation.ConstraintValidator
import com.abrigo.itimalia.resources.validation.hibernate.entities.AnimalDTORequestModel
import com.abrigo.itimalia.resources.validation.hibernate.utils.MapMounter
import javax.validation.Validator

class AnimalDTORequestConstraintValidator(private val javaxValidator: Validator, private val mapMounter: MapMounter): ConstraintValidator<AnimalDTORequest> {
    override fun getConstraints(t: AnimalDTORequest): HashMap<String, MutableList<String>> {
        val animalDTORequestModel = AnimalDTORequestModel(t.id, t.name, t.age, t.timeUnit, t.specie, t.description, t.creationDate, t.modificationDate, t.status)
        val constraints = javaxValidator.validate(animalDTORequestModel)
        return mapMounter.mount(constraints)
    }
}
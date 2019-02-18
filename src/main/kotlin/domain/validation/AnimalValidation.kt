package domain.validation

import domain.entities.AnimalDTO
import domain.exceptions.ValidationException

class AnimalValidation{
    fun validate(animalDTO: AnimalDTO){
        val validations = mutableListOf<Validation<*>>()

        validations.add(Validation("name",animalDTO.name).validName())

        val errorsMap = hashMapOf<String, MutableList<String>>()

        //filter by a validation entry with error list greater than zero
        validations.filter {
            it.errorMessageList.isNotEmpty()
        }.forEach {
            errorsMap[it.fieldName] = it.errorMessageList
        }
        if (errorsMap.size > 0) throw ValidationException(errorsMap)
    }
}

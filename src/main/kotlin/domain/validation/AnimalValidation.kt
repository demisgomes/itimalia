package domain.validation

import domain.entities.AnimalDTO
import domain.entities.TimeUnit
import domain.exceptions.ValidationException
import java.util.*

class AnimalValidation{
    fun validate(animalDTO: AnimalDTO):AnimalDTO{
        var validatedAnimalDTO=animalDTO
        val validations = mutableListOf<Validation<*>>()

        validations.add(Validation("name",animalDTO.name).validName())
        validations.add(Validation("specie",animalDTO.specie).validSpecie())

        val errorsMap = hashMapOf<String, MutableList<String>>()

        if(animalDTO.age==null && animalDTO.timeUnit!=null){
            validatedAnimalDTO= AnimalDTO(
                animalDTO.name,
                null,
                null,
                animalDTO.specie,
                animalDTO.description,
                animalDTO.creationDate,
                animalDTO.modificationDate,
                animalDTO.status
            )
        }

        else if (animalDTO.age!=null && animalDTO.timeUnit==null){
            validatedAnimalDTO= AnimalDTO(
                animalDTO.name,
                animalDTO.age,
                TimeUnit.YEAR,
                animalDTO.specie,
                animalDTO.description,
                animalDTO.creationDate,
                animalDTO.modificationDate,
                animalDTO.status
            )
        }
        //filter by a validation entry with error list greater than zero
        validations.filter {
            it.errorMessageList.isNotEmpty()
        }.forEach {
            errorsMap[it.fieldName] = it.errorMessageList
        }
        if (errorsMap.size > 0) throw ValidationException(errorsMap)
        return validatedAnimalDTO
    }
}

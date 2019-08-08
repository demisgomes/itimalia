package domain.validation
import domain.entities.user.UserDTO
import domain.exceptions.ValidationException

class UserValidation{

    fun validate(userDTO: UserDTO){
        val validations = mutableListOf<Validation<*>>()

        validations.add(Validation("email",userDTO.email).validEmail())
        validations.add(Validation("name",userDTO.name).validName())
        validations.add(Validation("birthDate",userDTO.birthDate).validBirthDate())
        validations.add(Validation("gender",userDTO.gender).validGender())

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
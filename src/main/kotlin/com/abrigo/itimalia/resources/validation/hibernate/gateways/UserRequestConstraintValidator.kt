package com.abrigo.itimalia.resources.validation.hibernate.gateways
import com.abrigo.itimalia.domain.entities.user.UserRequest
import com.abrigo.itimalia.domain.validation.ConstraintValidator
import com.abrigo.itimalia.resources.validation.hibernate.entities.UserRequestModel
import com.abrigo.itimalia.resources.validation.hibernate.utils.MapMounter
import javax.validation.Validator

class UserRequestConstraintValidator(private val javaxValidator: Validator, private val mapMounter: MapMounter): ConstraintValidator<UserRequest> {
    override fun getConstraints(t: UserRequest): HashMap<String, MutableList<String>> {
        val userDTORequestModel = UserRequestModel(t.id, t.email, t.password, t.birthDate, t.gender, t.name, t.phone, t.role, t.creationDate, t.modificationDate, t.token)
        val constraints = javaxValidator.validate(userDTORequestModel)
        return mapMounter.mount(constraints)
    }
}
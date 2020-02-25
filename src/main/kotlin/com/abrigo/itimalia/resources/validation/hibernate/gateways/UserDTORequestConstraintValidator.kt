package com.abrigo.itimalia.resources.validation.hibernate.gateways
import com.abrigo.itimalia.domain.entities.user.UserDTORequest
import com.abrigo.itimalia.domain.validation.ConstraintValidator
import com.abrigo.itimalia.resources.validation.hibernate.entities.UserDTORequestModel
import com.abrigo.itimalia.resources.validation.hibernate.utils.MapMounter
import javax.validation.Validator

class UserDTORequestConstraintValidator(private val javaxValidator: Validator, private val mapMounter: MapMounter): ConstraintValidator<UserDTORequest> {
    override fun getConstraints(t: UserDTORequest): HashMap<String, MutableList<String>> {
        val userDTORequestModel = UserDTORequestModel(t.id, t.email, t.password, t.birthDate, t.gender, t.name, t.phone, t.role, t.creationDate, t.modificationDate, t.token)
        val constraints = javaxValidator.validate(userDTORequestModel)
        return mapMounter.mount(constraints)
    }
}
package com.abrigo.itimalia.resources.validation.hibernate.gateways
import com.abrigo.itimalia.domain.entities.user.NewUserRequest
import com.abrigo.itimalia.domain.validation.ConstraintValidator
import com.abrigo.itimalia.resources.validation.hibernate.entities.NewUserRequestModel
import com.abrigo.itimalia.resources.validation.hibernate.utils.MapMounter
import javax.validation.Validator

class NewUserRequestConstraintValidator(private val javaxValidator: Validator, private val mapMounter: MapMounter): ConstraintValidator<NewUserRequest> {
    override fun getConstraints(t: NewUserRequest): HashMap<String, MutableList<String>> {
        val newRequestModel = NewUserRequestModel(t.email, t.password, t.birthDate, t.gender, t.name, t.phone)
        val constraints = javaxValidator.validate(newRequestModel)
        return mapMounter.mount(constraints)
    }
}
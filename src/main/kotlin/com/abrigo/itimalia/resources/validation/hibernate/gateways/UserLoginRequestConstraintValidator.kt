package com.abrigo.itimalia.resources.validation.hibernate.gateways
import com.abrigo.itimalia.domain.entities.user.UserLoginRequest
import com.abrigo.itimalia.domain.validation.ConstraintValidator
import com.abrigo.itimalia.resources.validation.hibernate.entities.UserLoginRequestModel
import com.abrigo.itimalia.resources.validation.hibernate.utils.MapMounter
import javax.validation.Validator

class UserLoginRequestConstraintValidator(private val javaxValidator: Validator, private val mapMounter: MapMounter) : ConstraintValidator<UserLoginRequest> {
    override fun getConstraints(t: UserLoginRequest): HashMap<String, MutableList<String>> {
        val userLoginRequestModel = UserLoginRequestModel(t.email, t.password)
        val constraints = javaxValidator.validate(userLoginRequestModel)
        return mapMounter.mount(constraints)
    }
}

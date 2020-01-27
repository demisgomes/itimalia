package com.abrigo.itimalia.domain.validation

import com.abrigo.itimalia.domain.entities.user.UserLogin
import com.abrigo.itimalia.domain.exceptions.ValidationException

class UserLoginValidator{
    fun validate(userLogin: UserLogin){
        val validations = mutableListOf<Validation<*>>()

        validations.add(Validation("email",userLogin.email).validEmail())

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
package com.abrigo.itimalia.resources.validation.hibernate.constraints

import com.abrigo.itimalia.resources.validation.hibernate.constraints.validators.MinimumAgeValidator
import javax.validation.Constraint
import javax.validation.Payload
import kotlin.reflect.KClass

@Target(AnnotationTarget.FIELD, AnnotationTarget.PROPERTY, AnnotationTarget.FUNCTION, AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
@Constraint(validatedBy = [MinimumAgeValidator::class])
@MustBeDocumented
annotation class MinimumAge(val message: String = "The age is under the minimum", val groups: Array<KClass<Any>> = [], val payload: Array<KClass<Payload>> = [])

package com.abrigo.itimalia.resources.validation.hibernate.constraints.validators

import com.abrigo.itimalia.resources.validation.hibernate.constraints.MinimumAge
import org.joda.time.DateTime
import org.joda.time.LocalDate
import org.joda.time.LocalTime
import javax.validation.ConstraintValidator
import javax.validation.ConstraintValidatorContext

private const val MINIMUM_AGE = 13
internal class MinimumAgeValidator : ConstraintValidator<MinimumAge, DateTime> {
    override fun isValid(value: DateTime?, context: ConstraintValidatorContext?): Boolean {
        val lastValidBirthDate = LocalDate.now().minusYears(MINIMUM_AGE).toDateTime(LocalTime())
        if (value == null) {
            return false
        }
        return (value.isBefore(lastValidBirthDate))
    }
}

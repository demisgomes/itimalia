package domain.entities

import com.fasterxml.jackson.annotation.JsonIgnore
import org.joda.time.DateTime
import java.util.*

data class NewUser(
    val email: String,
    val password:String,
    val birthDate: DateTime?,
    val gender:Gender?,
    val name: String,
    val phone: String)
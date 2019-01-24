package domain.validation

import domain.entities.Gender
import java.util.*

class Validation<T>(val fieldName: String, val fieldValue: T,
                              val errorMessageList: MutableList<String> = mutableListOf())

fun Validation<Gender?>.validGender():Validation<Gender?>{
    if(this.fieldValue==null){
        this.errorMessageList.add("invalid gender")
    }
    return this
}

@Suppress("INTEGER_OVERFLOW")
fun Validation<Date?>.validBirthDate():Validation<Date?>{
    //Only more than 13 years old
    val minTimeInMillis:Long =1000*3600*8760*13

    if(this.fieldValue==null){
        this.errorMessageList.add("Invalid Date")
    }
    else{
        if(Calendar.getInstance().timeInMillis-this.fieldValue.time<minTimeInMillis){
            this.errorMessageList.add("Only accept users with 13 years old or more")
        }
    }
    return this
}
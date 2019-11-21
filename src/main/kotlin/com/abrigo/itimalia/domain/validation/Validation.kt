package com.abrigo.itimalia.domain.validation

import com.abrigo.itimalia.domain.entities.Gender
import com.abrigo.itimalia.domain.entities.Specie
import org.joda.time.DateTime

class Validation<T>(val fieldName: String, val fieldValue: T,
                              val errorMessageList: MutableList<String> = mutableListOf())

val emailRegex = """^[a-zA-Z0-9_+&*-]+(?:\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\.)+[a-zA-Z]{2,7}$""".toRegex()
val nameRegex = """[a-zA-Z\u00C0-\u00FF\s.-]+""".toRegex()


fun Validation<Gender?>.validGender():Validation<Gender?>{
    if(this.fieldValue==null){
        this.errorMessageList.add("invalid gender")
    }
    return this
}

@Suppress("INTEGER_OVERFLOW")
fun Validation<DateTime?>.validBirthDate():Validation<DateTime?>{
    //Only more than 13 years old
    val minTimeInMillis:Long =1000*3600*8760*13

    if(this.fieldValue==null){
        this.errorMessageList.add("invalid birthDate")
    }
    else{
        if(DateTime.now().millis-this.fieldValue.millis<minTimeInMillis){
            this.errorMessageList.add("Only accept users with 13 years old or more")
        }
    }
    return this
}

fun Validation<String>.validEmail():Validation<String>{
    if(!emailRegex.matches(fieldValue)){
        this.errorMessageList.add("invalid email")
    }
    return this
}

fun Validation<String>.validName():Validation<String>{
    if(!nameRegex.matches(fieldValue)){
        this.errorMessageList.add("Invalid name. The name cannot be blank or contain numbers")
    }
    return this
}

fun Validation<Specie?>.validSpecie():Validation<Specie?>{
    if(this.fieldValue==null){
        this.errorMessageList.add("Invalid specie. The specie must be cat or dog")
    }
    return this
}
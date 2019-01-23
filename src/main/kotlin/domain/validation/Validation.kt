package domain.validation

import domain.entities.Gender

class Validation<T>(val fieldName: String, val fieldValue: T,
                              val errorMessageList: MutableList<String> = mutableListOf())

fun Validation<Gender?>.validGender():Validation<Gender?>{
    if(this.fieldValue==null){
        this.errorMessageList.add("invalid gender")
    }
    return this
}
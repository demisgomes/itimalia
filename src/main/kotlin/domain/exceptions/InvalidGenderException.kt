package domain.exceptions

import org.eclipse.jetty.http.HttpStatus

class InvalidGenderException:ApiException{
    constructor(message:String) :super(message)

    override fun httpStatus(): Int {
        return HttpStatus.UNPROCESSABLE_ENTITY_422
    }

    override fun apiError(): ApiError {
        return ApiError.INVALID_GENDER_ERROR
    }

    override fun userResponseMessage(): String {
        return "Invalid gender. Choose male, female, or undefined"
    }

}
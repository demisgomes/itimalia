package domain.exceptions

import org.eclipse.jetty.http.HttpStatus

class UnmodifiedUserException:ApiException(message = "The user has the same fields, i.e., it was not modified"){
    override fun httpStatus(): Int {
        return HttpStatus.NOT_ACCEPTABLE_406
    }

    override fun apiError(): ApiError {
        return ApiError.UNMODIFIED_USER_ERROR
    }

    override fun userResponseMessage(): String {
        return message!!
    }

}
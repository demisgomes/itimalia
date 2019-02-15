package domain.exceptions

import org.eclipse.jetty.http.HttpStatus

class InvalidNameException:ApiException("You must provide a valid name"){

    override fun httpStatus(): Int {
        return HttpStatus.BAD_REQUEST_400
    }

    override fun apiError(): ApiError {
        return ApiError.INVALID_NAME_ERROR
    }

    override fun userResponseMessage(): String {
        return message!!
    }
}
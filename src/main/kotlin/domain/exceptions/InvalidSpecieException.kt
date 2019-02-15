package domain.exceptions

import org.eclipse.jetty.http.HttpStatus

class InvalidSpecieException:ApiException("Invalid specie. Choose cat or dog"){

    override fun httpStatus(): Int {
        return HttpStatus.BAD_REQUEST_400
    }

    override fun apiError(): ApiError {
        return ApiError.INVALID_SPECIE_ERROR
    }

    override fun userResponseMessage(): String {
        return message!!
    }

}

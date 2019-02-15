package domain.exceptions

import org.eclipse.jetty.http.HttpStatus

class AnimalNotFoundException :ApiException("Animal not found"){

    override fun httpStatus(): Int {
        return HttpStatus.NOT_FOUND_404
    }

    override fun apiError(): ApiError {
        return ApiError.ANIMAL_NOT_FOUND_ERROR
    }

    override fun userResponseMessage(): String {
        return message!!
    }
}

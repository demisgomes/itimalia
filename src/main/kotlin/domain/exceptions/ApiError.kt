package domain.exceptions

enum class ApiError{
    INVALID_GENDER_ERROR,
    VALIDATION_ERROR,
    USER_NOT_FOUND_ERROR,
    UNMODIFIED_USER_ERROR,
    INVALID_CREDENTIALS_ERROR,
    UNAUTHORIZED_ADMIN_ROLE_ERROR,
    UNAUTHORIZED_USER_ROLE_ERROR,
    INVALID_TOKEN_ERROR,
    UNAUTHORIZED_ROLE_CHANGE_ERROR,
    UNAUTHORIZED_DIFFERENT_USER_CHANGE_ERROR,
    EMAIL_ALREADY_EXISTS_ERROR,
    ANIMAL_NOT_FOUND_ERROR
}
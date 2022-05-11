package ru.storozh.common

class AppException : Throwable {
    val error: String
    val errorHttp: Int

    constructor(_error: String) : super() {
        error = _error
        errorHttp = 0
    }

    constructor(_error: String, _errorHttp: Int) : super() {
        error = _error
        errorHttp = _errorHttp
    }

    constructor(_error: String, message: String) : super(message) {
        error = _error
        errorHttp = 0
    }

    constructor(_error: String, message: String, _errorHttp: Int) : super(message) {
        error = _error
        errorHttp = _errorHttp
    }

    constructor(_error: String, message: String, cause: Throwable, _errorHttp: Int) : super(
        message,
        cause
    ) {
        error = _error
        errorHttp = _errorHttp
    }

    constructor(_error: String, message: String, cause: Throwable) : super(message, cause) {
        error = _error
        errorHttp = 0
    }

    constructor(_error: String, cause: Throwable, _errorHttp: Int) : super(cause) {
        error = _error
        errorHttp = _errorHttp
    }

    constructor(_error: String, cause: Throwable) : super(cause) {
        error = _error
        errorHttp = 0
    }

}
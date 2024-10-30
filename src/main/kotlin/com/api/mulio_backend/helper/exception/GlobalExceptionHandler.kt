package com.api.mulio_backend.helper.exception

import com.api.mulio_backend.helper.response.ResponseObject
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice

@RestControllerAdvice
class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException::class)
    fun handleValidationExceptions(ex: MethodArgumentNotValidException): ResponseEntity<ResponseObject<Map<String, String>>> {
        val errors = ex.bindingResult.fieldErrors.associate { it.field to it.defaultMessage.orEmpty() }

        val response = ResponseObject(
            status = HttpStatus.BAD_REQUEST.value(),
            message = "Validation failed",
            data = errors
        )

        return ResponseEntity.badRequest().body(response)
    }
}

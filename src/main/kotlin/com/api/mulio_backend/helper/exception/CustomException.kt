package com.api.mulio_backend.helper.exception

import org.springframework.http.HttpStatus

class CustomException(message: String, val status: HttpStatus) : RuntimeException(message)

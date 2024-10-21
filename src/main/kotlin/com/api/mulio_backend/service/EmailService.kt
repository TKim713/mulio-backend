package com.api.mulio_backend.service

interface EmailService {
    fun sendEmail(to: String, subject: String, token: String, username: String)
}

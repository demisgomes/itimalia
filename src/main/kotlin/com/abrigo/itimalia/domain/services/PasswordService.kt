package com.abrigo.itimalia.domain.services

interface PasswordService {
    fun encode(password: String) : String
    fun verify(password: String, encodedPassword: String) : Boolean
}
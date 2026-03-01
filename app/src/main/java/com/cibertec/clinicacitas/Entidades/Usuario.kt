package com.cibertec.clinicacitas.Entidades

data class Usuario(
    val id: Int,
    val username: String,
    val password: String,
    val role: String,
    val email: String = "",
    val dni: String = "",
    val fullName: String = "",
)
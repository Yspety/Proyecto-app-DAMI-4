package com.cibertec.clinicacitas.Entidades

data class Doctor(
    val id: Int,
    val usuarioId: Int,
    val fullName: String,
    val specialtyId: Int,
    val cmp: String,
    val room: String
)

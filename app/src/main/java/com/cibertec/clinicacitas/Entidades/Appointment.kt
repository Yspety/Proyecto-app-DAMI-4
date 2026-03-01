package com.cibertec.clinicacitas.Entidades

data class Appointment(
    val id: Int = 0,
    val patientId: Int, // CAMBIO: Ahora es el ID del usuario
    val doctorId: Int,
    val date: String,
    val time: String,
    val reason: String,
    val status: String
)

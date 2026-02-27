package com.cibertec.clinicacitas.Entidades

data class Appointment(
    val id: Int,
    val patientName: String,
    val doctorId: Int,
    val date: String,   // yyyy-MM-dd
    val time: String,   // HH:mm
    val reason: String,
    val status: String  // Programada, Cancelada, Atendida
)

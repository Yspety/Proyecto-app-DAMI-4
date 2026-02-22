package com.cibertec.clinicacitas

data class Specialty(
    val id: Int,
    val name: String
)

data class Doctor(
    val id: Int,
    val fullName: String,
    val specialty: Specialty,
    val cmp: String,
    val room: String
)

data class Appointment(
    val id: Int,
    val patientName: String,
    val doctor: Doctor,
    val date: String,   // yyyy-MM-dd
    val time: String,   // HH:mm
    val reason: String,
    val status: String  // Programada, Cancelada, Atendida
)

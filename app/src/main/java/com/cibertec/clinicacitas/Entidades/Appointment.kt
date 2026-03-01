package com.cibertec.clinicacitas.Entidades

data class Appointment(
    val id: Int,
    val patientName: String,
    val doctorId: Int,
    val dateTime: Long, // Cambiado de date y time a un solo campo Long
    val reason: String,
    val status: String,
) {
    var doctorName: String? = null
    var specialtyName: String? = null
}

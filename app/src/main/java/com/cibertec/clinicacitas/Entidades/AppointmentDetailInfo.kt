package com.cibertec.clinicacitas.Entidades

/**
 * Clase de datos para mostrar el detalle completo de una cita en la UI.
 */
data class AppointmentDetailInfo(
    val appointmentId: Int,
    val patientName: String,
    val doctorName: String,
    val especialidadNombre: String,
    val date: String,
    val time: String,
    val reason: String,
    val status: String
)

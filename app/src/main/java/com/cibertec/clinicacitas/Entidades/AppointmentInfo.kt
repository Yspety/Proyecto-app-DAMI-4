package com.cibertec.clinicacitas.Entidades

/**
 * Clase de datos diseñada para mostrar información combinada de citas en la UI.
 */
data class AppointmentInfo(
    val appointmentId: Int,
    val date: String,
    val time: String,
    val status: String,
    val doctorName: String,
    val especialidadNombre: String
)

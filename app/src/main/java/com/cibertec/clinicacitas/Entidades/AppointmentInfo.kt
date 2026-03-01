package com.cibertec.clinicacitas.Entidades

/**
 * Clase unificada para mostrar información en la UI (Lista y Detalle).
 */
data class AppointmentInfo(
    val appointmentId: Int,
    val date: String,
    val time: String,
    val status: String,
    val doctorName: String,
    val especialidadNombre: String,
    val patientName: String = "", // Agregado: opcional para la lista, obligatorio para detalle
    val reason: String = ""       // Agregado: opcional para la lista, obligatorio para detalle
)
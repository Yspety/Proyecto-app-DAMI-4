package com.cibertec.clinicacitas.Entidades

/**
 * Clase de datos diseñada para mostrar información combinada de doctores en la UI.
 */
data class DoctorInfo(
    val doctorId: Int,
    val fullName: String,
    val especialidadNombre: String,
    val cmp: String,
    val room: String
)

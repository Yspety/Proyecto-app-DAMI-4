package com.cibertec.clinicacitas

import com.cibertec.clinicacitas.Entidades.Appointment
import com.cibertec.clinicacitas.Entidades.Doctor
import com.cibertec.clinicacitas.Entidades.Especialidad
import java.util.Calendar

object FakeData {

    private val s1 = Especialidad(1, "Medicina General")
    private val s2 = Especialidad(2, "Pediatría")
    private val s3 = Especialidad(3, "Cardiología")
    private val s4 = Especialidad(4, "Dermatología")

    val specialties: List<Especialidad> = listOf(s1, s2, s3, s4)

    // CORREGIDO: Añadido un usuarioId de relleno (0) para que coincida con el nuevo constructor de Doctor
    val doctors: List<Doctor> = listOf(
        Doctor(1, 0, "Dra. Ana Rodríguez", s2.id, "CMP 12345", "Consultorio 201"),
        Doctor(2, 0, "Dr. Luis García", s1.id, "CMP 23456", "Consultorio 105"),
        Doctor(3, 0, "Dra. María Torres", s4.id, "CMP 34567", "Consultorio 310"),
        Doctor(4, 0, "Dr. Carlos Vega", s3.id, "CMP 45678", "Consultorio 402")
    )

    val appointments: MutableList<Appointment> = mutableListOf(
        Appointment(
            id = 1001,
            patientName = "Juan Pérez",
            doctorId = doctors[1].id,
            dateTime = Calendar.getInstance().apply { set(2026, 1, 5, 10, 30) }.timeInMillis,
            reason = "Control general",
            status = "Programada"
        ),
        Appointment(
            id = 1002,
            patientName = "María Flores",
            doctorId = doctors[0].id,
            dateTime = Calendar.getInstance().apply { set(2026, 1, 6, 9, 0) }.timeInMillis,
            reason = "Consulta pediátrica",
            status = "Programada"
        )
    )

    fun appointmentsForPatient(patientName: String): List<Appointment> =
        appointments.filter { it.patientName.equals(patientName, ignoreCase = true) }
}

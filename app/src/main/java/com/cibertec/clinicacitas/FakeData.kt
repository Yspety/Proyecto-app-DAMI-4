package com.cibertec.clinicacitas

/**

 */
object FakeData {

    private val s1 = Specialty(1, "Medicina General")
    private val s2 = Specialty(2, "Pediatría")
    private val s3 = Specialty(3, "Cardiología")
    private val s4 = Specialty(4, "Dermatología")

    val specialties: List<Specialty> = listOf(s1, s2, s3, s4)

    val doctors: List<Doctor> = listOf(
        Doctor(1, "Dra. Ana Rodríguez", s2, "CMP 12345", "Consultorio 201"),
        Doctor(2, "Dr. Luis García", s1, "CMP 23456", "Consultorio 105"),
        Doctor(3, "Dra. María Torres", s4, "CMP 34567", "Consultorio 310"),
        Doctor(4, "Dr. Carlos Vega", s3, "CMP 45678", "Consultorio 402")
    )

    // Citas en memoria (sirve para listas y detalle)
    val appointments: MutableList<Appointment> = mutableListOf(
        Appointment(
            id = 1001,
            patientName = "Juan Pérez",
            doctor = doctors[1],
            date = "2026-02-05",
            time = "10:30",
            reason = "Control general",
            status = "Programada"
        ),
        Appointment(
            id = 1002,
            patientName = "María Flores",
            doctor = doctors[0],
            date = "2026-02-06",
            time = "09:00",
            reason = "Consulta pediátrica",
            status = "Programada"
        ),
        Appointment(
            id = 1003,
            patientName = "Juan Pérez",
            doctor = doctors[3],
            date = "2026-02-10",
            time = "11:15",
            reason = "Chequeo cardiológico",
            status = "Programada"
        )
    )

    fun appointmentsForPatient(patientName: String): List<Appointment> =
        appointments.filter { it.patientName.equals(patientName, ignoreCase = true) }
}

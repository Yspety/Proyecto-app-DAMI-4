package com.cibertec.clinicacitas

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.cibertec.clinicacitas.DAO.AppointmentDAO
import com.cibertec.clinicacitas.DAO.DoctorDAO
import com.cibertec.clinicacitas.Entidades.Appointment
import com.cibertec.clinicacitas.databinding.ActivityReservarCitaBinding

class ReservarCitaActivity : AppCompatActivity() {

    private lateinit var binding: ActivityReservarCitaBinding
    private lateinit var doctorDAO: DoctorDAO
    private lateinit var appointmentDAO: AppointmentDAO

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityReservarCitaBinding.inflate(layoutInflater)
        setContentView(binding.root)

        doctorDAO = DoctorDAO(this)
        appointmentDAO = AppointmentDAO(this)

        binding.toolbar.title = "Reservar cita"
        binding.toolbar.setNavigationOnClickListener { finish() }

        val doctors = doctorDAO.getAllDoctorInfo()
        val doctorNames = doctors.map { "${it.fullName} - ${it.especialidadNombre}" } // CORREGIDO

        val spinnerAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, doctorNames)
        binding.spDoctor.adapter = spinnerAdapter

        val doctorId = intent.getIntExtra(EXTRA_DOCTOR_ID, -1)
        val preIndex = doctors.indexOfFirst { it.doctorId == doctorId }
        if (preIndex >= 0) binding.spDoctor.setSelection(preIndex)

        binding.btnGuardar.setOnClickListener {
            // Asumiendo que hay al menos un usuario logueado
            val patient = SessionStore.currentUser?.username ?: "Paciente Anónimo"
            val selectedDoctorInfo = doctors[binding.spDoctor.selectedItemPosition]

            val date = binding.etDate.text.toString().trim()
            val time = binding.etTime.text.toString().trim()
            val reason = binding.etReason.text.toString().trim()

            if (date.isEmpty() || time.isEmpty() || reason.isEmpty()) {
                Toast.makeText(this, "Completa fecha, hora y motivo.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val newAppointment = Appointment(
                id = 0, // El ID es autoincrementado
                patientName = patient,
                doctorId = selectedDoctorInfo.doctorId,
                date = date,
                time = time,
                reason = reason,
                status = "Programada"
            )

            val result = appointmentDAO.addAppointment(newAppointment)

            if (result > -1) {
                Toast.makeText(this, "Cita registrada con éxito.", Toast.LENGTH_SHORT).show()
                finish()
            } else {
                Toast.makeText(this, "Error al registrar la cita.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    companion object {
        const val EXTRA_DOCTOR_ID = "extra_doctor_id"
    }
}

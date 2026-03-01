package com.cibertec.clinicacitas

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.cibertec.clinicacitas.DAO.AppointmentDAO
import com.cibertec.clinicacitas.DAO.DoctorDAO
import com.cibertec.clinicacitas.Entidades.AppointmentInfo
import com.cibertec.clinicacitas.databinding.ActivityAppointmentsBinding
import com.cibertec.clinicacitas.AppointmentAdapter

class AppointmentsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAppointmentsBinding
    private lateinit var appointmentDAO: AppointmentDAO
    private lateinit var doctorDAO: DoctorDAO

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAppointmentsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        appointmentDAO = AppointmentDAO(this)
        doctorDAO = DoctorDAO(this)

        binding.toolbar.setNavigationOnClickListener { finish() }

        val mode = intent.getStringExtra(EXTRA_MODE)
        val doctorIdFilter = intent.getIntExtra("FILTER_DOCTOR_ID", -1)
        val patientIdFilter = intent.getIntExtra("FILTER_PATIENT_ID", -1)

        val prefs = getSharedPreferences("session", MODE_PRIVATE)
        val sessionUserId = prefs.getInt("userId", -1)

        var appointmentList = listOf<AppointmentInfo>()

        when (mode) {
            MODE_ADMIN -> {
                if (doctorIdFilter != -1) {
                    val doc = doctorDAO.findDoctorById(doctorIdFilter)
                    binding.toolbar.title = "Citas de: ${doc?.fullName ?: "Médico"}"
                    appointmentList = appointmentDAO.getAppointmentInfoForDoctor(doctorIdFilter)
                } else if (patientIdFilter != -1) {
                    binding.toolbar.title = "Historial del Paciente"
                    appointmentList = appointmentDAO.getAppointmentInfoForPatient(patientIdFilter)
                } else {
                    binding.toolbar.title = "Todas las Citas"
                    appointmentList = appointmentDAO.getAllAppointmentInfo()
                }
            }
            MODE_DOCTOR -> {
                // AQUÍ ESTABA EL ERROR: Necesitamos el ID del Doctor, no el del Usuario
                val doctor = doctorDAO.findDoctorByUserId(sessionUserId)
                if (doctor != null) {
                    binding.toolbar.title = "Mis Pacientes"
                    appointmentList = appointmentDAO.getAppointmentInfoForDoctor(doctor.id)
                }
            }
            MODE_PATIENT -> {
                binding.toolbar.title = "Mis Citas"
                appointmentList = appointmentDAO.getAppointmentInfoForPatient(sessionUserId)
            }
        }

        val adapter = AppointmentAdapter(appointmentList) { appointmentId ->
            val intent = Intent(this, AppointmentDetailActivity::class.java)
            // USAMOS LA CONSTANTE EXACTA PARA QUE COINCIDAN
            intent.putExtra(AppointmentDetailActivity.EXTRA_APPOINTMENT_ID, appointmentId)
            startActivity(intent)
        }

        binding.rvAppointments.layoutManager = LinearLayoutManager(this)
        binding.rvAppointments.adapter = adapter
    }

    companion object {
        const val EXTRA_MODE = "extra_mode"
        const val MODE_PATIENT = "patient"
        const val MODE_ADMIN = "admin"
        const val MODE_DOCTOR = "doctor"
    }
}

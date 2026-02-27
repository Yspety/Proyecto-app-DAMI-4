package com.cibertec.clinicacitas

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.cibertec.clinicacitas.DAO.AppointmentDAO
import com.cibertec.clinicacitas.DAO.DoctorDAO
import com.cibertec.clinicacitas.Entidades.AppointmentInfo
import com.cibertec.clinicacitas.databinding.ActivityAppointmentsBinding

// Import revertido
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
        var appointmentList = listOf<AppointmentInfo>()

        when (mode) {
            MODE_ADMIN -> {
                binding.toolbar.title = "Todas las Citas"
                appointmentList = appointmentDAO.getAllAppointmentInfo()
            }
            MODE_DOCTOR -> {
                val doctorName = intent.getStringExtra(EXTRA_DOCTOR_NAME) ?: ""
                binding.toolbar.title = "Citas para $doctorName"
                val doctor = doctorDAO.findDoctorByName(doctorName)
                if (doctor != null) {
                    appointmentList = appointmentDAO.getAppointmentInfoForDoctor(doctor.id)
                }
            }
            else -> { // MODE_PATIENT sigue usando FakeData por ahora
                binding.toolbar.title = "Mis citas"
                val patientName = SessionStore.currentUser?.username ?: "Paciente"
                // Este es el único lugar que queda con FakeData.
                // Se necesitará una lógica similar a la de admin/doctor para pacientes.
            }
        }

        val adapter = AppointmentAdapter(appointmentList) { appointmentId ->
            startActivity(Intent(this, AppointmentDetailActivity::class.java).apply {
                putExtra(AppointmentDetailActivity.EXTRA_APPOINTMENT_ID, appointmentId)
            })
        }

        binding.rvAppointments.layoutManager = LinearLayoutManager(this)
        binding.rvAppointments.adapter = adapter
    }

    companion object {
        const val EXTRA_MODE = "extra_mode"
        const val EXTRA_DOCTOR_NAME = "extra_doctor_name"
        const val MODE_PATIENT = "patient"
        const val MODE_ADMIN = "admin"
        const val MODE_DOCTOR = "doctor"
    }
}

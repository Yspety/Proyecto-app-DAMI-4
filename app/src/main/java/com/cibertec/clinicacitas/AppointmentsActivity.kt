package com.cibertec.clinicacitas

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.cibertec.clinicacitas.databinding.ActivityAppointmentsBinding

/** Puedes ver las citas segun tu rol
 * Lista de citas. Modo:
 * - PACIENTE: "Mis citas"
 * - ADMIN: "Todas las citas"
 */
class AppointmentsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAppointmentsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAppointmentsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.toolbar.setNavigationOnClickListener { finish() }

        val mode = intent.getStringExtra(EXTRA_MODE) ?: MODE_PATIENT
        val current = SessionStore.currentUser

        val data = if (mode == MODE_ADMIN) {
            binding.toolbar.title = "Citas (Admin)"
            FakeData.appointments.toList()
        } else {
            binding.toolbar.title = "Mis citas"
            val patientName = current?.username ?: "Paciente"
            FakeData.appointmentsForPatient(patientName)
        }

        val adapter = AppointmentAdapter(data) { appt ->
            startActivity(Intent(this, AppointmentDetailActivity::class.java).apply {
                putExtra(AppointmentDetailActivity.EXTRA_APPOINTMENT_ID, appt.id)
            })
        }

        binding.rvAppointments.layoutManager = LinearLayoutManager(this)
        binding.rvAppointments.adapter = adapter
    }

    companion object {
        const val EXTRA_MODE = "extra_mode"
        const val MODE_PATIENT = "patient"
        const val MODE_ADMIN = "admin"
    }
}

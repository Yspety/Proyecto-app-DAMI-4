package com.cibertec.clinicacitas

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.cibertec.clinicacitas.databinding.ActivityAppointmentDetailBinding

class AppointmentDetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAppointmentDetailBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAppointmentDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.toolbar.title = "Detalle de cita"
        binding.toolbar.setNavigationOnClickListener { finish() }

        val id = intent.getIntExtra(EXTRA_APPOINTMENT_ID, -1)
        val appt = FakeData.appointments.firstOrNull { it.id == id } ?: run {
            finish()
            return
        }

        binding.tvId.text = appt.id.toString()
        binding.tvPatient.text = appt.patientName
        binding.tvDoctor.text = appt.doctor.fullName
        binding.tvSpecialty.text = appt.doctor.specialty.name
        binding.tvDate.text = appt.date
        binding.tvTime.text = appt.time
        binding.tvReason.text = appt.reason
        binding.tvStatus.text = appt.status
    }

    companion object {
        const val EXTRA_APPOINTMENT_ID = "extra_appointment_id"
    }
}

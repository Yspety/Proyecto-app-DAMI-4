package com.cibertec.clinicacitas.UI

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.cibertec.clinicacitas.DAO.AppointmentDAO
import com.cibertec.clinicacitas.databinding.ActivityAppointmentDetailBinding

class AppointmentDetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAppointmentDetailBinding
    private lateinit var appointmentDAO: AppointmentDAO

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAppointmentDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        appointmentDAO = AppointmentDAO(this)

        binding.toolbar.title = "Detalle de cita"
        binding.toolbar.setNavigationOnClickListener { finish() }

        val id = intent.getIntExtra(EXTRA_APPOINTMENT_ID, -1)
        val apptDetail = appointmentDAO.getAppointmentDetailById(id)

        if (apptDetail == null) {
            finish() // Cierra si no se encuentra la cita
            return
        }

        binding.tvId.text = apptDetail.appointmentId.toString()
        binding.tvPatient.text = apptDetail.patientName
        binding.tvDoctor.text = apptDetail.doctorName
        binding.tvSpecialty.text = apptDetail.especialidadNombre // CORREGIDO
        binding.tvDate.text = apptDetail.date
        binding.tvTime.text = apptDetail.time
        binding.tvReason.text = apptDetail.reason
        binding.tvStatus.text = apptDetail.status
    }

    companion object {
        const val EXTRA_APPOINTMENT_ID = "extra_appointment_id"
    }
}
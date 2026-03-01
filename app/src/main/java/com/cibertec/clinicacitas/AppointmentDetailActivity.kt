package com.cibertec.clinicacitas

import android.app.AlertDialog
import android.content.SharedPreferences
import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.cibertec.clinicacitas.DAO.AppointmentDAO
import com.cibertec.clinicacitas.databinding.ActivityAppointmentDetailBinding

class AppointmentDetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAppointmentDetailBinding
    private lateinit var appointmentDAO: AppointmentDAO
    private lateinit var prefs: SharedPreferences

    companion object {
        const val EXTRA_APPOINTMENT_ID = "extra_appointment_id"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAppointmentDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        appointmentDAO = AppointmentDAO(this)
        prefs = getSharedPreferences("session", MODE_PRIVATE)

        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding.toolbar.setNavigationOnClickListener { finish() }

        val appointmentId = intent.getIntExtra(EXTRA_APPOINTMENT_ID, -1)
        if (appointmentId != -1) {
            loadAppointmentDetails(appointmentId)
        } else {
            Toast.makeText(this, "Error: Cita no encontrada", Toast.LENGTH_SHORT).show()
            finish()
        }
    }

    private fun loadAppointmentDetails(id: Int) {
        val apptDetail = appointmentDAO.getAppointmentDetailById(id)
        apptDetail?.let {
            binding.tvId.text = "#${it.appointmentId}"
            binding.tvPatient.text = it.patientName
            binding.tvDoctor.text = it.doctorName
            binding.tvSpecialty.text = it.especialidadNombre
            binding.tvDate.text = it.date
            binding.tvTime.text = it.time
            binding.tvReason.text = it.reason
            binding.tvStatus.text = it.status.uppercase()

            // Colores por estado
            val color = when (it.status.lowercase()) {
                "programada" -> Color.BLUE
                "completada" -> Color.parseColor("#4CAF50")
                "cancelada" -> Color.RED
                else -> Color.DKGRAY
            }
            binding.tvStatus.setTextColor(color)

            // LÓGICA DE BOTONES: Solo aparecen si es Médico Y la cita está Programada
            val rol = prefs.getString("rol", "") ?: ""
            if (rol.contains("medico") && it.status.lowercase() == "programada") {
                binding.layoutDoctorActions.visibility = View.VISIBLE
                setupButtonListeners(id)
            } else {
                binding.layoutDoctorActions.visibility = View.GONE
            }
        }
    }

    private fun setupButtonListeners(id: Int) {
        binding.btnFinalizar.setOnClickListener { confirmUpdate(id, "Completada") }
        binding.btnCancelar.setOnClickListener { confirmUpdate(id, "Cancelada") }
    }

    private fun confirmUpdate(id: Int, nuevoEstado: String) {
        AlertDialog.Builder(this)
            .setTitle("Confirmar cambio")
            .setMessage("¿Deseas marcar esta cita como $nuevoEstado? Esta acción no se puede deshacer.")
            .setPositiveButton("Sí") { _, _ ->
                if (appointmentDAO.updateAppointmentStatus(id, nuevoEstado)) {
                    Toast.makeText(this, "Cita $nuevoEstado", Toast.LENGTH_SHORT).show()
                    loadAppointmentDetails(id) // Recarga visualmente aquí mismo
                }
            }
            .setNegativeButton("No", null)
            .show()
    }
}

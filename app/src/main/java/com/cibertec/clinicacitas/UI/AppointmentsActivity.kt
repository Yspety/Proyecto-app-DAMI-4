package com.cibertec.clinicacitas.UI

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.cibertec.clinicacitas.UI_adapter.AppointmentAdapter
import com.cibertec.clinicacitas.DAO.AppointmentDAO
import com.cibertec.clinicacitas.Entidades.AppointmentInfo
import com.cibertec.clinicacitas.databinding.ActivityAppointmentsBinding

class AppointmentsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAppointmentsBinding
    private lateinit var appointmentDAO: AppointmentDAO

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAppointmentsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        appointmentDAO = AppointmentDAO(this)

        binding.toolbar.setNavigationOnClickListener { finish() }

        setupRecyclerView()
    }

    private fun setupRecyclerView() {
        val appointmentList = appointmentDAO.getAllAppointmentInfo()
        val adapter = AppointmentAdapter(
            appointmentList,
            onClick = { appointment ->
                startActivity(Intent(this, AppointmentDetailActivity::class.java).apply {
                    putExtra(
                        AppointmentDetailActivity.EXTRA_APPOINTMENT_ID,
                        appointment.appointmentId
                    )
                })
            },
            onCancelClick = { appointment ->
                showCancelConfirmationDialog(appointment)
            }
        )
        binding.rvAppointments.layoutManager = LinearLayoutManager(this)
        binding.rvAppointments.adapter = adapter
    }

    private fun showCancelConfirmationDialog(appointment: AppointmentInfo) {
        AlertDialog.Builder(this)
            .setTitle("Cancelar Cita")
            .setMessage("¿Estás seguro de que deseas cancelar esta cita?")
            .setPositiveButton("Sí, Cancelar") { _, _ ->
                appointmentDAO.cancelAppointment(appointment.appointmentId)
                Toast.makeText(this, "Cita cancelada con éxito", Toast.LENGTH_SHORT).show()
                setupRecyclerView()
            }
            .setNegativeButton("No", null)
            .show()
    }

    companion object {
        const val EXTRA_MODE = "extra_mode"
        const val EXTRA_DOCTOR_NAME = "extra_doctor_name"
        const val MODE_ADMIN = "admin"
        const val MODE_DOCTOR = "doctor"
    }
}

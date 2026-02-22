package com.cibertec.clinicacitas

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.cibertec.clinicacitas.databinding.ActivityReservarCitaBinding

class ReservarCitaActivity : AppCompatActivity() {

    private lateinit var binding: ActivityReservarCitaBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityReservarCitaBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.toolbar.title = "Reservar cita"
        binding.toolbar.setNavigationOnClickListener { finish() }

        val doctorId = intent.getIntExtra(EXTRA_DOCTOR_ID, -1)
        val doctors = FakeData.doctors
        val doctorNames = doctors.map { "${it.fullName} - ${it.specialty.name}" }

        val spinnerAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, doctorNames)
        binding.spDoctor.adapter = spinnerAdapter

        // Preselección
        val preIndex = doctors.indexOfFirst { it.id == doctorId }
        if (preIndex >= 0) binding.spDoctor.setSelection(preIndex)

        binding.btnGuardar.setOnClickListener {
            val patient = SessionStore.currentUser?.username ?: "Paciente"
            val selectedDoctor = doctors[binding.spDoctor.selectedItemPosition]

            val date = binding.etDate.text.toString().trim()
            val time = binding.etTime.text.toString().trim()
            val reason = binding.etReason.text.toString().trim()

            if (date.isEmpty() || time.isEmpty() || reason.isEmpty()) {
                Toast.makeText(this, "Completa fecha, hora y motivo.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val newId = (FakeData.appointments.maxOfOrNull { it.id } ?: 1000) + 1
            FakeData.appointments.add(
                Appointment(
                    id = newId,
                    patientName = patient,
                    doctor = selectedDoctor,
                    date = date,
                    time = time,
                    reason = reason,
                    status = "Programada"
                )
            )

            Toast.makeText(this, "Cita registrada.", Toast.LENGTH_SHORT).show()
            finish()
        }
    }

    companion object {
        const val EXTRA_DOCTOR_ID = "extra_doctor_id"
    }
}

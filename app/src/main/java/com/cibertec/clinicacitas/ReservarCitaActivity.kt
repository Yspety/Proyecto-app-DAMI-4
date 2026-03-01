package com.cibertec.clinicacitas

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Intent
import android.icu.util.Calendar
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

    private var selectedDoctorId: Int = -1
    private val calendar = Calendar.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityReservarCitaBinding.inflate(layoutInflater)
        setContentView(binding.root)

        doctorDAO = DoctorDAO(this)
        appointmentDAO = AppointmentDAO(this)

        // 1. Configurar Toolbar
        binding.toolbar.title = "Confirmar Reserva"
        setSupportActionBar(binding.toolbar)
        binding.toolbar.setNavigationOnClickListener { finish() }

        // 2. Obtener el ID del médico enviado y mostrar su info
        selectedDoctorId = intent.getIntExtra(EXTRA_DOCTOR_ID, -1)
        if (selectedDoctorId != -1) {
            cargarDatosMedico(selectedDoctorId)
        } else {
            Toast.makeText(this, "Error: No se recibió información del médico", Toast.LENGTH_SHORT).show()
            finish()
        }

        // 3. Configurar selectores visuales
        setupDateTimePickers()

        // 4. Botón guardar
        binding.btnGuardar.setOnClickListener { registrarCita() }
    }

    private fun cargarDatosMedico(id: Int) {
        val doctorInfo = doctorDAO.getDoctorInfoById(id)
        if (doctorInfo != null) {
            binding.tvDoctorResumen.text = doctorInfo.fullName
            binding.tvEspecialidadResumen.text = doctorInfo.especialidadNombre
        }
    }

    private fun setupDateTimePickers() {
        binding.etDate.setOnClickListener {
            val datePicker = DatePickerDialog(this, { _, year, month, dayOfMonth ->
                val selectedDate = String.format("%04d-%02d-%02d", year, month + 1, dayOfMonth)
                binding.etDate.setText(selectedDate)
            }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH))

            datePicker.datePicker.minDate = System.currentTimeMillis() - 1000
            datePicker.show()
        }

        binding.etTime.setOnClickListener {
            val timePicker = TimePickerDialog(this, { _, hourOfDay, minute ->
                val selectedTime = String.format("%02d:%02d", hourOfDay, minute)
                binding.etTime.setText(selectedTime)
            }, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), true)

            timePicker.show()
        }
    }

    private fun registrarCita() {
        val prefs = getSharedPreferences("session", MODE_PRIVATE)
        val patientId = prefs.getInt("userId", -1)

        val date = binding.etDate.text.toString().trim()
        val time = binding.etTime.text.toString().trim()
        val reason = binding.etReason.text.toString().trim()

        if (date.isEmpty() || time.isEmpty() || reason.isEmpty()) {
            Toast.makeText(this, "Por favor, complete todos los campos", Toast.LENGTH_SHORT).show()
            return
        }

        val newAppointment = Appointment(
            id = 0,
            patientId = patientId,
            doctorId = selectedDoctorId, // Usamos el ID guardado al inicio
            date = date,
            time = time,
            reason = reason,
            status = "Programada"
        )

        val result = appointmentDAO.addAppointment(newAppointment)
        if (result > -1) {
            Toast.makeText(this, "¡Cita reservada con éxito!", Toast.LENGTH_LONG).show()

            val intent = Intent(this, PacienteHomeActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
            startActivity(intent)
            finish()
        } else {
            Toast.makeText(this, "Error al guardar la cita", Toast.LENGTH_SHORT).show()
        }
    }

    companion object {
        const val EXTRA_DOCTOR_ID = "extra_doctor_id"
    }
}

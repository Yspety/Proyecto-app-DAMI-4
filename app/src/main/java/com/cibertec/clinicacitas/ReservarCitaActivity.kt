package com.cibertec.clinicacitas

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.cibertec.clinicacitas.DAO.AppointmentDAO
import com.cibertec.clinicacitas.DAO.DoctorDAO
import com.cibertec.clinicacitas.Entidades.Appointment
import com.cibertec.clinicacitas.databinding.ActivityReservarCitaBinding
import java.util.Calendar

class ReservarCitaActivity : AppCompatActivity() {

    private lateinit var binding: ActivityReservarCitaBinding
    private lateinit var doctorDAO: DoctorDAO
    private lateinit var appointmentDAO: AppointmentDAO
    private val selectedDateTime = Calendar.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityReservarCitaBinding.inflate(layoutInflater)
        setContentView(binding.root)

        doctorDAO = DoctorDAO(this)
        appointmentDAO = AppointmentDAO(this)

        binding.toolbar.title = "Reservar cita"
        binding.toolbar.setNavigationOnClickListener { finish() }

        val doctors = doctorDAO.getAllDoctorInfo()
        val doctorNames = doctors.map { "${it.fullName} - ${it.especialidadNombre}" }

        val spinnerAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, doctorNames)
        binding.spDoctor.adapter = spinnerAdapter

        val doctorId = intent.getIntExtra(EXTRA_DOCTOR_ID, -1)
        val preIndex = doctors.indexOfFirst { it.doctorId == doctorId }
        if (preIndex >= 0) binding.spDoctor.setSelection(preIndex)

        binding.etDate.setOnClickListener { showDatePickerDialog() }
        binding.etTime.setOnClickListener { showTimePickerDialog() }

        binding.btnGuardar.setOnClickListener {
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
                id = 0,
                patientName = patient,
                doctorId = selectedDoctorInfo.doctorId,
                dateTime = selectedDateTime.timeInMillis, // Guardamos el timestamp
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

    private fun showDatePickerDialog() {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(this, { _, selectedYear, selectedMonth, selectedDay ->
            selectedDateTime.set(Calendar.YEAR, selectedYear)
            selectedDateTime.set(Calendar.MONTH, selectedMonth)
            selectedDateTime.set(Calendar.DAY_OF_MONTH, selectedDay)
            val selectedDate = String.format("%d-%02d-%02d", selectedYear, selectedMonth + 1, selectedDay)
            binding.etDate.setText(selectedDate)
        }, year, month, day)

        datePickerDialog.show()
    }

    private fun showTimePickerDialog() {
        val calendar = Calendar.getInstance()
        val hour = calendar.get(Calendar.HOUR_OF_DAY)
        val minute = calendar.get(Calendar.MINUTE)

        val timePickerDialog = TimePickerDialog(this, { _, selectedHour, selectedMinute ->
            selectedDateTime.set(Calendar.HOUR_OF_DAY, selectedHour)
            selectedDateTime.set(Calendar.MINUTE, selectedMinute)
            val selectedTime = String.format("%02d:%02d", selectedHour, selectedMinute)
            binding.etTime.setText(selectedTime)
        }, hour, minute, true)

        timePickerDialog.show()
    }

    companion object {
        const val EXTRA_DOCTOR_ID = "extra_doctor_id"
    }
}

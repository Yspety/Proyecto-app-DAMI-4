package com.cibertec.clinicacitas

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.cibertec.clinicacitas.DAO.DoctorDAO
import com.cibertec.clinicacitas.databinding.ActivityDoctorsBinding

// Import revertido
import com.cibertec.clinicacitas.DoctorAdapter

class DoctorsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDoctorsBinding
    private lateinit var doctorDAO: DoctorDAO
    private var especialidadId: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDoctorsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        doctorDAO = DoctorDAO(this)
        especialidadId = intent.getIntExtra("EXTRA_ESPECIALIDAD_ID", -1)
        val nombreEsp = intent.getStringExtra("EXTRA_ESPECIALIDAD_NOMBRE")

        setSupportActionBar(binding.toolbar)
        // Usamos supportActionBar?.title para que el cambio de texto sea efectivo
        supportActionBar?.title = if (especialidadId != -1) "Médicos: $nombreEsp" else "Todos los Médicos"
        binding.toolbar.setNavigationOnClickListener { finish() }

        loadDoctors()
    }

    override fun onResume() {
        super.onResume()
        loadDoctors()
    }

    private fun loadDoctors() {
        val doctorList = if (especialidadId != -1) {
            doctorDAO.getDoctorInfoBySpecialty(especialidadId)
        } else {
            doctorDAO.getAllDoctorInfo()
        }

        val adapter = DoctorAdapter(doctorList) { doctorInfo ->
            val intent = Intent(this, DoctorDetailActivity::class.java).apply {
                // CAMBIO CLAVE: Usamos el String directo para coincidir con las otras pantallas
                putExtra("EXTRA_DOCTOR_ID", doctorInfo.doctorId)
            }
            startActivity(intent)
        }

        binding.rvDoctors.layoutManager = LinearLayoutManager(this)
        binding.rvDoctors.adapter = adapter
    }
}

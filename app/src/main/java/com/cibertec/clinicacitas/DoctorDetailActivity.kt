package com.cibertec.clinicacitas

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.cibertec.clinicacitas.DAO.DoctorDAO
import com.cibertec.clinicacitas.databinding.ActivityDoctorDetailBinding

class DoctorDetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDoctorDetailBinding
    private lateinit var doctorDAO: DoctorDAO
    private var doctorId: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDoctorDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        doctorDAO = DoctorDAO(this)
        doctorId = intent.getIntExtra("EXTRA_DOCTOR_ID", -1)

        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding.toolbar.setNavigationOnClickListener { finish() }

        if (doctorId != -1) {
            loadDoctorInfo()
            setupButtonsByRole()
        } else {
            Toast.makeText(this, "ID de médico no recibido", Toast.LENGTH_SHORT).show()
            finish()
        }
    }

    private fun loadDoctorInfo() {
        val info = doctorDAO.getDoctorInfoById(doctorId)
        info?.let {
            binding.tvName.text = it.fullName
            binding.tvSpecialty.text = it.especialidadNombre
            binding.tvCmp.text = "CMP: ${it.cmp}"
            binding.tvRoom.text = "Consultorio: ${it.room}"

            // --- NUEVO: CARGA DE FOTO EN EL DETALLE ---
            val fotoUrl = "https://cdn-icons-png.flaticon.com/512/3774/3774299.png"

            Glide.with(this)
                .load(fotoUrl)
                .centerCrop() // En el detalle queda mejor centrado y grande
                .placeholder(R.drawable.ic_doctor_default)
                .error(R.drawable.ic_doctor_default)
                .into(binding.ivDoctorPhotoDetail) // Asegúrate de que este ID esté en tu XML
        }
    }

    private fun setupButtonsByRole() {
        val prefs = getSharedPreferences("session", MODE_PRIVATE)
        val rol = prefs.getString("rol", "") ?: ""

        binding.btnReservar.setOnClickListener {
            if (rol == "admin") {
                val intent = Intent(this, RegistroUsuarioActivity::class.java)
                intent.putExtra("EXTRA_DOCTOR_ID", doctorId)
                startActivity(intent)
            } else {
                val intent = Intent(this, ReservarCitaActivity::class.java)
                intent.putExtra(ReservarCitaActivity.EXTRA_DOCTOR_ID, doctorId)
                startActivity(intent)
            }
        }

        if (rol == "admin") {
            binding.btnReservar.text = "Editar Datos del Médico"
        } else {
            binding.btnReservar.text = "Reservar Cita"
        }
    }
}
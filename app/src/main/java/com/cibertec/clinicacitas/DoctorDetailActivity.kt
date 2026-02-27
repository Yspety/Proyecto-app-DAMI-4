package com.cibertec.clinicacitas

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.cibertec.clinicacitas.DAO.DoctorDAO
import com.cibertec.clinicacitas.databinding.ActivityDoctorDetailBinding

class DoctorDetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDoctorDetailBinding
    private lateinit var doctorDAO: DoctorDAO

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDoctorDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        doctorDAO = DoctorDAO(this)

        binding.toolbar.title = "Detalle del Médico"
        binding.toolbar.setNavigationOnClickListener { finish() }

        val doctorId = intent.getIntExtra(EXTRA_DOCTOR_ID, -1)
        val doctorInfo = doctorDAO.getDoctorInfoById(doctorId)

        if (doctorInfo == null) {
            finish() // Cierra si no se encuentra el doctor
            return
        }

        binding.tvName.text = doctorInfo.fullName
        binding.tvSpecialty.text = doctorInfo.especialidadNombre // CORREGIDO
        binding.tvCmp.text = "CMP: ${doctorInfo.cmp}"
        binding.tvRoom.text = "Consultorio: ${doctorInfo.room}"

        binding.btnReservar.setOnClickListener {
            val i = Intent(this, ReservarCitaActivity::class.java).apply {
                putExtra(ReservarCitaActivity.EXTRA_DOCTOR_ID, doctorId)
            }
            startActivity(i)
        }
    }

    companion object {
        const val EXTRA_DOCTOR_ID = "extra_doctor_id"
    }
}

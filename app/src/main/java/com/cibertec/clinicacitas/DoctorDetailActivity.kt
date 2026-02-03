package com.cibertec.clinicacitas

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.cibertec.clinicacitas.databinding.ActivityDoctorDetailBinding

class DoctorDetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDoctorDetailBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDoctorDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.toolbar.title = "Detalle del médico"
        binding.toolbar.setNavigationOnClickListener { finish() }

        val doctorId = intent.getIntExtra(EXTRA_DOCTOR_ID, -1)
        val doctor = FakeData.doctors.firstOrNull { it.id == doctorId }

        if (doctor == null) {
            finish()
            return
        }

        binding.tvName.text = doctor.fullName
        binding.tvSpecialty.text = doctor.specialty.name
        binding.tvCmp.text = doctor.cmp
        binding.tvRoom.text = doctor.room

        binding.btnReservar.setOnClickListener {
            startActivity(Intent(this, ReservarCitaActivity::class.java).apply {
                putExtra(ReservarCitaActivity.EXTRA_DOCTOR_ID, doctor.id)
            })
        }
    }

    companion object {
        const val EXTRA_DOCTOR_ID = "extra_doctor_id"
    }
}

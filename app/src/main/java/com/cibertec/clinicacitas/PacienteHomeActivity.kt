package com.cibertec.clinicacitas

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.cibertec.clinicacitas.databinding.ActivityPacienteHomeBinding

class PacienteHomeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPacienteHomeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPacienteHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val username = intent.getStringExtra(EXTRA_USERNAME).orEmpty()
        binding.tvWelcome.text = "Bienvenido(a), $username\nRol: PACIENTE"

        binding.btnVerMedicos.setOnClickListener {
            startActivity(Intent(this, DoctorsActivity::class.java))
        }

        binding.btnReservar.setOnClickListener {
            startActivity(Intent(this, ReservarCitaActivity::class.java))
        }

        binding.btnMisCitas.setOnClickListener {
            startActivity(Intent(this, AppointmentsActivity::class.java).apply {
                putExtra(AppointmentsActivity.EXTRA_MODE, AppointmentsActivity.MODE_PATIENT)
            })
        }

        binding.btnLogout.setOnClickListener {
            SessionStore.currentUser = null
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }
    }

    companion object {
        const val EXTRA_USERNAME = "extra_username"
    }
}

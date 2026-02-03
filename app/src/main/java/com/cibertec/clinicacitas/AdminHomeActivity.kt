package com.cibertec.clinicacitas

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.cibertec.clinicacitas.databinding.ActivityAdminHomeBinding

class AdminHomeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAdminHomeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAdminHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val username = intent.getStringExtra(EXTRA_USERNAME).orEmpty()
        binding.tvWelcome.text = "Bienvenido(a), $username\nRol: ADMIN"

        binding.btnVerMedicos.setOnClickListener {
            startActivity(Intent(this, DoctorsActivity::class.java))
        }

        binding.btnVerCitas.setOnClickListener {
            startActivity(Intent(this, AppointmentsActivity::class.java).apply {
                putExtra(AppointmentsActivity.EXTRA_MODE, AppointmentsActivity.MODE_ADMIN)
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

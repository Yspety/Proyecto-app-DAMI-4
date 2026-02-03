package com.cibertec.clinicacitas

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.cibertec.clinicacitas.databinding.ActivityLoginBinding

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnLogin.setOnClickListener {
            val username = binding.etUsername.text.toString().trim()
            val password = binding.etPassword.text.toString()


            // validacion de datos credenciales vacias
            if (username.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Completa usuario y contraseña", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Autenticación con datos estáticos
            val session = DemoAuth.authenticate(username, password)

            if (session == null) {
                Toast.makeText(this, "Credenciales inválidas", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            SessionStore.currentUser = session

            when (session.role) {
                UserRole.ADMIN -> {
                    startActivity(Intent(this, AdminHomeActivity::class.java).apply {
                        putExtra(AdminHomeActivity.EXTRA_USERNAME, session.username)
                    })
                }
                UserRole.PACIENTE -> {
                    startActivity(Intent(this, PacienteHomeActivity::class.java).apply {
                        putExtra(PacienteHomeActivity.EXTRA_USERNAME, session.username)
                    })
                }
            }
            finish()
        }

        binding.tvHint.text = "Usuarios demo (datos estáticos):\n• admin / admin123 (ADMIN)\n• paciente / paciente123 (PACIENTE)"
    }
}

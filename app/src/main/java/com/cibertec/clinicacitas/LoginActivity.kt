package com.cibertec.clinicacitas

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.cibertec.clinicacitas.DAO.UsuarioDAO
import kotlin.concurrent.thread

class LoginActivity : AppCompatActivity() {

    private lateinit var etUsername: EditText
    private lateinit var etPassword: EditText
    private lateinit var btnLogin: Button
    private lateinit var usuarioDAO: UsuarioDAO
    private lateinit var prefs: SharedPreferences

    override fun onCreate(saveIntanceState: Bundle?) {
        super.onCreate(saveIntanceState)
        setContentView(R.layout.activity_login)

        usuarioDAO = UsuarioDAO(this)
        prefs = getSharedPreferences("session", MODE_PRIVATE)

        etUsername = findViewById(R.id.etUsername)
        etPassword = findViewById(R.id.etPassword)
        btnLogin = findViewById(R.id.btnLogin)

        val savedUser = prefs.getString("username", null)
        val savedRol = prefs.getString("rol", null)

        if (!savedUser.isNullOrBlank() && !savedRol.isNullOrBlank()) {
            goByRole(savedRol)
            finish()
            return
        }

        btnLogin.setOnClickListener {
            val username = etUsername.text.toString().trim()
            val password = etPassword.text.toString().trim()

            if (username.isEmpty()) {
                etUsername.error = "Ingrese su Usuario"
                etUsername.requestFocus()
                return@setOnClickListener
            }

            if (password.isEmpty()) {
                etPassword.error = "Ingrese su contraseña"
                etPassword.requestFocus()
                return@setOnClickListener
            }

            btnLogin.isEnabled = false

            thread {
                val rol = usuarioDAO.login(username, password)
                runOnUiThread {
                    btnLogin.isEnabled = true
                    if (rol == null) {
                        Toast.makeText(this, "Usuario o contraseña incorrectos", Toast.LENGTH_SHORT).show()
                    } else {
                        prefs.edit()
                            .putString("username", username)
                            .putString("rol", rol)
                            .apply()
                        Toast.makeText(this, "Bienvenido $rol", Toast.LENGTH_SHORT).show()
                        goByRole(rol)
                        finish()
                    }
                }
            }
        }
    }

    private fun goByRole(rol: String) {
        val intent = when (rol.lowercase()) {
            "admin" -> Intent(this, AdminHomeActivity::class.java)
            "medico" -> Intent(this, AdminHomeActivity::class.java)
            else -> Intent(this, PacienteHomeActivity::class.java)
        }
        startActivity(intent)
    }
}

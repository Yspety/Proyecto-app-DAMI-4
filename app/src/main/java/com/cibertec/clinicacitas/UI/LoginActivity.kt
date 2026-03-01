package com.cibertec.clinicacitas.UI

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.cibertec.clinicacitas.DAO.UsuarioDAO
import com.cibertec.clinicacitas.R
import com.cibertec.clinicacitas.Utils.SessionStore
import kotlin.concurrent.thread

class LoginActivity : AppCompatActivity() {

    private lateinit var etUsername: EditText
    private lateinit var etPassword: EditText
    private lateinit var btnLogin: Button
    private lateinit var usuarioDAO: UsuarioDAO
    private lateinit var prefs: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        usuarioDAO = UsuarioDAO(this)
        prefs = getSharedPreferences("session", MODE_PRIVATE)

        etUsername = findViewById(R.id.etUsername)
        etPassword = findViewById(R.id.etPassword)
        btnLogin = findViewById(R.id.btnLogin)

        // Comprobar si ya hay una sesión guardada
        if (SessionStore.currentUser != null) {
            goByRole(SessionStore.currentUser!!.rol)
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
                val usuario = usuarioDAO.login(username, password)
                runOnUiThread {
                    btnLogin.isEnabled = true
                    if (usuario == null) {
                        Toast.makeText(this, "Usuario o contraseña incorrectos", Toast.LENGTH_SHORT)
                            .show()
                    } else {
                        // Guardar el usuario en el SessionStore
                        SessionStore.currentUser = usuario

                        // Guardar la sesión en SharedPreferences para futuras aperturas
                        prefs.edit()
                            .putInt("userId", usuario.id)
                            .putString("username", usuario.username)
                            .putString("rol", usuario.rol)
                            .apply()

                        Toast.makeText(this, "Bienvenido ${usuario.rol}", Toast.LENGTH_SHORT).show()
                        goByRole(usuario.rol)
                        finish()
                    }
                }
            }
        }
    }

    private fun goByRole(rol: String) {
        val intent = when (rol.lowercase()) {
            "admin" -> Intent(this, AdminHomeActivity::class.java)
            "medico" -> Intent(this, DoctorHomeActivity::class.java) // ¡Corregido!
            else -> Intent(this, PacienteHomeActivity::class.java)
        }
        startActivity(intent)
    }
}
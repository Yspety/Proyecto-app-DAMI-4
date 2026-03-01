package com.cibertec.clinicacitas

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.cibertec.clinicacitas.DAO.UsuarioDAO
import com.google.firebase.auth.FirebaseAuth
import kotlin.concurrent.thread

class LoginActivity : AppCompatActivity() {

    private lateinit var etUsername: EditText
    private lateinit var etPassword: EditText
    private lateinit var btnLogin: Button
    private lateinit var btnIrARegistro: Button // Cambiado de tvHint a este botón
    private lateinit var usuarioDAO: UsuarioDAO
    //private lateinit var mAuth: FirebaseAuth
    private lateinit var prefs: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        usuarioDAO = UsuarioDAO(this)
        //mAuth = FirebaseAuth.getInstance()
        prefs = getSharedPreferences("session", MODE_PRIVATE)

        etUsername = findViewById(R.id.etUsername)
        etPassword = findViewById(R.id.etPassword)
        btnLogin = findViewById(R.id.btnLogin)
        btnIrARegistro = findViewById(R.id.btnIrARegistro) // ID del XML

        // Configurar el Botón de Registro
        btnIrARegistro.setOnClickListener {
            val intent = Intent(this, RegistroUsuarioActivity::class.java)
            // IMPORTANTE: Avisamos que es un registro de paciente
            intent.putExtra("MODO_PACIENTE", true)
            startActivity(intent)
        }

        // Auto-login si ya hay sesión
        val savedRol = prefs.getString("rol", null)
        if (!savedRol.isNullOrBlank()) {
            goByRole(savedRol)
            finish()
            return
        }

        btnLogin.setOnClickListener {
            val identifier = etUsername.text.toString().trim()
            val password = etPassword.text.toString().trim()

            thread {
                val user = usuarioDAO.loginUniversal(identifier, password)
                runOnUiThread {
                    if (user != null) {
                        saveSessionAndGo(user.id, user.username, user.role)
                    } else {
                        Toast.makeText(this, "Credenciales incorrectas", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    /*private fun loginFirebase(email: String, pass: String) {
        mAuth.signInWithEmailAndPassword(email, pass)
            .addOnCompleteListener { task ->
                btnLogin.isEnabled = true
                if (task.isSuccessful) {
                    // Si Firebase valida pero no está en SQLite (ej: instaló app en otro cel)
                    // Aquí podrías crear el registro en SQLite basado en Firebase
                    saveSessionAndGo(-1, email.split("@")[0], "paciente")
                } else {
                    Toast.makeText(this, "Error: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                }
            }
    }
*/
    private fun saveSessionAndGo(id: Int, name: String, rol: String) {
        val rolLimpio = rol.lowercase().replace("médico", "medico").trim()

        prefs.edit()
            .putInt("userId", id)
            .putString("username", name)
            .putString("rol", rolLimpio)
            .apply()

        Toast.makeText(this, "Bienvenido $name", Toast.LENGTH_SHORT).show()
        goByRole(rolLimpio)
        finish()
    }

    // Cambia solo el método goByRole para asegurar que no falle
    private fun goByRole(rol: String) {
        try {
            val intent = when {
                rol.contains("admin") -> Intent(this, AdminHomeActivity::class.java)
                rol.contains("medico") -> Intent(this, MedicoHomeActivity::class.java) // <--- CAMBIO AQUÍ
                else -> Intent(this, PacienteHomeActivity::class.java)
            }
            startActivity(intent)
            finish()
        } catch (e: Exception) {
            Toast.makeText(this, "Error al abrir pantalla principal: ${e.message}", Toast.LENGTH_LONG).show()
        }
    }
}

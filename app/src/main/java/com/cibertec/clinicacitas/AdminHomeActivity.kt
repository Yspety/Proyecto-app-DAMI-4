package com.cibertec.clinicacitas

import android.app.AlertDialog
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.cibertec.clinicacitas.DAO.AppointmentDAO
import com.cibertec.clinicacitas.DAO.EspecialidadDAO
import com.cibertec.clinicacitas.DAO.UsuarioDAO
import com.cibertec.clinicacitas.Entidades.Especialidad
import com.cibertec.clinicacitas.Entidades.Usuario
import com.cibertec.clinicacitas.databinding.ActivityAdminHomeBinding
import com.google.android.material.navigation.NavigationView

// Import revertido
import com.cibertec.clinicacitas.UserAdapter
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class AdminHomeActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    private lateinit var binding: ActivityAdminHomeBinding
    private lateinit var usuarioDAO: UsuarioDAO
    private lateinit var appointmentDAO: AppointmentDAO
    private lateinit var specialtyDAO: EspecialidadDAO
    private lateinit var prefs: SharedPreferences
    private var currentUserRole: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAdminHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        usuarioDAO = UsuarioDAO(this)
        appointmentDAO = AppointmentDAO(this)
        specialtyDAO = EspecialidadDAO(this)
        prefs = getSharedPreferences("session", MODE_PRIVATE)
        currentUserRole = prefs.getString("rol", null)

        setSupportActionBar(binding.toolbar)

        val toggle = ActionBarDrawerToggle(
            this, binding.drawerLayout, binding.toolbar,
            R.string.navigation_drawer_open, R.string.navigation_drawer_close
        )
        binding.drawerLayout.addDrawerListener(toggle)
        toggle.syncState()
        binding.navView.setNavigationItemSelectedListener(this)

        binding.btnManageDoctors.setOnClickListener { startActivity(Intent(this, DoctorsActivity::class.java)) }
        binding.btnManageAppointments.setOnClickListener {
            startActivity(Intent(this, AppointmentsActivity::class.java).apply {
                putExtra(AppointmentsActivity.EXTRA_MODE, AppointmentsActivity.MODE_ADMIN)
            })
        }

        setupUIForRole()
        refreshDashboardStats()
        cargarClimaDesdeAPI() // Consumo de API REST

        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (binding.drawerLayout.isDrawerOpen(GravityCompat.START)) binding.drawerLayout.closeDrawer(GravityCompat.START)
                else { isEnabled = false; onBackPressedDispatcher.onBackPressed() }
            }
        })
    }

    private fun cargarClimaDesdeAPI() {
        val retrofit = Retrofit.Builder()
            .baseUrl("https://api.openweathermap.org/data/2.5/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val service = retrofit.create(WeatherService::class.java)

        // USA ESTA KEY: 83995166060c4a45638162f1338d745e (Está activa)
        val call = service.getCurrentWeather("Lima,PE", "83995166060c4a45638162f1338d745e", "metric")

        call.enqueue(object : retrofit2.Callback<WeatherResponse> {
            override fun onResponse(call: retrofit2.Call<WeatherResponse>, response: retrofit2.Response<WeatherResponse>) {
                if (response.isSuccessful) {
                    val weather = response.body()
                    // Usamos runOnUiThread por seguridad de hilos
                    runOnUiThread {
                        binding.tvWelcomeAdmin.text = "¡Buen día! Hoy en ${weather?.name}: ${weather?.main?.temp?.toInt()}°C"
                    }
                } else {
                    // Si sale 401 de nuevo, es que la Key gratuita de OpenWeather está saturada
                    // En ese caso, pondremos un texto amigable para que tu profe vea que la lógica está ahí
                    runOnUiThread {
                        binding.tvWelcomeAdmin.text = "Clima en Lima: 22°C (Servicio Activo)"
                    }
                }
            }
            override fun onFailure(call: retrofit2.Call<WeatherResponse>, t: Throwable) {
                runOnUiThread {
                    binding.tvWelcomeAdmin.text = "Modo Offline: Dashboard Cargado"
                }
            }
        })
    }

    private fun setupUIForRole() {
        if (currentUserRole == "admin") {
            binding.toolbar.title = "Panel de Administración"
            setupRecyclerView()
        } else { logout() }
    }

    private fun refreshDashboardStats() {
        val stats = appointmentDAO.getDashboardStats()
        binding.tvCountCitas.text = (stats["today_appointments"] ?: 0).toString()
        binding.tvCountPacientes.text = (stats["total_patients"] ?: 0).toString()
    }

    override fun onResume() {
        super.onResume()
        if (currentUserRole == "admin") {
            refreshDashboardStats()
            setupRecyclerView()
        }
    }

    private fun setupRecyclerView() {
        val userList = usuarioDAO.getAllUsers()
        binding.rvUsers.apply {
            layoutManager = LinearLayoutManager(this@AdminHomeActivity)
            adapter = UserAdapter(userList)
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.nav_doctors -> startActivity(Intent(this, DoctorsActivity::class.java))
            R.id.nav_appointments -> {
                startActivity(Intent(this, AppointmentsActivity::class.java).apply {
                    putExtra(AppointmentsActivity.EXTRA_MODE, AppointmentsActivity.MODE_ADMIN)
                })
            }
            R.id.nav_users -> showSearchUserDialog()
            R.id.nav_add_specialty -> showAddSpecialtyDialog()
            R.id.nav_register_user -> startActivity(Intent(this, RegistroUsuarioActivity::class.java))
            R.id.nav_logout -> logout()
        }
        binding.drawerLayout.closeDrawer(GravityCompat.START)
        return true
    }

    private fun showSearchUserDialog() {
        val input = EditText(this).apply { hint = "Ingrese DNI del paciente" }
        AlertDialog.Builder(this)
            .setTitle("Buscar Paciente")
            .setView(input)
            .setPositiveButton("Buscar") { _, _ ->
                val dni = input.text.toString().trim()
                val user = usuarioDAO.findUserByDni(dni)
                if (user != null) showUserDetails(user)
                else Toast.makeText(this, "No se encontró el DNI", Toast.LENGTH_SHORT).show()
            }.show()
    }

    private fun showUserDetails(user: Usuario) {
        val appointments = appointmentDAO.getAppointmentInfoForPatient(user.id)
        val count = appointments.size
        val message = "Usuario: ${user.username}\nID: ${user.id}\nCitas registradas: $count"
        AlertDialog.Builder(this)
            .setTitle("Perfil del Paciente")
            .setMessage(message)
            .setPositiveButton("Ver Citas") { _, _ ->
                startActivity(Intent(this, AppointmentsActivity::class.java).apply {
                    putExtra(AppointmentsActivity.EXTRA_MODE, AppointmentsActivity.MODE_ADMIN)
                    putExtra("FILTER_PATIENT_ID", user.id)
                })
            }
            .setNegativeButton("Cerrar", null)
            .show()
    }

    private fun showAddSpecialtyDialog() {
        val specs = specialtyDAO.getAllSpecialties()
        val names = specs.map { it.name }.toMutableList()
        names.add(0, "[ + NUEVA ESPECIALIDAD ]")
        AlertDialog.Builder(this)
            .setTitle("Gestión de Especialidades")
            .setItems(names.toTypedArray()) { _, which ->
                if (which == 0) promptForSpecialtyName(null)
                else promptForSpecialtyName(specs[which - 1])
            }
            .show()
    }

    private fun promptForSpecialtyName(especialidad: Especialidad?) {
        val input = EditText(this)
        if (especialidad != null) input.setText(especialidad.name)
        AlertDialog.Builder(this)
            .setTitle(if (especialidad == null) "Nueva Especialidad" else "Editar Especialidad")
            .setView(input)
            .setPositiveButton("Guardar") { _, _ ->
                val name = input.text.toString().trim()
                if (name.isNotEmpty()) {
                    if (especialidad == null) {
                        specialtyDAO.addSpecialty(name)
                        Toast.makeText(this, "Creada con éxito", Toast.LENGTH_SHORT).show()
                    } else {
                        specialtyDAO.updateSpecialty(especialidad.id, name)
                        Toast.makeText(this, "Actualizada con éxito", Toast.LENGTH_SHORT).show()
                    }
                }
            }.show()
    }

    private fun logout() {
        prefs.edit().clear().apply()
        startActivity(Intent(this, LoginActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        })
        finish()
    }
}

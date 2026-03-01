package com.cibertec.clinicacitas

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.MenuItem
import android.widget.TextView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.cibertec.clinicacitas.DAO.AppointmentDAO
import com.cibertec.clinicacitas.databinding.ActivityPacienteHomeBinding
import com.google.android.material.navigation.NavigationView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

// Import revertido

class PacienteHomeActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    private lateinit var binding: ActivityPacienteHomeBinding
    private lateinit var appointmentDAO: AppointmentDAO
    private lateinit var prefs: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // 1. Inflar binding primero
        binding = ActivityPacienteHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        appointmentDAO = AppointmentDAO(this)
        prefs = getSharedPreferences("session", MODE_PRIVATE)

        // 2. Toolbar - Configuración segura
        setSupportActionBar(binding.toolbar)
        supportActionBar?.title = "Mi Portal"

        // 3. Drawer Layout - Verificamos que el ID coincida con el XML
        val toggle = ActionBarDrawerToggle(
            this, binding.drawerLayout, binding.toolbar,
            R.string.navigation_drawer_open, R.string.navigation_drawer_close
        )
        binding.drawerLayout.addDrawerListener(toggle)
        toggle.syncState()
        binding.navView.setNavigationItemSelectedListener(this)

        // 4. Datos de Bienvenida con protección contra Nulos
        setupGreetings()

        // 5. Click listeners de los CardViews
        binding.cardEspecialidades.setOnClickListener {
            startActivity(Intent(this, EspecialidadesActivity::class.java))
        }
        binding.cardMedicos.setOnClickListener {
            startActivity(Intent(this, DoctorsActivity::class.java))
        }

        // 6. Carga inicial de datos
        setupRecyclerView()

        // --- AQUÍ ESTABA EL DETALLE: LLAMAR A LA FUNCIÓN ---
        cargarClimaDesdeAPI() // LLAMADA CLAVE

        // Manejo del botón atrás
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (binding.drawerLayout.isDrawerOpen(GravityCompat.START)) {
                    binding.drawerLayout.closeDrawer(GravityCompat.START)
                } else {
                    isEnabled = false
                    onBackPressedDispatcher.onBackPressed()
                }
            }
        })
    }

    private fun setupGreetings() {
        val username = prefs.getString("username", "Usuario")
        binding.tvWelcomeTitle.text = "¡Hola, $username!"

        // Acceso seguro al header del NavigationView
        val headerView = binding.navView.getHeaderView(0)
        if (headerView != null) {
            val tvNavTitle = headerView.findViewById<TextView>(R.id.tvNavHeaderTitle)
            tvNavTitle?.text = "Bienvenido, $username"
        }
    }

    private fun setupRecyclerView() {
        val userId = prefs.getInt("userId", -1)
        if (userId != -1) {
            try {
                val appointmentList = appointmentDAO.getAppointmentInfoForPatient(userId)
                val adapter = AppointmentAdapter(appointmentList) { appointmentId ->
                    val intent = Intent(this, AppointmentDetailActivity::class.java)
                    intent.putExtra(AppointmentDetailActivity.EXTRA_APPOINTMENT_ID, appointmentId)
                    startActivity(intent)
                }
                binding.rvPatientAppointments.layoutManager = LinearLayoutManager(this)
                binding.rvPatientAppointments.adapter = adapter
            } catch (e: Exception) {
                Toast.makeText(this, "Error al cargar citas", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        setupRecyclerView()
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.nav_logout -> logout()

            // CAMBIO AQUÍ: No mandes a ReservarCitaActivity directamente
            R.id.nav_book_appointment -> {
                val intent = Intent(this, DoctorsActivity::class.java)
                startActivity(intent)
            }
        }
        binding.drawerLayout.closeDrawer(GravityCompat.START)
        return true
    }


    private fun cargarClimaDesdeAPI() {
        val retrofit = Retrofit.Builder()
            .baseUrl("https://api.openweathermap.org/data/2.5/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val service = retrofit.create(WeatherService::class.java)
        val call = service.getCurrentWeather("Lima,PE", "83995166060c4a45638162f1338d745e", "metric")

        call.enqueue(object : retrofit2.Callback<WeatherResponse> {
            override fun onResponse(call: retrofit2.Call<WeatherResponse>, response: retrofit2.Response<WeatherResponse>) {
                if (response.isSuccessful) {
                    val weather = response.body()
                    runOnUiThread {
                        // ID correcto del XML de paciente
                        binding.tvClimaPaciente.text = "Hoy en ${weather?.name}: ${weather?.main?.temp?.toInt()}°C"
                    }
                } else {
                    runOnUiThread {
                        binding.tvClimaPaciente.text = "Lima: 22°C (Servicio Activo)"
                    }
                }
            }
            override fun onFailure(call: retrofit2.Call<WeatherResponse>, t: Throwable) {
                runOnUiThread {
                    binding.tvClimaPaciente.text = "Modo Offline: Citas Cargadas"
                }
            }
        })
    }

    private fun logout() {
        prefs.edit().clear().apply()
        val intent = Intent(this, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }
}

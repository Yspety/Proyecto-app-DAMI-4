package com.cibertec.clinicacitas

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.cibertec.clinicacitas.DAO.AppointmentDAO
import com.cibertec.clinicacitas.DAO.DoctorDAO
import com.cibertec.clinicacitas.Entidades.AppointmentInfo
import com.cibertec.clinicacitas.databinding.ActivityMedicoHomeBinding
import com.google.android.material.navigation.NavigationView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class MedicoHomeActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    private lateinit var binding: ActivityMedicoHomeBinding
    private lateinit var appointmentDAO: AppointmentDAO
    private lateinit var doctorDAO: DoctorDAO
    private var listaCompletaCitas: List<AppointmentInfo> = listOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMedicoHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        appointmentDAO = AppointmentDAO(this)
        doctorDAO = DoctorDAO(this)

        setSupportActionBar(binding.toolbar)
        supportActionBar?.title = "Gestión Médica"

        val toggle = ActionBarDrawerToggle(
            this, binding.drawerLayoutMedico, binding.toolbar,
            R.string.navigation_drawer_open, R.string.navigation_drawer_close
        )
        binding.drawerLayoutMedico.addDrawerListener(toggle)
        toggle.syncState()

        binding.navViewMedico.setNavigationItemSelectedListener(this)

        // Datos de sesión
        val prefs = getSharedPreferences("session", MODE_PRIVATE)
        val nombre = prefs.getString("username", "Doctor")
        binding.tvWelcomeMedico.text = "¡Buen día, Dr. $nombre!"

        // --- SOLUCIÓN AL ERROR DEL HEADER (ACCESO SEGURO) ---
        val headerView = binding.navViewMedico.getHeaderView(0)

        // Buscamos el título por ID
        val tvHeaderTitle = headerView.findViewById<TextView>(R.id.tvNavHeaderTitle)
        tvHeaderTitle?.text = "Dr. $nombre"

        // Como el subtítulo no tiene ID en tu XML, lo buscamos por su posición (índice 1)
        try {
            val layoutContenedor = headerView as? ViewGroup
            val tvHeaderSubtitle = layoutContenedor?.getChildAt(1) as? TextView
            tvHeaderSubtitle?.text = "Portal Médico"
        } catch (e: Exception) {
            // Si algo falla, el programa no se cae, solo no cambia el texto
        }

        // Listener para los Filtros (Chips)
        binding.chipGroupFiltros.setOnCheckedChangeListener { _, checkedId ->
            aplicarFiltro(checkedId)
        }

        // --- LLAMADA AL CLIMA PARA EL MÉDICO ---
        cargarClimaDesdeAPI()

        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (binding.drawerLayoutMedico.isDrawerOpen(GravityCompat.START)) {
                    binding.drawerLayoutMedico.closeDrawer(GravityCompat.START)
                } else {
                    isEnabled = false
                    onBackPressedDispatcher.onBackPressed()
                }
            }
        })
    }

    override fun onResume() {
        super.onResume()
        actualizarInterfazCompleta()
    }

    private fun actualizarInterfazCompleta() {
        val prefs = getSharedPreferences("session", MODE_PRIVATE)
        val userId = prefs.getInt("userId", -1)

        val doctor = doctorDAO.findDoctorByUserId(userId)
        if (doctor != null) {
            listaCompletaCitas = appointmentDAO.getAppointmentInfoForDoctor(doctor.id)

            // Actualizar contador del CardView
            val today = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
            val citasHoy = listaCompletaCitas.count { it.date == today && it.status.lowercase() == "programada" }
            binding.tvCountCitasHoy.text = "Citas de hoy: $citasHoy"

            // Refrescar lista con el filtro actual
            aplicarFiltro(binding.chipGroupFiltros.checkedChipId)
        } else {
            Toast.makeText(this, "Error: No se encontró perfil médico", Toast.LENGTH_SHORT).show()
        }
    }

    private fun aplicarFiltro(chipId: Int) {
        val listaFiltrada = when (chipId) {
            R.id.chipPendientes -> listaCompletaCitas.filter { it.status.lowercase() == "programada" }
            R.id.chipHistorial -> listaCompletaCitas.filter { it.status.lowercase() != "programada" }
            else -> listaCompletaCitas // Caso chipTodos
        }

        val adapter = AppointmentAdapter(listaFiltrada) { appointmentId ->
            val intent = Intent(this, AppointmentDetailActivity::class.java)
            intent.putExtra(AppointmentDetailActivity.EXTRA_APPOINTMENT_ID, appointmentId)
            startActivity(intent)
        }
        binding.rvMedicoAppointments.layoutManager = LinearLayoutManager(this)
        binding.rvMedicoAppointments.adapter = adapter
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.nav_medico_home -> {
                // Ya estamos aquí, solo cerramos el menú
            }
            R.id.nav_medico_agenda -> {
                // Llamamos a la nueva función creativa
                mostrarResumenGuardia()
            }
            R.id.nav_logout -> logout()
        }
        binding.drawerLayoutMedico.closeDrawer(GravityCompat.START)
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
                val prefs = getSharedPreferences("session", MODE_PRIVATE)
                val nombre = prefs.getString("username", "Doctor")

                if (response.isSuccessful) {
                    val weather = response.body()
                    runOnUiThread {
                        binding.tvWelcomeMedico.text = "¡Buen día, Dr. $nombre! Clima: ${weather?.main?.temp?.toInt()}°C"
                    }
                } else {
                    runOnUiThread {
                        binding.tvWelcomeMedico.text = "¡Buen día, Dr. $nombre! (22°C)"
                    }
                }
            }
            override fun onFailure(call: retrofit2.Call<WeatherResponse>, t: Throwable) {
                // No hacemos nada o ponemos un texto base para no romper la estética
            }
        })
    }

    private fun mostrarResumenGuardia() {
        val totalCitas = listaCompletaCitas.size
        val pendientes = listaCompletaCitas.count { it.status.lowercase() == "programada" }

        // Determinamos un mensaje según la carga de trabajo
        val estadoCarga = when {
            pendientes > 5 -> "Carga Alta - Priorizar Urgencias"
            pendientes > 0 -> "Carga Moderada"
            else -> "Sin pacientes pendientes"
        }

        val mensaje = """
        🏥 Estado: Activo
        📅 Jornada: Mañana / Tarde
        👨‍⚕️ Pacientes Totales: $totalCitas
        ⏳ Pendientes hoy: $pendientes
        
        Nota: $estadoCarga
    """.trimIndent()

        androidx.appcompat.app.AlertDialog.Builder(this)
            .setTitle("📋 Mi Agenda de Guardia")
            .setMessage(mensaje)
            .setPositiveButton("Entendido", null)
            .setIcon(android.R.drawable.ic_menu_myplaces)
            .show()
    }

    private fun logout() {
        val prefs = getSharedPreferences("session", MODE_PRIVATE)
        prefs.edit().clear().apply()
        val intent = Intent(this, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }
}
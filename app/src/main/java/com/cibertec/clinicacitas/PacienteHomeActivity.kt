package com.cibertec.clinicacitas

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.MenuItem
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.cibertec.clinicacitas.DAO.AppointmentDAO
import com.cibertec.clinicacitas.databinding.ActivityPacienteHomeBinding
import com.google.android.material.navigation.NavigationView

// Import revertido
import com.cibertec.clinicacitas.AppointmentAdapter

class PacienteHomeActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    private lateinit var binding: ActivityPacienteHomeBinding
    private lateinit var appointmentDAO: AppointmentDAO
    private lateinit var prefs: SharedPreferences
    private var currentPatientName: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPacienteHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        appointmentDAO = AppointmentDAO(this)
        prefs = getSharedPreferences("session", MODE_PRIVATE)
        currentPatientName = prefs.getString("username", null)

        setSupportActionBar(binding.toolbar)
        binding.toolbar.title = "Portal del Paciente"

        val toggle = ActionBarDrawerToggle(
            this, binding.drawerLayout, binding.toolbar,
            R.string.navigation_drawer_open, R.string.navigation_drawer_close
        )
        binding.drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        binding.navView.setNavigationItemSelectedListener(this)

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

        setupRecyclerView()
    }

    override fun onResume() {
        super.onResume()
        setupRecyclerView()
    }

    private fun setupRecyclerView() {
        if (currentPatientName != null) {
            val appointmentList = appointmentDAO.getAppointmentInfoForPatient(currentPatientName!!)
            val adapter = AppointmentAdapter(appointmentList) { appointmentId ->
                startActivity(Intent(this, AppointmentDetailActivity::class.java).apply {
                    putExtra(AppointmentDetailActivity.EXTRA_APPOINTMENT_ID, appointmentId)
                })
            }
            binding.rvPatientAppointments.layoutManager = LinearLayoutManager(this)
            binding.rvPatientAppointments.adapter = adapter
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.nav_my_appointments -> binding.drawerLayout.closeDrawer(GravityCompat.START)
            R.id.nav_book_appointment -> startActivity(Intent(this, ReservarCitaActivity::class.java))
            R.id.nav_logout -> logout()
        }
        binding.drawerLayout.closeDrawer(GravityCompat.START)
        return true
    }

    private fun logout() {
        prefs.edit().clear().apply()
        val intent = Intent(this, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }
}

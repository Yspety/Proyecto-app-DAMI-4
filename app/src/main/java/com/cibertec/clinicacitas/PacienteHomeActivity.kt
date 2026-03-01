package com.cibertec.clinicacitas

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.cibertec.clinicacitas.DAO.AppointmentDAO
import com.cibertec.clinicacitas.Entidades.AppointmentInfo
import com.cibertec.clinicacitas.databinding.ActivityPacienteHomeBinding
import com.google.android.material.navigation.NavigationView

class PacienteHomeActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    private lateinit var binding: ActivityPacienteHomeBinding
    private lateinit var appointmentDAO: AppointmentDAO
    private var currentPatientName: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPacienteHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        appointmentDAO = AppointmentDAO(this)
        currentPatientName = SessionStore.currentUser?.username

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
        if (SessionStore.currentUser == null) {
            SessionStore.logout(this)
            return
        }
        setupRecyclerView()
    }

    private fun setupRecyclerView() {
        if (currentPatientName != null) {
            val appointmentList = appointmentDAO.getAppointmentInfoForPatient(currentPatientName!!)
            val adapter = AppointmentAdapter(appointmentList,
                onClick = { appointment ->
                    startActivity(Intent(this, AppointmentDetailActivity::class.java).apply {
                        putExtra(AppointmentDetailActivity.EXTRA_APPOINTMENT_ID, appointment.appointmentId)
                    })
                },
                onCancelClick = { appointment ->
                    showCancelConfirmationDialog(appointment)
                }
            )
            binding.rvPatientAppointments.layoutManager = LinearLayoutManager(this)
            binding.rvPatientAppointments.adapter = adapter
        }
    }

    private fun showCancelConfirmationDialog(appointment: AppointmentInfo) {
        AlertDialog.Builder(this)
            .setTitle("Cancelar Cita")
            .setMessage("¿Estás seguro de que deseas cancelar esta cita?")
            .setPositiveButton("Sí, Cancelar") { _, _ ->
                appointmentDAO.cancelAppointment(appointment.appointmentId)
                Toast.makeText(this, "Cita cancelada con éxito", Toast.LENGTH_SHORT).show()
                setupRecyclerView() // Recargar la lista
            }
            .setNegativeButton("No", null)
            .show()
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.nav_my_appointments -> binding.drawerLayout.closeDrawer(GravityCompat.START)
            R.id.nav_book_appointment -> startActivity(Intent(this, ReservarCitaActivity::class.java))
            R.id.nav_logout -> SessionStore.logout(this)
        }
        binding.drawerLayout.closeDrawer(GravityCompat.START)
        return true
    }
}

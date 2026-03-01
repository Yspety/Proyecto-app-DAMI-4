package com.cibertec.clinicacitas.UI

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
import com.cibertec.clinicacitas.UI_adapter.AppointmentAdapter
import com.cibertec.clinicacitas.DAO.AppointmentDAO
import com.cibertec.clinicacitas.DAO.DoctorDAO
import com.cibertec.clinicacitas.Entidades.AppointmentInfo
import com.cibertec.clinicacitas.R
import com.cibertec.clinicacitas.Utils.SessionStore
import com.cibertec.clinicacitas.databinding.ActivityDoctorHomeBinding
import com.google.android.material.navigation.NavigationView

class DoctorHomeActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    private lateinit var binding: ActivityDoctorHomeBinding
    private lateinit var appointmentDAO: AppointmentDAO
    private lateinit var doctorDAO: DoctorDAO
    private var currentDoctorId: Int? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDoctorHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        appointmentDAO = AppointmentDAO(this)
        doctorDAO = DoctorDAO(this)

        val currentUserId = SessionStore.currentUser?.id
        if (currentUserId == null) {
            SessionStore.logout(this)
            return
        }

        val doctor = doctorDAO.getDoctorByUserId(currentUserId)
        if (doctor == null) {
            Toast.makeText(this, "Error crítico: No se encontró el perfil de doctor.", Toast.LENGTH_LONG).show()
            SessionStore.logout(this)
            return
        }
        currentDoctorId = doctor.id

        setSupportActionBar(binding.toolbar)
        binding.toolbar.title = "Portal del Médico"

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
        if (currentDoctorId != null) {
            val appointmentList = appointmentDAO.getAppointmentInfoForDoctor(currentDoctorId!!)
            val adapter = AppointmentAdapter(
                appointmentList,
                onClick = { appointment ->
                    startActivity(Intent(this, AppointmentDetailActivity::class.java).apply {
                        putExtra(
                            AppointmentDetailActivity.EXTRA_APPOINTMENT_ID,
                            appointment.appointmentId
                        )
                    })
                },
                onCancelClick = { appointment ->
                    showCancelConfirmationDialog(appointment)
                }
            )
            binding.rvDoctorAppointments.layoutManager = LinearLayoutManager(this)
            binding.rvDoctorAppointments.adapter = adapter
        }
    }

    private fun showCancelConfirmationDialog(appointment: AppointmentInfo) {
        AlertDialog.Builder(this)
            .setTitle("Cancelar Cita")
            .setMessage("¿Estás seguro de que deseas cancelar esta cita?")
            .setPositiveButton("Sí, Cancelar") { _, _ ->
                appointmentDAO.cancelAppointment(appointment.appointmentId)
                Toast.makeText(this, "Cita cancelada con éxito", Toast.LENGTH_SHORT).show()
                setupRecyclerView()
            }
            .setNegativeButton("No", null)
            .show()
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.nav_my_appointments_doctor -> binding.drawerLayout.closeDrawer(GravityCompat.START)
            R.id.nav_logout_doctor -> SessionStore.logout(this)
        }
        binding.drawerLayout.closeDrawer(GravityCompat.START)
        return true
    }
}

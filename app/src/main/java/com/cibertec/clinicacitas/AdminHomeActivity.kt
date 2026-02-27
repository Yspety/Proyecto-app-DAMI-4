package com.cibertec.clinicacitas

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.cibertec.clinicacitas.DAO.UsuarioDAO
import com.cibertec.clinicacitas.databinding.ActivityAdminHomeBinding
import com.google.android.material.navigation.NavigationView

// Import revertido
import com.cibertec.clinicacitas.UserAdapter

class AdminHomeActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    private lateinit var binding: ActivityAdminHomeBinding
    private lateinit var usuarioDAO: UsuarioDAO
    private lateinit var prefs: SharedPreferences
    private var currentUserRole: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAdminHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        usuarioDAO = UsuarioDAO(this)
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

        setupUIForRole()

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

    private fun setupUIForRole() {
        val menu = binding.navView.menu

        if (currentUserRole == "medico") {
            binding.toolbar.title = "Portal de Médico"
            binding.rvUsers.visibility = View.GONE

            menu.findItem(R.id.nav_users).isVisible = false
            menu.findItem(R.id.nav_doctors).isVisible = false
            menu.findItem(R.id.nav_register_user).isVisible = false
        } else {
            binding.toolbar.title = "Administración"
            binding.rvUsers.visibility = View.VISIBLE
            setupRecyclerView()
        }
    }

    override fun onResume() {
        super.onResume()
        if (currentUserRole == "admin") {
            setupRecyclerView()
        }
    }

    private fun setupRecyclerView() {
        val userList = usuarioDAO.getAllUsers()
        val userAdapter = UserAdapter(userList)
        binding.rvUsers.apply {
            layoutManager = LinearLayoutManager(this@AdminHomeActivity)
            adapter = userAdapter
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.nav_doctors -> startActivity(Intent(this, DoctorsActivity::class.java))
            R.id.nav_appointments -> {
                val intent = Intent(this, AppointmentsActivity::class.java)
                if (currentUserRole == "medico") {
                    val username = prefs.getString("username", null)
                    val doctorFullName = when(username) {
                        "medico" -> "Dr. Luis García"
                        else -> ""
                    }
                    intent.putExtra(AppointmentsActivity.EXTRA_MODE, AppointmentsActivity.MODE_DOCTOR)
                    intent.putExtra(AppointmentsActivity.EXTRA_DOCTOR_NAME, doctorFullName)
                } else {
                    intent.putExtra(AppointmentsActivity.EXTRA_MODE, AppointmentsActivity.MODE_ADMIN)
                }
                startActivity(intent)
            }
            R.id.nav_register_user -> startActivity(Intent(this, RegistroUsuarioActivity::class.java))
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

package com.cibertec.clinicacitas.UI

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.cibertec.clinicacitas.DAO.UsuarioDAO
import com.cibertec.clinicacitas.Entidades.Usuario
import com.cibertec.clinicacitas.R
import com.cibertec.clinicacitas.Utils.SessionStore
import com.cibertec.clinicacitas.UI_adapter.UserAdapter
import com.cibertec.clinicacitas.databinding.ActivityAdminHomeBinding
import com.google.android.material.navigation.NavigationView

class AdminHomeActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    private lateinit var binding: ActivityAdminHomeBinding
    private lateinit var usuarioDAO: UsuarioDAO

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAdminHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        usuarioDAO = UsuarioDAO(this)

        setSupportActionBar(binding.toolbar)
        binding.toolbar.title = "Administración"

        val toggle = ActionBarDrawerToggle(
            this, binding.drawerLayout, binding.toolbar,
            R.string.navigation_drawer_open, R.string.navigation_drawer_close
        )
        binding.drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        binding.navView.setNavigationItemSelectedListener(this)

        setupRecyclerView()

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

    override fun onResume() {
        super.onResume()
        if (SessionStore.currentUser == null) {
            SessionStore.logout(this)
            return
        }
        setupRecyclerView()
    }

    private fun setupRecyclerView() {
        val userList = usuarioDAO.getAllUsers()
        val userAdapter = UserAdapter(
            userList,
            onEditClick = { user -> showEditRoleDialog(user) },
            onDeleteClick = { user -> showDeleteConfirmationDialog(user) }
        )
        binding.rvUsers.apply {
            layoutManager = LinearLayoutManager(this@AdminHomeActivity)
            adapter = userAdapter
        }
    }

    private fun showEditRoleDialog(user: Usuario) {
        val roles = arrayOf("admin", "medico", "usuario")
        val currentRoleIndex = roles.indexOf(user.rol)

        AlertDialog.Builder(this)
            .setTitle("Editar Rol")
            .setSingleChoiceItems(roles, currentRoleIndex) { dialog, which ->
                val selectedRole = roles[which]
                usuarioDAO.updateUserRole(user.id, selectedRole)
                dialog.dismiss()
                setupRecyclerView() // Recargar la lista
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    private fun showDeleteConfirmationDialog(user: Usuario) {
        AlertDialog.Builder(this)
            .setTitle("Eliminar Usuario")
            .setMessage("¿Estás seguro de que deseas eliminar a '${user.username}'? Esta acción no se puede deshacer.")
            .setPositiveButton("Eliminar") { _, _ ->
                usuarioDAO.deleteUser(user.id)
                setupRecyclerView() // Recargar la lista
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.nav_doctors -> startActivity(Intent(this, DoctorsActivity::class.java))
            // CORREGIDO: El botón ahora abre la nueva AppointmentsActivity
            R.id.nav_appointments -> startActivity(Intent(this, AppointmentsActivity::class.java))
            R.id.nav_register_user -> startActivity(
                Intent(
                    this,
                    RegistroUsuarioActivity::class.java
                )
            )
            R.id.nav_logout -> SessionStore.logout(this)
        }
        binding.drawerLayout.closeDrawer(GravityCompat.START)
        return true
    }
}
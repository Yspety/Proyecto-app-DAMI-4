package com.cibertec.clinicacitas

import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import com.cibertec.clinicacitas.DAO.DoctorDAO
import com.cibertec.clinicacitas.DAO.EspecialidadDAO
import com.cibertec.clinicacitas.DAO.UsuarioDAO
import com.cibertec.clinicacitas.Entidades.Doctor
import com.cibertec.clinicacitas.Entidades.Especialidad
import com.cibertec.clinicacitas.databinding.ActivityRegistroUsuarioBinding

class RegistroUsuarioActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegistroUsuarioBinding
    private lateinit var usuarioDAO: UsuarioDAO
    private lateinit var doctorDAO: DoctorDAO
    private lateinit var specialtyDAO: EspecialidadDAO
    private var specialties: List<Especialidad> = listOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegistroUsuarioBinding.inflate(layoutInflater)
        setContentView(binding.root)

        usuarioDAO = UsuarioDAO(this)
        doctorDAO = DoctorDAO(this)
        specialtyDAO = EspecialidadDAO(this)

        setupSpinners()
        setupRoleSelectionListener()

        binding.btnGuardar.setOnClickListener {
            performValidationAndSave()
        }
    }

    private fun setupSpinners() {
        ArrayAdapter.createFromResource(
            this, R.array.roles_array, android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            binding.spRol.adapter = adapter
        }

        specialties = specialtyDAO.getAllSpecialties()
        val specialtyNames = specialties.map { it.name }
        val specialtyAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, specialtyNames)
        specialtyAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spSpecialty.adapter = specialtyAdapter
    }

    private fun setupRoleSelectionListener() {
        binding.spRol.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val selectedRole = parent?.getItemAtPosition(position).toString()
                binding.doctorFieldsContainer.isVisible = selectedRole.equals("Médico", ignoreCase = true)
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
    }

    private fun performValidationAndSave() {
        val username = binding.etUsernameFormRegistro.text.toString().trim()
        val password = binding.etPasswordFormRegistro.text.toString().trim()
        val selectedRole = binding.spRol.selectedItem.toString()

        if (username.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Por favor, complete el usuario y la contraseña", Toast.LENGTH_SHORT).show()
            return
        }

        // CORRECCIÓN FINAL: Llamando al método correcto 'registrarUsuario' (con 'r' minúscula)
        val newUserId = usuarioDAO.registrarUsuario(username, password, selectedRole)

        if (newUserId <= 0) {
            Toast.makeText(this, "Error al registrar el usuario. El username podría ya existir.", Toast.LENGTH_LONG).show()
            return
        }

        if (selectedRole.equals("Médico", ignoreCase = true)) {
            saveDoctorData(newUserId.toInt())
        } else {
            Toast.makeText(this, "Usuario registrado con éxito", Toast.LENGTH_SHORT).show()
            finish()
        }
    }

    private fun saveDoctorData(userId: Int) {
        val fullName = binding.etFullName.text.toString().trim()
        val cmp = binding.etCmp.text.toString().trim()
        val room = binding.etRoom.text.toString().trim()

        if (binding.spSpecialty.selectedItem == null) {
            Toast.makeText(this, "No hay especialidades disponibles.", Toast.LENGTH_SHORT).show()
            return
        }
        val selectedSpecialty = specialties[binding.spSpecialty.selectedItemPosition]

        if (fullName.isEmpty() || cmp.isEmpty() || room.isEmpty()) {
            Toast.makeText(this, "Por favor, complete todos los datos del médico", Toast.LENGTH_SHORT).show()
            return
        }

        val newDoctor = Doctor(
            id = 0,
            usuarioId = userId,
            fullName = fullName,
            specialtyId = selectedSpecialty.id,
            cmp = cmp,
            room = room
        )

        val doctorResult = doctorDAO.addDoctor(newDoctor)

        if (doctorResult > 0) {
            Toast.makeText(this, "Médico registrado con éxito", Toast.LENGTH_SHORT).show()
            finish()
        } else {
            Toast.makeText(this, "Error al registrar los datos del médico.", Toast.LENGTH_LONG).show()
        }
    }
}

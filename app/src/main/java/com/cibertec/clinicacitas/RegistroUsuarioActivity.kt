package com.cibertec.clinicacitas

import android.app.DatePickerDialog
import android.icu.util.Calendar
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Patterns
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
import com.cibertec.clinicacitas.Entidades.Usuario
import com.cibertec.clinicacitas.databinding.ActivityRegistroUsuarioBinding

class RegistroUsuarioActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegistroUsuarioBinding
    private lateinit var usuarioDAO: UsuarioDAO
    private lateinit var doctorDAO: DoctorDAO
    private lateinit var specialtyDAO: EspecialidadDAO
    private var specialties: List<Especialidad> = listOf()

    private var esModoPaciente: Boolean = false
    private var doctorIdParaEditar: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegistroUsuarioBinding.inflate(layoutInflater)
        setContentView(binding.root)

        usuarioDAO = UsuarioDAO(this)
        doctorDAO = DoctorDAO(this)
        specialtyDAO = EspecialidadDAO(this)

        esModoPaciente = intent.getBooleanExtra("MODO_PACIENTE", false)
        doctorIdParaEditar = intent.getIntExtra("EXTRA_DOCTOR_ID", -1)

        setupSpinners()
        // Ocultamos el campo de fecha ya que no existe en tu BD actual
        binding.etFechaNacimiento.visibility = View.GONE
        binding.etTelefonoForm.visibility = View.GONE

        setupRoleSelectionListener()
        setupUI()

        binding.btnGuardar.setOnClickListener {
            if (doctorIdParaEditar != -1) {
                actualizarDoctor()
            } else {
                performValidationAndSave()
            }
        }
    }

    private fun setupUI() {
        if (esModoPaciente) {
            binding.tvTituloRegistro.text = "Registro de Paciente"
            binding.spRol.visibility = View.GONE
            binding.doctorFieldsContainer.visibility = View.GONE
            binding.userFieldsContainer.visibility = View.VISIBLE
        } else if (doctorIdParaEditar != -1) {
            binding.tvTituloRegistro.text = "Editar Médico"
            binding.btnGuardar.text = "ACTUALIZAR DATOS"
            binding.spRol.visibility = View.GONE
            binding.userFieldsContainer.visibility = View.VISIBLE
            binding.doctorFieldsContainer.visibility = View.VISIBLE
            binding.etDniForm.isEnabled = false
            cargarDatosParaEdicion()
        } else {
            binding.tvTituloRegistro.text = "Registro de Usuario"
            binding.spRol.visibility = View.VISIBLE
            binding.doctorFieldsContainer.visibility = View.GONE
        }
    }

    private fun cargarDatosParaEdicion() {
        val data = doctorDAO.getDoctorFullDetailsById(doctorIdParaEditar)
        if (data != null) {
            binding.etFullNameForm.setText(data["fullName"])
            binding.etCmp.setText(data["cmp"])
            binding.etRoom.setText(data["room"])
            binding.etDniForm.setText(data["dni"])
            binding.etEmailForm.setText(data["email"])
            binding.etUsernameFormRegistro.setText(data["username"])

            binding.spSpecialty.post {
                val specIdStr = data["specialtyId"]
                val specId = specIdStr?.toIntOrNull() ?: -1
                val index = specialties.indexOfFirst { it.id == specId }
                if (index != -1) binding.spSpecialty.setSelection(index)
            }
        } else {
            Toast.makeText(this, "No se pudieron obtener datos del médico", Toast.LENGTH_SHORT).show()
        }
    }

    private fun actualizarDoctor() {
        val name = binding.etFullNameForm.text.toString().trim()
        val cmp = binding.etCmp.text.toString().trim()
        val room = binding.etRoom.text.toString().trim()
        val user = binding.etUsernameFormRegistro.text.toString().trim()
        val email = binding.etEmailForm.text.toString().trim()
        val pass = binding.etPasswordFormRegistro.text.toString().trim()

        if (name.isEmpty() || cmp.isEmpty() || user.isEmpty() || email.isEmpty()) {
            Toast.makeText(this, "Completa los campos obligatorios", Toast.LENGTH_SHORT).show()
            return
        }

        // 1. Obtener datos actuales para saber a quién ignorar en la validación
        val medicoBase = doctorDAO.findDoctorById(doctorIdParaEditar) ?: return
        val idUsuarioActual = medicoBase.usuarioId

        // 2. VALIDAR DUPLICADOS (CMP, USERNAME, EMAIL)
        val db = com.cibertec.clinicacitas.DataBase.AppDBHelper(this).readableDatabase

        // Validar CMP en otros médicos
        val cursorCmp = db.rawQuery("SELECT id FROM doctor WHERE cmp = ? AND id != ?",
            arrayOf(cmp, doctorIdParaEditar.toString()))
        if (cursorCmp.count > 0) {
            Toast.makeText(this, "Error: El CMP ya está registrado en otro médico", Toast.LENGTH_SHORT).show()
            cursorCmp.close()
            return
        }
        cursorCmp.close()

        // Validar Usuario o Email en otras cuentas
        val cursorUser = db.rawQuery("SELECT id FROM usuario WHERE (username = ? OR email = ?) AND id != ?",
            arrayOf(user, email, idUsuarioActual.toString()))
        if (cursorUser.count > 0) {
            Toast.makeText(this, "Error: El nombre de usuario o email ya están en uso", Toast.LENGTH_SHORT).show()
            cursorUser.close()
            return
        }
        cursorUser.close()

        // 3. Si todo está limpio, procedemos a actualizar
        val selectedPos = binding.spSpecialty.selectedItemPosition
        val specId = if (selectedPos != -1) specialties[selectedPos].id else -1

        val doc = Doctor(doctorIdParaEditar, idUsuarioActual, name, specId, cmp, room)
        if (doctorDAO.updateDoctor(doc) > 0) {
            // Esta función ya tiene su validación interna pero con la de arriba ya estamos blindados
            usuarioDAO.updateUsuarioSimple(idUsuarioActual, user, email, name, pass)
            Toast.makeText(this, "Médico actualizado correctamente", Toast.LENGTH_SHORT).show()
            finish()
        }
    }

    private fun setupSpinners() {
        val roles = arrayOf("Administrador", "Médico")
        val adapterRol = ArrayAdapter(this, android.R.layout.simple_spinner_item, roles)
        adapterRol.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spRol.adapter = adapterRol

        specialties = specialtyDAO.getAllSpecialties()
        val adapterSpec = ArrayAdapter(this, android.R.layout.simple_spinner_item, specialties.map { it.name })
        adapterSpec.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spSpecialty.adapter = adapterSpec
    }

    private fun setupRoleSelectionListener() {
        binding.spRol.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, pos: Int, p3: Long) {
                if (doctorIdParaEditar == -1 && !esModoPaciente) {
                    binding.doctorFieldsContainer.visibility = if (pos == 1) View.VISIBLE else View.GONE
                }
            }
            override fun onNothingSelected(p0: AdapterView<*>?) {}
        }
    }

    private fun performValidationAndSave() {
        val user = binding.etUsernameFormRegistro.text.toString().trim()
        val email = binding.etEmailForm.text.toString().trim()
        val dni = binding.etDniForm.text.toString().trim()
        val pass = binding.etPasswordFormRegistro.text.toString().trim()
        val name = binding.etFullNameForm.text.toString().trim()
        val rol = if (esModoPaciente) "paciente" else if (binding.spRol.selectedItemPosition == 1) "medico" else "admin"

        // 1. Validar campos vacíos básicos
        if (user.isEmpty() || email.isEmpty() || dni.isEmpty() || pass.isEmpty()) {
            Toast.makeText(this, "Por favor, completa los campos obligatorios", Toast.LENGTH_SHORT).show()
            return
        }

        // 2. Validaciones de Duplicados en Usuario
        val todosLosUsuarios = usuarioDAO.getAllUsers() // Asegúrate que este método traiga DNI y Email también
        if (todosLosUsuarios.any { it.username == user }) {
            Toast.makeText(this, "El nombre de usuario ya existe", Toast.LENGTH_SHORT).show()
            return
        }

        // 3. Validaciones específicas para Médicos (CMP y Consultorio)
        if (rol == "medico") {
            val cmp = binding.etCmp.text.toString().trim()
            val room = binding.etRoom.text.toString().trim()

            if (cmp.isEmpty() || room.isEmpty()) {
                Toast.makeText(this, "CMP y Consultorio son obligatorios para médicos", Toast.LENGTH_SHORT).show()
                return
            }

            if (doctorDAO.checkCmpExists(cmp)) {
                Toast.makeText(this, "El CMP ya está registrado por otro médico", Toast.LENGTH_SHORT).show()
                return
            }
        }

        // 4. Intento de registro
        val uid = usuarioDAO.registrarUsuarioCompleto(user, email, pass, rol, dni, name, "", "")
        if (uid > 0) {
            if (rol == "medico") {
                val cmp = binding.etCmp.text.toString().trim()
                val room = binding.etRoom.text.toString().trim()
                val pos = binding.spSpecialty.selectedItemPosition
                val specId = if (pos != -1) specialties[pos].id else -1
                doctorDAO.addDoctor(Doctor(0, uid.toInt(), name, specId, cmp, room))
            }
            Toast.makeText(this, "Registrado exitosamente", Toast.LENGTH_SHORT).show()
            finish()
        } else {
            Toast.makeText(this, "Error: El DNI o Correo ya están en uso", Toast.LENGTH_LONG).show()
        }
    }
}
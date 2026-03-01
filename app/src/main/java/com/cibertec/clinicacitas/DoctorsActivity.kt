package com.cibertec.clinicacitas

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.cibertec.clinicacitas.DAO.DoctorDAO
import com.cibertec.clinicacitas.DAO.EspecialidadDAO
import com.cibertec.clinicacitas.Entidades.DoctorInfo
import com.cibertec.clinicacitas.databinding.ActivityDoctorsBinding

class DoctorsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDoctorsBinding
    private lateinit var doctorDAO: DoctorDAO
    private lateinit var especialidadDAO: EspecialidadDAO

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDoctorsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        doctorDAO = DoctorDAO(this)
        especialidadDAO = EspecialidadDAO(this)

        binding.toolbar.title = "Médicos"
        binding.toolbar.setNavigationOnClickListener { finish() }

        binding.btnAddDoctor.setOnClickListener {
            startActivity(Intent(this, RegistroUsuarioActivity::class.java))
        }

        setupRecyclerView()
    }

    override fun onResume() {
        super.onResume()
        setupRecyclerView()
    }

    private fun setupRecyclerView() {
        val doctorList = doctorDAO.getAllDoctorInfo()

        val adapter = DoctorAdapter(doctorList,
            onItemClick = { doctor ->
                val i = Intent(this, DoctorDetailActivity::class.java).apply {
                    putExtra(DoctorDetailActivity.EXTRA_DOCTOR_ID, doctor.doctorId)
                }
                startActivity(i)
            },
            onEditClick = { doctor -> showEditDoctorDialog(doctor) },
            onDeleteClick = { doctor -> showDeleteConfirmationDialog(doctor) }
        )

        binding.rvDoctors.layoutManager = LinearLayoutManager(this)
        binding.rvDoctors.adapter = adapter
    }

    private fun showDeleteConfirmationDialog(doctor: DoctorInfo) {
        AlertDialog.Builder(this)
            .setTitle("Eliminar Médico")
            .setMessage("¿Estás seguro de que deseas eliminar a ${doctor.fullName}? Se eliminarán también todas sus citas asociadas.")
            .setPositiveButton("Eliminar") { _, _ ->
                doctorDAO.deleteDoctor(doctor.doctorId)
                Toast.makeText(this, "Médico eliminado con éxito", Toast.LENGTH_SHORT).show()
                setupRecyclerView() // Recargar la lista
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    private fun showEditDoctorDialog(doctor: DoctorInfo) {
        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_edit_doctor, null)
        val etFullName = dialogView.findViewById<EditText>(R.id.etFullName)
        val etCmp = dialogView.findViewById<EditText>(R.id.etCmp)
        val etRoom = dialogView.findViewById<EditText>(R.id.etRoom)
        val spSpecialty = dialogView.findViewById<Spinner>(R.id.spSpecialty)

        etFullName.setText(doctor.fullName)
        etCmp.setText(doctor.cmp)
        etRoom.setText(doctor.room)

        val specialties = especialidadDAO.getAllSpecialties()
        val specialtyNames = specialties.map { it.name }
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, specialtyNames)
        spSpecialty.adapter = adapter

        val currentSpecialtyIndex = specialties.indexOfFirst { it.name == doctor.especialidadNombre }
        if (currentSpecialtyIndex != -1) {
            spSpecialty.setSelection(currentSpecialtyIndex)
        }

        AlertDialog.Builder(this)
            .setTitle("Editar Médico")
            .setView(dialogView)
            .setPositiveButton("Guardar") { _, _ ->
                val newFullName = etFullName.text.toString().trim()
                val newCmp = etCmp.text.toString().trim()
                val newRoom = etRoom.text.toString().trim()
                val newSpecialtyId = specialties[spSpecialty.selectedItemPosition].id

                if (newFullName.isNotEmpty() && newCmp.isNotEmpty() && newRoom.isNotEmpty()) {
                    val updatedDoctor = doctor.copy(
                        fullName = newFullName,
                        cmp = newCmp,
                        room = newRoom,
                        especialidadNombre = specialties[spSpecialty.selectedItemPosition].name
                    )
                    doctorDAO.updateDoctor(updatedDoctor, newSpecialtyId)
                    Toast.makeText(this, "Médico actualizado con éxito", Toast.LENGTH_SHORT).show()
                    setupRecyclerView()
                }
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }
}

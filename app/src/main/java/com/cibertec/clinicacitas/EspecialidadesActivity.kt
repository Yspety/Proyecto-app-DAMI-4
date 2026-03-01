package com.cibertec.clinicacitas

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.cibertec.clinicacitas.DAO.EspecialidadDAO
import com.cibertec.clinicacitas.databinding.ActivityEspecialidadesBinding // Asegúrate de crear el XML respectivo

class EspecialidadesActivity : AppCompatActivity() {

    private lateinit var binding: ActivityEspecialidadesBinding
    private lateinit var specialtyDAO: EspecialidadDAO

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEspecialidadesBinding.inflate(layoutInflater)
        setContentView(binding.root)

        specialtyDAO = EspecialidadDAO(this)

        binding.toolbar.title = "Especialidades"
        setSupportActionBar(binding.toolbar)
        binding.toolbar.setNavigationOnClickListener { finish() }

        val lista = specialtyDAO.getAllSpecialties()

        binding.rvEspecialidades.layoutManager = LinearLayoutManager(this)
        binding.rvEspecialidades.adapter = EspecialidadAdapter(lista) { esp ->
            // AL HACER CLICK: Mandamos a DoctorsActivity con el FILTRO
            val intent = Intent(this, DoctorsActivity::class.java).apply {
                putExtra("EXTRA_ESPECIALIDAD_ID", esp.id)
                putExtra("EXTRA_ESPECIALIDAD_NOMBRE", esp.name)
            }
            startActivity(intent)
        }
    }
}
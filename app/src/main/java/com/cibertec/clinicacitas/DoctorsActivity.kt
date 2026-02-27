package com.cibertec.clinicacitas

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.cibertec.clinicacitas.DAO.DoctorDAO
import com.cibertec.clinicacitas.databinding.ActivityDoctorsBinding

// Import revertido
import com.cibertec.clinicacitas.DoctorAdapter

class DoctorsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDoctorsBinding
    private lateinit var doctorDAO: DoctorDAO

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDoctorsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        doctorDAO = DoctorDAO(this)

        binding.toolbar.title = "Médicos"
        binding.toolbar.setNavigationOnClickListener { finish() }

        val doctorList = doctorDAO.getAllDoctorInfo()

        val adapter = DoctorAdapter(doctorList) { doctorInfo ->
            val i = Intent(this, DoctorDetailActivity::class.java).apply {
                putExtra(DoctorDetailActivity.EXTRA_DOCTOR_ID, doctorInfo.doctorId)
            }
            startActivity(i)
        }

        binding.rvDoctors.layoutManager = LinearLayoutManager(this)
        binding.rvDoctors.adapter = adapter
    }
}

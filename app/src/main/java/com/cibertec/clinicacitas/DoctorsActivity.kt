package com.cibertec.clinicacitas

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.cibertec.clinicacitas.databinding.ActivityDoctorsBinding

class DoctorsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDoctorsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDoctorsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.toolbar.title = "Médicos"
        binding.toolbar.setNavigationOnClickListener { finish() }

        val adapter = DoctorAdapter(FakeData.doctors) { doctor ->
            val i = Intent(this, DoctorDetailActivity::class.java).apply {
                putExtra(DoctorDetailActivity.EXTRA_DOCTOR_ID, doctor.id)
            }
            startActivity(i)
        }

        binding.rvDoctors.layoutManager = LinearLayoutManager(this)
        binding.rvDoctors.adapter = adapter
    }


}

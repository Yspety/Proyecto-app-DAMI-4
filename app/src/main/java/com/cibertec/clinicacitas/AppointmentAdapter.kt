package com.cibertec.clinicacitas

import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.cibertec.clinicacitas.Entidades.AppointmentInfo
import com.cibertec.clinicacitas.databinding.ItemAppointmentBinding

class AppointmentAdapter(
    private val appointmentList: List<AppointmentInfo>,
    private val onItemClick: (Int) -> Unit
) : RecyclerView.Adapter<AppointmentAdapter.AppointmentViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AppointmentViewHolder {
        val binding = ItemAppointmentBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return AppointmentViewHolder(binding)
    }

    override fun onBindViewHolder(holder: AppointmentViewHolder, position: Int) {
        holder.bind(appointmentList[position])
    }

    override fun getItemCount(): Int = appointmentList.size

    inner class AppointmentViewHolder(private val binding: ItemAppointmentBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(appointment: AppointmentInfo) {
            // 1. Textos (Tu lógica original)
            binding.tvCitaTitle.text = "${appointment.date} • ${appointment.time}"
            binding.tvCitaDoctor.text = appointment.doctorName ?: "Médico no asignado"
            binding.tvCitaSpecialty.text = appointment.especialidadNombre ?: "General"
            binding.tvCitaStatusBadge.text = (appointment.status ?: "PENDIENTE").uppercase()

            val context = binding.root.context

            // --- NUEVO: CARGA DE IMAGEN DESDE FIREBASE/API ---
            // Cambia esto solo para probar:
            val fotoUrl = "https://cdn-icons-png.flaticon.com/512/3774/3774299.png"

            Glide.with(context)
                .load(fotoUrl)
                .circleCrop()
                .placeholder(android.R.drawable.ic_menu_report_image) // Un icono estándar de Android
                .error(android.R.drawable.ic_menu_report_image)
                .into(binding.ivDoctorPhoto)

            // 2. Colores (Tu lógica original)
            val colorStatus = when (appointment.status?.lowercase()) {
                "programada" -> ContextCompat.getColor(context, android.R.color.holo_blue_light)
                "completada" -> ContextCompat.getColor(context, android.R.color.holo_green_light)
                "cancelada" -> ContextCompat.getColor(context, android.R.color.holo_red_light)
                else -> Color.GRAY
            }

            // 3. Seguridad de Interfaz (Tu lógica original)
            val background = binding.tvCitaStatusBadge.background
            if (background is GradientDrawable) {
                background.setColor(colorStatus)
            } else {
                binding.tvCitaStatusBadge.setTextColor(colorStatus)
            }

            // 4. Click
            binding.root.setOnClickListener { onItemClick(appointment.appointmentId) }
        }
    }
}

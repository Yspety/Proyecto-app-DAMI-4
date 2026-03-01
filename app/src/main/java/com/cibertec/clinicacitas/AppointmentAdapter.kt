package com.cibertec.clinicacitas

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.cibertec.clinicacitas.Entidades.AppointmentInfo
import com.cibertec.clinicacitas.databinding.ItemAppointmentBinding

class AppointmentAdapter(
    private val items: List<AppointmentInfo>,
    private val onClick: (AppointmentInfo) -> Unit,
    private val onCancelClick: (AppointmentInfo) -> Unit
) : RecyclerView.Adapter<AppointmentAdapter.VH>() {

    class VH(val binding: ItemAppointmentBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val binding = ItemAppointmentBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return VH(binding)
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        val info = items[position]
        holder.binding.tvAppointmentDateTime.text = "${info.date} ${info.time}"
        holder.binding.tvAppointmentDoctor.text = "${info.doctorName} • ${info.especialidadNombre}"
        holder.binding.tvAppointmentStatus.text = info.status
        
        holder.binding.root.setOnClickListener { onClick(info) }
        holder.binding.btnCancelAppointment.setOnClickListener { onCancelClick(info) }
    }

    override fun getItemCount(): Int = items.size
}

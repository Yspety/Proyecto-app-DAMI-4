package com.cibertec.clinicacitas

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.cibertec.clinicacitas.databinding.ItemAppointmentBinding

class AppointmentAdapter(
    private val items: List<Appointment>,
    private val onClick: (Appointment) -> Unit
) : RecyclerView.Adapter<AppointmentAdapter.VH>() {

    class VH(val binding: ItemAppointmentBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val binding = ItemAppointmentBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return VH(binding)
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        val a = items[position]
        holder.binding.tvTitle.text = "${a.date} ${a.time}"
        holder.binding.tvSubtitle.text = "${a.doctor.fullName} • ${a.doctor.specialty.name}"
        holder.binding.tvStatus.text = a.status
        holder.binding.root.setOnClickListener { onClick(a) }
    }

    override fun getItemCount(): Int = items.size
}

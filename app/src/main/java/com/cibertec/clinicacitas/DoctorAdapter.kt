package com.cibertec.clinicacitas

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.cibertec.clinicacitas.Entidades.DoctorInfo
import com.cibertec.clinicacitas.databinding.ItemDoctorBinding

class DoctorAdapter(
    private val items: List<DoctorInfo>,
    private val onClick: (DoctorInfo) -> Unit
) : RecyclerView.Adapter<DoctorAdapter.VH>() {

    class VH(val binding: ItemDoctorBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val binding = ItemDoctorBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return VH(binding)
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        val info = items[position]
        holder.binding.tvName.text = info.fullName
        holder.binding.tvSpecialty.text = info.especialidadNombre
        holder.binding.tvRoom.text = info.room
        holder.binding.root.setOnClickListener { onClick(info) }
    }

    override fun getItemCount(): Int = items.size
}

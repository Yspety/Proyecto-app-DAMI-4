package com.cibertec.clinicacitas

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.cibertec.clinicacitas.databinding.ItemDoctorBinding

class DoctorAdapter(
    private val items: List<Doctor>,
    private val onClick: (Doctor) -> Unit
) : RecyclerView.Adapter<DoctorAdapter.VH>() {

    class VH(val binding: ItemDoctorBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val binding = ItemDoctorBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return VH(binding)
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        val d = items[position]
        holder.binding.tvName.text = d.fullName
        holder.binding.tvSpecialty.text = d.specialty.name
        holder.binding.tvRoom.text = d.room
        holder.binding.root.setOnClickListener { onClick(d) }
    }
        // le dice al recycler cuantos items hay
    override fun getItemCount(): Int = items.size
}

package com.cibertec.clinicacitas

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.cibertec.clinicacitas.Entidades.Especialidad
import com.cibertec.clinicacitas.databinding.ItemEspecialidadBinding

class EspecialidadAdapter(
    private val items: List<Especialidad>,
    private val onClick: (Especialidad) -> Unit
) : RecyclerView.Adapter<EspecialidadAdapter.VH>() {

    class VH(val binding: ItemEspecialidadBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val binding = ItemEspecialidadBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return VH(binding)
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        val esp = items[position]
        holder.binding.tvEspecialidadNombre.text = esp.name
        holder.binding.root.setOnClickListener { onClick(esp) }
    }

    override fun getItemCount(): Int = items.size
}
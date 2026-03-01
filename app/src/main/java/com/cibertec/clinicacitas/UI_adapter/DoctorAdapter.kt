package com.cibertec.clinicacitas.UI_adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.PopupMenu
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.cibertec.clinicacitas.Entidades.DoctorInfo
import com.cibertec.clinicacitas.R

class DoctorAdapter(
    private val doctorList: List<DoctorInfo>,
    private val onItemClick: (DoctorInfo) -> Unit,
    private val onEditClick: (DoctorInfo) -> Unit,
    private val onDeleteClick: (DoctorInfo) -> Unit
) : RecyclerView.Adapter<DoctorAdapter.DoctorViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DoctorViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_doctor, parent, false)
        return DoctorViewHolder(view)
    }

    override fun onBindViewHolder(holder: DoctorViewHolder, position: Int) {
        val doctor = doctorList[position]
        holder.bind(doctor, onItemClick, onEditClick, onDeleteClick)
    }

    override fun getItemCount(): Int = doctorList.size

    class DoctorViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvDoctorName: TextView = itemView.findViewById(R.id.tvDoctorName)
        private val tvDoctorSpecialty: TextView = itemView.findViewById(R.id.tvDoctorSpecialty)
        private val btnMenuOptions: ImageButton = itemView.findViewById(R.id.btnMenuOptions)

        fun bind(
            doctor: DoctorInfo,
            onItemClick: (DoctorInfo) -> Unit,
            onEditClick: (DoctorInfo) -> Unit,
            onDeleteClick: (DoctorInfo) -> Unit
        ) {
            tvDoctorName.text = doctor.fullName
            tvDoctorSpecialty.text = doctor.especialidadNombre

            itemView.setOnClickListener { onItemClick(doctor) }

            btnMenuOptions.setOnClickListener { view ->
                showPopupMenu(view, doctor, onEditClick, onDeleteClick)
            }
        }

        private fun showPopupMenu(view: View, doctor: DoctorInfo, onEditClick: (DoctorInfo) -> Unit, onDeleteClick: (DoctorInfo) -> Unit) {
            val popup = PopupMenu(view.context, view)
            popup.menu.add("Editar")
            popup.menu.add("Eliminar")

            popup.setOnMenuItemClickListener { item ->
                when (item.title) {
                    "Editar" -> onEditClick(doctor)
                    "Eliminar" -> onDeleteClick(doctor)
                }
                true
            }
            popup.show()
        }
    }
}

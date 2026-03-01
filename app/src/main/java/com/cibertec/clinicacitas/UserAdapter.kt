package com.cibertec.clinicacitas

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.cibertec.clinicacitas.Entidades.Usuario

class UserAdapter(private val userList: List<Usuario>) : RecyclerView.Adapter<UserAdapter.UserViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_user, parent, false)
        return UserViewHolder(view)
    }

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        val user = userList[position]
        holder.tvUsername.text = user.username

        // --- UNIFICACIÓN VISUAL DE ROLES ---
        val rolParaMostrar = when (user.role.lowercase()) {
            "usuario", "paciente" -> "PACIENTE"
            "medico" -> "MÉDICO"
            "admin" -> "ADMIN"
            else -> user.role.uppercase()
        }

        // Mostramos el Rol y el ID juntos para que no se pierda el dato del DNI
        holder.tvRole.text = "$rolParaMostrar (ID: ${user.id})"

        // Colores consistentes (Tu lógica original intacta)
        when (user.role.lowercase()) {
            "admin" -> holder.tvRole.setTextColor(Color.parseColor("#D32F2F")) // Rojo
            "medico" -> holder.tvRole.setTextColor(Color.parseColor("#1976D2")) // Azul
            "usuario", "paciente" -> holder.tvRole.setTextColor(Color.parseColor("#388E3C")) // Verde
            else -> holder.tvRole.setTextColor(Color.GRAY)
        }
    }

    override fun getItemCount(): Int = userList.size

    class UserViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        // Ahora estos IDs coinciden exactamente con el XML actualizado arriba
        val tvUsername: TextView = itemView.findViewById(R.id.tvUsername)
        val tvRole: TextView = itemView.findViewById(R.id.tvRole)
    }
}

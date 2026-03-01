package com.cibertec.clinicacitas

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.PopupMenu
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.cibertec.clinicacitas.Entidades.Usuario

class UserAdapter(
    private val userList: List<Usuario>,
    private val onEditClick: (Usuario) -> Unit,
    private val onDeleteClick: (Usuario) -> Unit
) : RecyclerView.Adapter<UserAdapter.UserViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_user, parent, false)
        return UserViewHolder(view)
    }

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        val user = userList[position]
        holder.bind(user, onEditClick, onDeleteClick)
    }

    override fun getItemCount(): Int = userList.size

    class UserViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvUsername: TextView = itemView.findViewById(R.id.tvUsername)
        private val tvRole: TextView = itemView.findViewById(R.id.tvRole)
        private val btnMenuOptions: ImageButton = itemView.findViewById(R.id.btnMenuOptions)

        fun bind(user: Usuario, onEditClick: (Usuario) -> Unit, onDeleteClick: (Usuario) -> Unit) {
            tvUsername.text = user.username
            tvRole.text = user.rol

            // No se puede editar o eliminar al usuario 'admin'
            if (user.username.equals("admin", ignoreCase = true)) {
                btnMenuOptions.visibility = View.INVISIBLE
            } else {
                btnMenuOptions.visibility = View.VISIBLE
            }

            btnMenuOptions.setOnClickListener {
                showPopupMenu(it, user, onEditClick, onDeleteClick)
            }
        }

        private fun showPopupMenu(view: View, user: Usuario, onEditClick: (Usuario) -> Unit, onDeleteClick: (Usuario) -> Unit) {
            val popup = PopupMenu(view.context, view)
            popup.menu.add("Editar Rol")
            popup.menu.add("Eliminar Usuario")

            popup.setOnMenuItemClickListener { item ->
                when (item.title) {
                    "Editar Rol" -> onEditClick(user)
                    "Eliminar Usuario" -> onDeleteClick(user)
                }
                true
            }

            popup.show()
        }
    }
}

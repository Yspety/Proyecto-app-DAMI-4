package com.cibertec.clinicacitas.Utils

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import com.cibertec.clinicacitas.Entidades.Usuario
import com.cibertec.clinicacitas.UI.LoginActivity

object SessionStore {
    var currentUser: Usuario? = null

    fun logout(context: Context) {
        currentUser = null

        val prefs: SharedPreferences = context.getSharedPreferences("session", Context.MODE_PRIVATE)
        // Usamos commit() para asegurar que el borrado sea síncrono y se complete
        // antes de navegar a la siguiente pantalla. Esto evita condiciones de carrera.
        prefs.edit().clear().commit()

        val intent = Intent(context, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        context.startActivity(intent)
        
        // Finalizamos la actividad actual para que el usuario no pueda volver a ella
        if (context is Activity) {
            context.finish()
        }
    }
}

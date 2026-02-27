package com.cibertec.clinicacitas.DAO

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import com.cibertec.clinicacitas.DataBase.AppDBHelper
import com.cibertec.clinicacitas.Entidades.Usuario

class UsuarioDAO(context: Context) {

    private val dbHelper = AppDBHelper(context)

    fun login(username: String, password: String): String? {
        val db = dbHelper.readableDatabase
        val query = "SELECT rol FROM usuario WHERE username = ? AND password = ?"
        val cursor = db.rawQuery(query, arrayOf(username, password))

        var rol: String? = null
        if (cursor.moveToFirst()) {
            rol = cursor.getString(0)
        }
        cursor.close()
        db.close()
        return rol
    }

    fun registrarUsuario(usuario: Usuario): Long {
        val db = dbHelper.writableDatabase
        val values = ContentValues().apply {
            put("username", usuario.username)
            put("password", usuario.password)
            put("rol", usuario.role)
        }
        val result = db.insert("usuario", null, values)
        db.close()
        return result
    }

    @SuppressLint("Range")
    fun getAllUsers(): List<Usuario> {
        val users = mutableListOf<Usuario>()
        val db = dbHelper.readableDatabase
        val cursor = db.rawQuery("SELECT id, username, password, rol FROM usuario", null)

        if (cursor.moveToFirst()) {
            do {
                val id = cursor.getInt(cursor.getColumnIndex("id"))
                val username = cursor.getString(cursor.getColumnIndex("username"))
                val password = cursor.getString(cursor.getColumnIndex("password"))
                val role = cursor.getString(cursor.getColumnIndex("rol"))
                users.add(Usuario(id, username, password, role))
            } while (cursor.moveToNext())
        }
        cursor.close()
        db.close()
        return users
    }

    fun Registrar(username: String, pass: String, rol: String): Long {
        val db = dbHelper.writableDatabase
        val values = ContentValues().apply {
            put("username", username)
            put("password", pass)
            put("rol", rol)
        }
        val result = db.insert("usuario", null, values)
        db.close()
        return result
    }
}

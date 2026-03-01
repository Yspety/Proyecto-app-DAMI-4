package com.cibertec.clinicacitas.DAO

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import com.cibertec.clinicacitas.DataBase.AppDBHelper
import com.cibertec.clinicacitas.Entidades.Usuario

class UsuarioDAO(context: Context) {

    private val dbHelper = AppDBHelper(context)

    @SuppressLint("Range")
    fun loginUniversal(identifier: String, password: String): Usuario? {
        val db = dbHelper.readableDatabase
        val query = "SELECT * FROM usuario WHERE (username = ? OR email = ? OR dni = ?) AND password = ?"
        val cursor = db.rawQuery(query, arrayOf(identifier, identifier, identifier, password))

        var usuario: Usuario? = null
        if (cursor.moveToFirst()) {
            usuario = Usuario(
                id = cursor.getInt(cursor.getColumnIndex("id")),
                username = cursor.getString(cursor.getColumnIndex("username")),
                password = cursor.getString(cursor.getColumnIndex("password")),
                role = cursor.getString(cursor.getColumnIndex("rol"))
            )
        }
        cursor.close()
        return usuario
    }

    @SuppressLint("Range")
    fun findUserByDni(dni: String): Usuario? {
        val db = dbHelper.readableDatabase
        val cursor = db.rawQuery("SELECT * FROM usuario WHERE dni = ? LIMIT 1", arrayOf(dni))
        var usuario: Usuario? = null
        if (cursor.moveToFirst()) {
            usuario = Usuario(
                id = cursor.getInt(cursor.getColumnIndex("id")),
                username = cursor.getString(cursor.getColumnIndex("username")),
                password = cursor.getString(cursor.getColumnIndex("password")),
                role = cursor.getString(cursor.getColumnIndex("rol"))
            )
        }
        cursor.close()
        return usuario
    }

    fun registrarUsuarioCompleto(username: String, email: String, pass: String, rol: String, dni: String, fullName: String, telefono: String, fechaNac: String): Long {
        val db = dbHelper.writableDatabase
        val values = ContentValues().apply {
            put("username", username)
            put("email", email)
            put("password", pass)
            put("rol", rol)
            put("dni", dni)
            put("fullName", fullName)
            // NO AGREGAR TELEFONO NI FECHA AQUÍ
        }
        return db.insert("usuario", null, values)
    }

    fun updateUsuarioSimple(id: Int, username: String, email: String, fullName: String, password: String): Int {
        val db = dbHelper.writableDatabase

        // Verificamos antes de actualizar si el email o username ya existen en OTRO ID
        val cursor = db.rawQuery("SELECT id FROM usuario WHERE (username = ? OR email = ?) AND id != ?",
            arrayOf(username, email, id.toString()))

        if (cursor.count > 0) {
            cursor.close()
            return -1 // Indica conflicto de duplicados
        }
        cursor.close()

        val values = ContentValues().apply {
            put("username", username)
            put("email", email)
            put("fullName", fullName)
            if (password.isNotEmpty()) put("password", password)
        }
        return db.update("usuario", values, "id = ?", arrayOf(id.toString()))
    }

    @SuppressLint("Range")
    fun getAllUsers(): List<Usuario> {
        val users = mutableListOf<Usuario>()
        val db = dbHelper.readableDatabase
        val cursor = db.rawQuery("SELECT * FROM usuario", null)
        if (cursor.moveToFirst()) {
            do {
                users.add(Usuario(
                    id = cursor.getInt(cursor.getColumnIndex("id")),
                    username = cursor.getString(cursor.getColumnIndex("username")),
                    password = cursor.getString(cursor.getColumnIndex("password")),
                    role = cursor.getString(cursor.getColumnIndex("rol"))
                ))
            } while (cursor.moveToNext())
        }
        cursor.close()
        return users
    }
}
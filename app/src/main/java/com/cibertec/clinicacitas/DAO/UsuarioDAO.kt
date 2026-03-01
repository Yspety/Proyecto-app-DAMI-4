package com.cibertec.clinicacitas.DAO

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import com.cibertec.clinicacitas.DataBase.AppDBHelper
import com.cibertec.clinicacitas.Entidades.Usuario
import com.cibertec.clinicacitas.SecurityUtils

class UsuarioDAO(context: Context) {

    private val dbHelper = AppDBHelper(context)

    @SuppressLint("Range")
    fun login(username: String, password: String): Usuario? {
        val db = dbHelper.readableDatabase
        val cursor = db.rawQuery("SELECT id, username, rol, passwordHash, passwordSalt FROM usuario WHERE username = ? LIMIT 1", arrayOf(username))

        if (!cursor.moveToFirst()) {
            cursor.close()
            db.close()
            return null
        }

        val id = cursor.getInt(cursor.getColumnIndex("id"))
        val storedUsername = cursor.getString(cursor.getColumnIndex("username"))
        val rol = cursor.getString(cursor.getColumnIndex("rol"))
        val storedHash = cursor.getString(cursor.getColumnIndex("passwordHash"))
        val storedSalt = cursor.getBlob(cursor.getColumnIndex("passwordSalt"))

        cursor.close()
        db.close()

        val inputHash = SecurityUtils.hashPassword(password, storedSalt)

        return if (inputHash == storedHash) {
            Usuario(id, storedUsername, rol)
        } else {
            null
        }
    }

    fun registrarUsuario(username: String, pass: String, rol: String): Long {
        val db = dbHelper.writableDatabase
        val salt = SecurityUtils.getSalt()
        val hash = SecurityUtils.hashPassword(pass, salt)

        val values = ContentValues().apply {
            put("username", username)
            put("passwordHash", hash)
            put("passwordSalt", salt)
            put("rol", rol)
        }

        val result = db.insert("usuario", null, values)
        db.close()
        return result
    }

    fun updateUserRole(userId: Int, newRole: String) {
        val db = dbHelper.writableDatabase
        val values = ContentValues().apply {
            put("rol", newRole)
        }
        db.update("usuario", values, "id = ?", arrayOf(userId.toString()))
        db.close()
    }

    // CORREGIDO: Lógica de borrado segura que maneja las dependencias
    @SuppressLint("Range")
    fun deleteUser(userId: Int) {
        val db = dbHelper.writableDatabase

        // 1. Buscar si el usuario es un médico
        val doctorCursor = db.rawQuery("SELECT id FROM doctor WHERE usuarioId = ? LIMIT 1", arrayOf(userId.toString()))
        if (doctorCursor.moveToFirst()) {
            val doctorId = doctorCursor.getInt(doctorCursor.getColumnIndex("id"))

            // 2. Si es médico, eliminar sus citas
            db.delete("appointment", "doctorId = ?", arrayOf(doctorId.toString()))

            // 3. Luego, eliminar su perfil de doctor
            db.delete("doctor", "id = ?", arrayOf(doctorId.toString()))
        }
        doctorCursor.close()

        // 4. Finalmente, eliminar al usuario
        db.delete("usuario", "id = ?", arrayOf(userId.toString()))
        db.close()
    }

    @SuppressLint("Range")
    fun getAllUsers(): List<Usuario> {
        val users = mutableListOf<Usuario>()
        val db = dbHelper.readableDatabase
        val cursor = db.rawQuery("SELECT id, username, rol FROM usuario", null)

        if (cursor.moveToFirst()) {
            do {
                val id = cursor.getInt(cursor.getColumnIndex("id"))
                val username = cursor.getString(cursor.getColumnIndex("username"))
                val role = cursor.getString(cursor.getColumnIndex("rol"))
                users.add(Usuario(id, username, role))
            } while (cursor.moveToNext())
        }
        cursor.close()
        db.close()
        return users
    }
}

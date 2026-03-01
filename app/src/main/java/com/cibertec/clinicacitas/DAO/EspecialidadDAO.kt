package com.cibertec.clinicacitas.DAO

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import com.cibertec.clinicacitas.DataBase.AppDBHelper
import com.cibertec.clinicacitas.Entidades.Especialidad

class EspecialidadDAO(context: Context) {
    private val dbHelper = AppDBHelper(context)

    fun addSpecialty(name: String): Long {
        val db = dbHelper.writableDatabase
        val values = ContentValues().apply { put("name", name) }
        return db.insert("specialty", null, values)
    }

    fun updateSpecialty(id: Int, newName: String): Int {
        val db = dbHelper.writableDatabase
        val values = ContentValues().apply { put("name", newName) }
        return db.update("specialty", values, "id = ?", arrayOf(id.toString()))
    }

    @SuppressLint("Range")
    fun getAllSpecialties(): List<Especialidad> {
        val specialties = mutableListOf<Especialidad>()
        val db = dbHelper.readableDatabase
        val cursor = db.rawQuery("SELECT * FROM specialty", null)
        if (cursor.moveToFirst()) {
            do {
                specialties.add(Especialidad(
                    cursor.getInt(cursor.getColumnIndex("id")),
                    cursor.getString(cursor.getColumnIndex("name"))
                ))
            } while (cursor.moveToNext())
        }
        cursor.close()
        return specialties
    }
}

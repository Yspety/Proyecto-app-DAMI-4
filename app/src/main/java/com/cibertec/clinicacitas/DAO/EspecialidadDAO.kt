package com.cibertec.clinicacitas.DAO

import android.annotation.SuppressLint
import android.content.Context
import com.cibertec.clinicacitas.DataBase.AppDBHelper
import com.cibertec.clinicacitas.Entidades.Especialidad

class EspecialidadDAO(context: Context) {

    private val dbHelper = AppDBHelper(context)

    @SuppressLint("Range")
    fun getAllSpecialties(): List<Especialidad> {
        val specialties = mutableListOf<Especialidad>()
        val db = dbHelper.readableDatabase
        val cursor = db.rawQuery("SELECT * FROM specialty", null)

        if (cursor.moveToFirst()) {
            do {
                val id = cursor.getInt(cursor.getColumnIndex("id"))
                val name = cursor.getString(cursor.getColumnIndex("name"))
                specialties.add(Especialidad(id, name))
            } while (cursor.moveToNext())
        }

        cursor.close()
        db.close()
        return specialties
    }
}

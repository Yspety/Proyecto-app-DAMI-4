package com.cibertec.clinicacitas.DAO

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import com.cibertec.clinicacitas.DataBase.AppDBHelper
import com.cibertec.clinicacitas.Entidades.Doctor
import com.cibertec.clinicacitas.Entidades.DoctorInfo

class DoctorDAO(context: Context) {

    private val dbHelper = AppDBHelper(context)

    fun addDoctor(doctor: Doctor): Long {
        val db = dbHelper.writableDatabase
        val values = ContentValues().apply {
            put("usuarioId", doctor.usuarioId)
            put("fullName", doctor.fullName)
            put("specialtyId", doctor.specialtyId)
            put("cmp", doctor.cmp)
            put("room", doctor.room)
        }
        val result = db.insert("doctor", null, values)
        db.close()
        return result
    }

    fun updateDoctor(doctor: DoctorInfo, newSpecialtyId: Int) {
        val db = dbHelper.writableDatabase
        val values = ContentValues().apply {
            put("fullName", doctor.fullName)
            put("cmp", doctor.cmp)
            put("room", doctor.room)
            put("specialtyId", newSpecialtyId)
        }
        db.update("doctor", values, "id = ?", arrayOf(doctor.doctorId.toString()))
        db.close()
    }

    fun deleteDoctor(doctorId: Int) {
        val db = dbHelper.writableDatabase
        // Primero, eliminar todas las citas asociadas a este médico
        db.delete("appointment", "doctorId = ?", arrayOf(doctorId.toString()))
        // Luego, eliminar al médico
        db.delete("doctor", "id = ?", arrayOf(doctorId.toString()))
        db.close()
    }

    @SuppressLint("Range")
    fun getDoctorByUserId(userId: Int): Doctor? {
        val db = dbHelper.readableDatabase
        val cursor = db.rawQuery("SELECT * FROM doctor WHERE usuarioId = ? LIMIT 1", arrayOf(userId.toString()))
        var doctor: Doctor? = null
        if (cursor.moveToFirst()) {
            val id = cursor.getInt(cursor.getColumnIndex("id"))
            val usuarioId = cursor.getInt(cursor.getColumnIndex("usuarioId"))
            val fullName = cursor.getString(cursor.getColumnIndex("fullName"))
            val specialtyId = cursor.getInt(cursor.getColumnIndex("specialtyId"))
            val cmp = cursor.getString(cursor.getColumnIndex("cmp"))
            val room = cursor.getString(cursor.getColumnIndex("room"))
            doctor = Doctor(id, usuarioId, fullName, specialtyId, cmp, room)
        }
        cursor.close()
        db.close()
        return doctor
    }

    @SuppressLint("Range")
    fun getDoctorInfoById(doctorId: Int): DoctorInfo? {
        val db = dbHelper.readableDatabase
        var doctorInfo: DoctorInfo? = null
        val query = """
            SELECT d.id AS doctorId, d.fullName, s.name AS especialidadNombre, d.cmp, d.room
            FROM doctor d
            JOIN specialty s ON d.specialtyId = s.id
            WHERE d.id = ?
            LIMIT 1
        """
        val cursor = db.rawQuery(query, arrayOf(doctorId.toString()))
        if (cursor.moveToFirst()) {
            val id = cursor.getInt(cursor.getColumnIndex("doctorId"))
            val fullName = cursor.getString(cursor.getColumnIndex("fullName"))
            val especialidadNombre = cursor.getString(cursor.getColumnIndex("especialidadNombre"))
            val cmp = cursor.getString(cursor.getColumnIndex("cmp"))
            val room = cursor.getString(cursor.getColumnIndex("room"))
            doctorInfo = DoctorInfo(id, fullName, especialidadNombre, cmp, room)
        }
        cursor.close()
        db.close()
        return doctorInfo
    }

    @SuppressLint("Range")
    fun getAllDoctorInfo(): List<DoctorInfo> {
        val doctorInfos = mutableListOf<DoctorInfo>()
        val db = dbHelper.readableDatabase
        val query = """
            SELECT d.id AS doctorId, d.fullName, s.name AS especialidadNombre, d.cmp, d.room
            FROM doctor d
            JOIN specialty s ON d.specialtyId = s.id
        """
        val cursor = db.rawQuery(query, null)
        if (cursor.moveToFirst()) {
            do {
                val doctorId = cursor.getInt(cursor.getColumnIndex("doctorId"))
                val fullName = cursor.getString(cursor.getColumnIndex("fullName"))
                val especialidadNombre = cursor.getString(cursor.getColumnIndex("especialidadNombre"))
                val cmp = cursor.getString(cursor.getColumnIndex("cmp"))
                val room = cursor.getString(cursor.getColumnIndex("room"))
                doctorInfos.add(DoctorInfo(doctorId, fullName, especialidadNombre, cmp, room))
            } while (cursor.moveToNext())
        }
        cursor.close()
        db.close()
        return doctorInfos
    }

    @SuppressLint("Range")
    fun findDoctorByName(fullName: String): Doctor? {
        val db = dbHelper.readableDatabase
        val cursor = db.rawQuery("SELECT * FROM doctor WHERE fullName = ? LIMIT 1", arrayOf(fullName))
        var doctor: Doctor? = null
        if (cursor.moveToFirst()) {
            val id = cursor.getInt(cursor.getColumnIndex("id"))
            val name = cursor.getString(cursor.getColumnIndex("fullName"))
            val usuarioId = cursor.getInt(cursor.getColumnIndex("usuarioId"))
            val specialtyId = cursor.getInt(cursor.getColumnIndex("specialtyId"))
            val cmp = cursor.getString(cursor.getColumnIndex("cmp"))
            val room = cursor.getString(cursor.getColumnIndex("room"))
            doctor = Doctor(id, usuarioId, name, specialtyId, cmp, room)
        }
        cursor.close()
        db.close()
        return doctor
    }
}

package com.cibertec.clinicacitas.DAO

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import com.cibertec.clinicacitas.DataBase.AppDBHelper
import com.cibertec.clinicacitas.Entidades.Doctor
import com.cibertec.clinicacitas.Entidades.DoctorInfo

class DoctorDAO(context: Context) {

    private val dbHelper = AppDBHelper(context)

    fun checkCmpExists(cmp: String): Boolean {
        val db = dbHelper.readableDatabase
        val cursor = db.rawQuery("SELECT id FROM doctor WHERE cmp = ?", arrayOf(cmp))
        val exists = cursor.count > 0
        cursor.close()
        return exists
    }

    fun addDoctor(doctor: Doctor): Long {
        val db = dbHelper.writableDatabase
        val values = ContentValues().apply {
            put("usuarioId", doctor.usuarioId)
            put("fullName", doctor.fullName)
            put("specialtyId", doctor.specialtyId)
            put("cmp", doctor.cmp)
            put("room", doctor.room)
        }
        return db.insert("doctor", null, values)
    }

    fun updateDoctor(doctor: Doctor): Int {
        val db = dbHelper.writableDatabase
        val values = ContentValues().apply {
            put("fullName", doctor.fullName)
            put("specialtyId", doctor.specialtyId)
            put("cmp", doctor.cmp)
            put("room", doctor.room)
        }
        return db.update("doctor", values, "id = ?", arrayOf(doctor.id.toString()))
    }

    @SuppressLint("Range")
    fun getAllDoctorInfo(): List<DoctorInfo> {
        val doctorInfos = mutableListOf<DoctorInfo>()
        val db = dbHelper.readableDatabase
        val query = """
            SELECT d.id AS doctorId, d.fullName, 
            COALESCE(s.name, 'Sin Especialidad') AS especialidadNombre, 
            d.cmp, d.room
            FROM doctor d
            LEFT JOIN specialty s ON d.specialtyId = s.id
        """.trimIndent()
        val cursor = db.rawQuery(query, null)
        if (cursor.moveToFirst()) {
            do {
                doctorInfos.add(DoctorInfo(
                    cursor.getInt(cursor.getColumnIndex("doctorId")),
                    cursor.getString(cursor.getColumnIndex("fullName")),
                    cursor.getString(cursor.getColumnIndex("especialidadNombre")),
                    cursor.getString(cursor.getColumnIndex("cmp")),
                    cursor.getString(cursor.getColumnIndex("room"))
                ))
            } while (cursor.moveToNext())
        }
        cursor.close()
        return doctorInfos
    }

    @SuppressLint("Range")
    fun findDoctorById(doctorId: Int): Doctor? {
        val db = dbHelper.readableDatabase
        val cursor = db.rawQuery("SELECT * FROM doctor WHERE id = ? LIMIT 1", arrayOf(doctorId.toString()))
        var doctor: Doctor? = null
        if (cursor.moveToFirst()) {
            doctor = Doctor(
                id = cursor.getInt(cursor.getColumnIndex("id")),
                usuarioId = cursor.getInt(cursor.getColumnIndex("usuarioId")),
                fullName = cursor.getString(cursor.getColumnIndex("fullName")),
                specialtyId = cursor.getInt(cursor.getColumnIndex("specialtyId")),
                cmp = cursor.getString(cursor.getColumnIndex("cmp")),
                room = cursor.getString(cursor.getColumnIndex("room"))
            )
        }
        cursor.close()
        return doctor
    }

    @SuppressLint("Range")
    fun findDoctorByUserId(userId: Int): Doctor? {
        val db = dbHelper.readableDatabase
        val cursor = db.rawQuery("SELECT * FROM doctor WHERE usuarioId = ? LIMIT 1", arrayOf(userId.toString()))
        var doctor: Doctor? = null
        if (cursor.moveToFirst()) {
            doctor = Doctor(
                id = cursor.getInt(cursor.getColumnIndex("id")),
                usuarioId = cursor.getInt(cursor.getColumnIndex("usuarioId")),
                fullName = cursor.getString(cursor.getColumnIndex("fullName")),
                specialtyId = cursor.getInt(cursor.getColumnIndex("specialtyId")),
                cmp = cursor.getString(cursor.getColumnIndex("cmp")),
                room = cursor.getString(cursor.getColumnIndex("room"))
            )
        }
        cursor.close()
        return doctor
    }
    @SuppressLint("Range")
    fun getDoctorInfoById(doctorId: Int): DoctorInfo? {
        val db = dbHelper.readableDatabase
        var doctorInfo: DoctorInfo? = null
        val query = """
        SELECT d.id AS doctorId, d.fullName, 
        COALESCE(s.name, 'Sin Especialidad') AS especialidadNombre, 
        d.cmp, d.room
        FROM doctor d
        LEFT JOIN specialty s ON d.specialtyId = s.id
        WHERE d.id = ? LIMIT 1
    """.trimIndent()

        val cursor = db.rawQuery(query, arrayOf(doctorId.toString()))
        if (cursor.moveToFirst()) {
            doctorInfo = DoctorInfo(
                cursor.getInt(cursor.getColumnIndex("doctorId")),
                cursor.getString(cursor.getColumnIndex("fullName")),
                cursor.getString(cursor.getColumnIndex("especialidadNombre")),
                cursor.getString(cursor.getColumnIndex("cmp")),
                cursor.getString(cursor.getColumnIndex("room"))
            )
        }
        cursor.close()
        return doctorInfo
    }

    @SuppressLint("Range")
    fun getDoctorFullDetailsById(doctorId: Int): Map<String, String>? {
        val db = dbHelper.readableDatabase
        val data = mutableMapOf<String, String>()

        // Eliminamos 'telefono' y 'fechaNacimiento' de la consulta porque no existen en tu tabla
        val query = """
        SELECT d.fullName, d.cmp, d.room, d.specialtyId, 
               u.dni, u.email, u.username
        FROM doctor d
        INNER JOIN usuario u ON d.usuarioId = u.id
        WHERE d.id = ?
    """.trimIndent()

        val cursor = db.rawQuery(query, arrayOf(doctorId.toString()))
        if (cursor != null && cursor.moveToFirst()) {
            data["fullName"] = cursor.getString(cursor.getColumnIndex("fullName")) ?: ""
            data["cmp"] = cursor.getString(cursor.getColumnIndex("cmp")) ?: ""
            data["room"] = cursor.getString(cursor.getColumnIndex("room")) ?: ""
            data["specialtyId"] = cursor.getString(cursor.getColumnIndex("specialtyId")) ?: ""
            data["dni"] = cursor.getString(cursor.getColumnIndex("dni")) ?: ""
            data["email"] = cursor.getString(cursor.getColumnIndex("email")) ?: ""
            data["username"] = cursor.getString(cursor.getColumnIndex("username")) ?: ""

            // Ponemos valores vacíos para que la Activity no explote al buscarlos
            data["telefono"] = ""
            data["fechaNacimiento"] = ""

            cursor.close()
            return data
        }
        cursor?.close()
        return null
    }

    @SuppressLint("Range")
    fun getDoctorInfoBySpecialty(specialtyId: Int): List<DoctorInfo> {
        val doctorInfos = mutableListOf<DoctorInfo>()
        val db = dbHelper.readableDatabase
        val query = """
        SELECT d.id AS doctorId, d.fullName, 
               s.name AS especialidadNombre, d.cmp, d.room
        FROM doctor d
        JOIN specialty s ON d.specialtyId = s.id
        WHERE d.specialtyId = ?
    """.trimIndent()

        val cursor = db.rawQuery(query, arrayOf(specialtyId.toString()))
        if (cursor.moveToFirst()) {
            do {
                doctorInfos.add(DoctorInfo(
                    cursor.getInt(cursor.getColumnIndex("doctorId")),
                    cursor.getString(cursor.getColumnIndex("fullName")),
                    cursor.getString(cursor.getColumnIndex("especialidadNombre")),
                    cursor.getString(cursor.getColumnIndex("cmp")),
                    cursor.getString(cursor.getColumnIndex("room"))
                ))
            } while (cursor.moveToNext())
        }
        cursor.close()
        return doctorInfos
    }
}
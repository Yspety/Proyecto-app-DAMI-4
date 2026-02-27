package com.cibertec.clinicacitas.DAO

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import com.cibertec.clinicacitas.DataBase.AppDBHelper
import com.cibertec.clinicacitas.Entidades.Appointment
import com.cibertec.clinicacitas.Entidades.AppointmentDetailInfo
import com.cibertec.clinicacitas.Entidades.AppointmentInfo

class AppointmentDAO(context: Context) {

    private val dbHelper = AppDBHelper(context)

    fun addAppointment(appointment: Appointment): Long {
        val db = dbHelper.writableDatabase
        val values = ContentValues().apply {
            put("patientName", appointment.patientName)
            put("doctorId", appointment.doctorId)
            put("date", appointment.date)
            put("time", appointment.time)
            put("reason", appointment.reason)
            put("status", appointment.status)
        }
        val result = db.insert("appointment", null, values)
        db.close()
        return result
    }

    @SuppressLint("Range")
    fun getAppointmentDetailById(appointmentId: Int): AppointmentDetailInfo? {
        val db = dbHelper.readableDatabase
        var appointmentDetail: AppointmentDetailInfo? = null
        val query = """
            SELECT a.id AS appointmentId, a.patientName, d.fullName AS doctorName, s.name AS especialidadNombre, a.date, a.time, a.reason, a.status
            FROM appointment a
            JOIN doctor d ON a.doctorId = d.id
            JOIN specialty s ON d.specialtyId = s.id
            WHERE a.id = ? LIMIT 1
        """
        val cursor = db.rawQuery(query, arrayOf(appointmentId.toString()))
        if (cursor.moveToFirst()) {
            val id = cursor.getInt(cursor.getColumnIndex("appointmentId"))
            val patientName = cursor.getString(cursor.getColumnIndex("patientName"))
            val doctorName = cursor.getString(cursor.getColumnIndex("doctorName"))
            val especialidadNombre = cursor.getString(cursor.getColumnIndex("especialidadNombre"))
            val date = cursor.getString(cursor.getColumnIndex("date"))
            val time = cursor.getString(cursor.getColumnIndex("time"))
            val reason = cursor.getString(cursor.getColumnIndex("reason"))
            val status = cursor.getString(cursor.getColumnIndex("status"))
            appointmentDetail = AppointmentDetailInfo(id, patientName, doctorName, especialidadNombre, date, time, reason, status)
        }
        cursor.close()
        db.close()
        return appointmentDetail
    }

    @SuppressLint("Range")
    fun getAllAppointmentInfo(): List<AppointmentInfo> {
        val appointmentInfos = mutableListOf<AppointmentInfo>()
        val db = dbHelper.readableDatabase
        val query = """
            SELECT a.id AS appointmentId, a.date, a.time, a.status, d.fullName AS doctorName, s.name AS especialidadNombre
            FROM appointment a
            JOIN doctor d ON a.doctorId = d.id
            JOIN specialty s ON d.specialtyId = s.id
        """
        val cursor = db.rawQuery(query, null)
        if (cursor.moveToFirst()) {
            do {
                val appointmentId = cursor.getInt(cursor.getColumnIndex("appointmentId"))
                val date = cursor.getString(cursor.getColumnIndex("date"))
                val time = cursor.getString(cursor.getColumnIndex("time"))
                val status = cursor.getString(cursor.getColumnIndex("status"))
                val doctorName = cursor.getString(cursor.getColumnIndex("doctorName"))
                val especialidadNombre = cursor.getString(cursor.getColumnIndex("especialidadNombre"))
                appointmentInfos.add(AppointmentInfo(appointmentId, date, time, status, doctorName, especialidadNombre))
            } while (cursor.moveToNext())
        }
        cursor.close()
        db.close()
        return appointmentInfos
    }

    @SuppressLint("Range")
    fun getAppointmentInfoForDoctor(doctorId: Int): List<AppointmentInfo> {
        val appointmentInfos = mutableListOf<AppointmentInfo>()
        val db = dbHelper.readableDatabase
        val query = """
            SELECT a.id AS appointmentId, a.date, a.time, a.status, d.fullName AS doctorName, s.name AS especialidadNombre
            FROM appointment a
            JOIN doctor d ON a.doctorId = d.id
            JOIN specialty s ON d.specialtyId = s.id
            WHERE a.doctorId = ?
        """
        val cursor = db.rawQuery(query, arrayOf(doctorId.toString()))
        if (cursor.moveToFirst()) {
            do {
                val appointmentId = cursor.getInt(cursor.getColumnIndex("appointmentId"))
                val date = cursor.getString(cursor.getColumnIndex("date"))
                val time = cursor.getString(cursor.getColumnIndex("time"))
                val status = cursor.getString(cursor.getColumnIndex("status"))
                val doctorName = cursor.getString(cursor.getColumnIndex("doctorName"))
                val especialidadNombre = cursor.getString(cursor.getColumnIndex("especialidadNombre"))
                appointmentInfos.add(AppointmentInfo(appointmentId, date, time, status, doctorName, especialidadNombre))
            } while (cursor.moveToNext())
        }
        cursor.close()
        db.close()
        return appointmentInfos
    }

    @SuppressLint("Range")
    fun getAppointmentInfoForPatient(patientName: String): List<AppointmentInfo> {
        val appointmentInfos = mutableListOf<AppointmentInfo>()
        val db = dbHelper.readableDatabase
        val query = """
            SELECT a.id AS appointmentId, a.date, a.time, a.status, d.fullName AS doctorName, s.name AS especialidadNombre
            FROM appointment a
            JOIN doctor d ON a.doctorId = d.id
            JOIN specialty s ON d.specialtyId = s.id
            WHERE a.patientName = ?
        """
        val cursor = db.rawQuery(query, arrayOf(patientName))
        if (cursor.moveToFirst()) {
            do {
                val appointmentId = cursor.getInt(cursor.getColumnIndex("appointmentId"))
                val date = cursor.getString(cursor.getColumnIndex("date"))
                val time = cursor.getString(cursor.getColumnIndex("time"))
                val status = cursor.getString(cursor.getColumnIndex("status"))
                val doctorName = cursor.getString(cursor.getColumnIndex("doctorName"))
                val especialidadNombre = cursor.getString(cursor.getColumnIndex("especialidadNombre"))
                appointmentInfos.add(AppointmentInfo(appointmentId, date, time, status, doctorName, especialidadNombre))
            } while (cursor.moveToNext())
        }
        cursor.close()
        db.close()
        return appointmentInfos
    }
}

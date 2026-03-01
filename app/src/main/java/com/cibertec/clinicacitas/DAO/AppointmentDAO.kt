package com.cibertec.clinicacitas.DAO

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import com.cibertec.clinicacitas.DataBase.AppDBHelper
import com.cibertec.clinicacitas.Entidades.Appointment
import com.cibertec.clinicacitas.Entidades.AppointmentInfo

class AppointmentDAO(context: Context) {

    private val dbHelper = AppDBHelper(context)

    fun addAppointment(appointment: Appointment): Long {
        val db = dbHelper.writableDatabase
        val values = ContentValues().apply {
            put("patientId", appointment.patientId)
            put("doctorId", appointment.doctorId)
            put("date", appointment.date)
            put("time", appointment.time)
            put("reason", appointment.reason)
            put("status", appointment.status)
        }
        val result = db.insert("appointment", null, values)
        // No cerramos db aquí para evitar el error "database not open" en ejecuciones rápidas
        return result
    }

    @SuppressLint("Range")
    fun getAppointmentDetailById(appointmentId: Int): AppointmentInfo? {
        val db = dbHelper.readableDatabase
        var appointmentDetail: AppointmentInfo? = null

        val query = """
        SELECT a.id AS appointmentId, a.date, a.time, a.reason, a.status,
               u.fullName AS patientName, 
               d.fullName AS doctorName,
               s.name AS especialidadNombre
        FROM appointment a
        LEFT JOIN usuario u ON a.patientId = u.id
        LEFT JOIN doctor d ON a.doctorId = d.id
        LEFT JOIN specialty s ON d.specialtyId = s.id
        WHERE a.id = ?
    """.trimIndent()

        val cursor = db.rawQuery(query, arrayOf(appointmentId.toString()))
        if (cursor.moveToFirst()) {
            appointmentDetail = AppointmentInfo(
                appointmentId = cursor.getInt(cursor.getColumnIndex("appointmentId")),
                date = cursor.getString(cursor.getColumnIndex("date")) ?: "",
                time = cursor.getString(cursor.getColumnIndex("time")) ?: "",
                status = cursor.getString(cursor.getColumnIndex("status")) ?: "",
                doctorName = cursor.getString(cursor.getColumnIndex("doctorName")) ?: "No asignado",
                especialidadNombre = cursor.getString(cursor.getColumnIndex("especialidadNombre")) ?: "General",
                patientName = cursor.getString(cursor.getColumnIndex("patientName")) ?: "Paciente",
                reason = cursor.getString(cursor.getColumnIndex("reason")) ?: ""
            )
        }
        cursor.close()
        return appointmentDetail
    }

    // --- MÉTODOS PARA LISTADOS ---

    fun getAllAppointmentInfo(): List<AppointmentInfo> {
        val query = """
            SELECT a.id AS appointmentId, a.date, a.time, a.status, 
                   d.fullName AS doctorName, s.name AS especialidadNombre, u.username AS patientName
            FROM appointment a
            LEFT JOIN doctor d ON a.doctorId = d.id
            LEFT JOIN specialty s ON d.specialtyId = s.id
            LEFT JOIN usuario u ON a.patientId = u.id
            ORDER BY a.date DESC
        """
        return getAppointmentsByQuery(query, emptyArray())
    }

    fun getAppointmentInfoForPatient(patientId: Int): List<AppointmentInfo> {
        val query = """
            SELECT a.id AS appointmentId, a.date, a.time, a.status, 
                   d.fullName AS doctorName, s.name AS especialidadNombre
            FROM appointment a
            LEFT JOIN doctor d ON a.doctorId = d.id
            LEFT JOIN specialty s ON d.specialtyId = s.id
            WHERE a.patientId = ?
            ORDER BY a.date ASC
        """
        return getAppointmentsByQuery(query, arrayOf(patientId.toString()))
    }

    fun getAppointmentInfoForDoctor(doctorId: Int): List<AppointmentInfo> {
        val query = """
            SELECT a.id AS appointmentId, a.date, a.time, a.status, 
                   d.fullName AS doctorName, s.name AS especialidadNombre, u.username AS patientName
            FROM appointment a
            LEFT JOIN doctor d ON a.doctorId = d.id
            LEFT JOIN specialty s ON d.specialtyId = s.id
            LEFT JOIN usuario u ON a.patientId = u.id
            WHERE a.doctorId = ?
            ORDER BY a.date ASC
        """
        return getAppointmentsByQuery(query, arrayOf(doctorId.toString()))
    }

    @SuppressLint("Range")
    private fun getAppointmentsByQuery(query: String, args: Array<String>): List<AppointmentInfo> {
        val list = mutableListOf<AppointmentInfo>()
        val db = dbHelper.readableDatabase
        val cursor = db.rawQuery(query, args)

        if (cursor.moveToFirst()) {
            do {
                val idxId = cursor.getColumnIndex("appointmentId")
                val idxDate = cursor.getColumnIndex("date")
                val idxTime = cursor.getColumnIndex("time")
                val idxStatus = cursor.getColumnIndex("status")
                val idxDoc = cursor.getColumnIndex("doctorName")
                val idxEsp = cursor.getColumnIndex("especialidadNombre")
                val idxPat = cursor.getColumnIndex("patientName")

                list.add(AppointmentInfo(
                    appointmentId = if (idxId != -1) cursor.getInt(idxId) else 0,
                    date = if (idxDate != -1) cursor.getString(idxDate) ?: "" else "",
                    time = if (idxTime != -1) cursor.getString(idxTime) ?: "" else "",
                    status = if (idxStatus != -1) cursor.getString(idxStatus) ?: "" else "",
                    doctorName = if (idxDoc != -1) cursor.getString(idxDoc) ?: "Sin asignar" else "Sin asignar",
                    especialidadNombre = if (idxEsp != -1) cursor.getString(idxEsp) ?: "General" else "General",
                    patientName = if (idxPat != -1) cursor.getString(idxPat) ?: "" else "",
                    reason = ""
                ))
            } while (cursor.moveToNext())
        }
        cursor.close()
        // db.close() ELIMINADO: Dejamos que el sistema gestione la conexión
        return list
    }

    fun getDashboardStats(): Map<String, Int> {
        val db = dbHelper.readableDatabase
        val stats = mutableMapOf<String, Int>()

        // Citas Totales (Para que el Admin vea todo el movimiento)
        val resCitasTotal = db.rawQuery("SELECT COUNT(*) FROM appointment", null)
        if (resCitasTotal.moveToFirst()) stats["total_appointments"] = resCitasTotal.getInt(0)
        resCitasTotal.close()

        // Citas de HOY (Asegurando formato y eliminando espacios)
        val today = java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault()).format(java.util.Date())
        // Intentamos contar las que coincidan exactamente con la fecha
        val resToday = db.rawQuery("SELECT COUNT(*) FROM appointment WHERE TRIM(date) = ?", arrayOf(today))
        if (resToday.moveToFirst()) stats["today_appointments"] = resToday.getInt(0)
        resToday.close()

        // Pacientes
        val resPatients = db.rawQuery("SELECT COUNT(*) FROM usuario WHERE rol IN ('usuario', 'paciente')", null)
        if (resPatients.moveToFirst()) stats["total_patients"] = resPatients.getInt(0)
        resPatients.close()

        return stats
    }

    fun updateAppointmentStatus(appointmentId: Int, newStatus: String): Boolean {
        val db = dbHelper.writableDatabase
        val values = ContentValues().apply {
            put("status", newStatus)
        }
        // Retorna true si se actualizó al menos una fila
        return db.update("appointment", values, "id = ?", arrayOf(appointmentId.toString())) > 0
    }
}
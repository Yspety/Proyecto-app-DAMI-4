package com.cibertec.clinicacitas.DataBase

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.cibertec.clinicacitas.SecurityUtils

class AppDBHelper(context: Context) : SQLiteOpenHelper(context, "app.db", null, 6) {

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL("""
            CREATE TABLE usuario(
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                username TEXT NOT NULL UNIQUE,
                passwordHash TEXT NOT NULL,
                passwordSalt BLOB NOT NULL,
                rol TEXT NOT NULL
            )
        """.trimIndent())

        db.execSQL("CREATE TABLE specialty(id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT NOT NULL UNIQUE)")

        db.execSQL("""
            CREATE TABLE doctor(
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                usuarioId INTEGER NOT NULL UNIQUE,
                fullName TEXT NOT NULL,
                specialtyId INTEGER NOT NULL,
                cmp TEXT NOT NULL,
                room TEXT NOT NULL,
                FOREIGN KEY (usuarioId) REFERENCES usuario(id),
                FOREIGN KEY (specialtyId) REFERENCES specialty(id)
            )
        """.trimIndent())

        db.execSQL("""
            CREATE TABLE appointment(
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                patientName TEXT NOT NULL,
                doctorId INTEGER NOT NULL,
                dateTime INTEGER NOT NULL,
                reason TEXT NOT NULL,
                status TEXT NOT NULL,
                FOREIGN KEY (doctorId) REFERENCES doctor(id)
            )
        """.trimIndent())

        insertInitialData(db)
    }

    private fun insertInitialData(db: SQLiteDatabase) {
        // --- 1. Crear Especialidades ---
        db.execSQL("INSERT INTO specialty(id, name) VALUES(1, 'Medicina General')")
        db.execSQL("INSERT INTO specialty(id, name) VALUES(2, 'Pediatría')")
        db.execSQL("INSERT INTO specialty(id, name) VALUES(3, 'Cardiología')")

        // --- 2. Crear Usuarios ---
        insertUser(db, "admin", "admin123", "admin")
        val medicoUserId = insertUser(db, "medico", "medico123", "medico")
        insertUser(db, "usuario", "usuario123", "usuario")

        // --- 3. Crear Perfil de Doctor (de forma 100% segura) ---
        var medicoDoctorId: Long = -1
        if (medicoUserId != -1L) {
            val doctorValues = ContentValues().apply {
                put("usuarioId", medicoUserId)
                put("fullName", "Dr. Luis García")
                put("specialtyId", 1)
                put("cmp", "CMP 23456")
                put("room", "Consultorio 105")
            }
            medicoDoctorId = db.insert("doctor", null, doctorValues)
        }

        // --- 4. Crear Citas para el Doctor (de forma segura) ---
        if (medicoDoctorId != -1L) {
            val appointment1 = ContentValues().apply {
                put("patientName", "Juan Pérez")
                put("doctorId", medicoDoctorId)
                put("dateTime", 1773108000000L)
                put("reason", "Revisión general")
                put("status", "Programada")
            }
            db.insert("appointment", null, appointment1)

            val appointment2 = ContentValues().apply {
                put("patientName", "Ana Gómez")
                put("doctorId", medicoDoctorId)
                put("dateTime", 1773286200000L)
                put("reason", "Dolor de garganta")
                put("status", "Programada")
            }
            db.insert("appointment", null, appointment2)
        }
    }

    private fun insertUser(db: SQLiteDatabase, user: String, pass: String, role: String): Long {
        val salt = SecurityUtils.getSalt()
        val hash = SecurityUtils.hashPassword(pass, salt)
        val values = ContentValues().apply {
            put("username", user)
            put("passwordHash", hash)
            put("passwordSalt", salt)
            put("rol", role)
        }
        return db.insert("usuario", null, values)
    }

    @SuppressLint("Range")
    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        if (oldVersion < 5 && newVersion >= 5) {
            try {
                db.execSQL("ALTER TABLE appointment ADD COLUMN dateTime INTEGER NOT NULL DEFAULT 0")
                db.execSQL("UPDATE appointment SET dateTime = STRFTIME('%s', date || ' ' || time) * 1000 WHERE date IS NOT NULL AND time IS NOT NULL")
            } catch (e: Exception) { /* Ignorar */ }
        }

        if (oldVersion < 6 && newVersion >= 6) {
            try {
                db.execSQL("ALTER TABLE usuario ADD COLUMN passwordHash TEXT")
                db.execSQL("ALTER TABLE usuario ADD COLUMN passwordSalt BLOB")

                val cursor = db.rawQuery("SELECT id, password FROM usuario WHERE password IS NOT NULL", null)
                if (cursor.moveToFirst()) {
                    do {
                        val id = cursor.getInt(cursor.getColumnIndex("id"))
                        val oldPassword = cursor.getString(cursor.getColumnIndex("password"))

                        val salt = SecurityUtils.getSalt()
                        val hash = SecurityUtils.hashPassword(oldPassword, salt)
                        val values = ContentValues().apply {
                            put("passwordHash", hash)
                            put("passwordSalt", salt)
                        }
                        db.update("usuario", values, "id = ?", arrayOf(id.toString()))
                    } while (cursor.moveToNext())
                }
                cursor.close()
            } catch (e: Exception) { /* Ignorar */ }
        }
    }
}

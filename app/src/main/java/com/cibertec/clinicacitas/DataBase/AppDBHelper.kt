package com.cibertec.clinicacitas.DataBase

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class AppDBHelper(context: Context) : SQLiteOpenHelper(context, "app.db", null, 12) {
    // Subí la versión a 10 para forzar la recreación de tablas con los nuevos datos.

    override fun onCreate(db: SQLiteDatabase) {
        // --- 1. Tabla de Usuarios ---
        db.execSQL("""
            CREATE TABLE usuario(
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                username TEXT UNIQUE,
                email TEXT UNIQUE,
                password TEXT,
                rol TEXT,
                dni TEXT UNIQUE,
                fullName TEXT
            )
        """.trimIndent())

        // --- 2. Tabla de Especialidades ---
        db.execSQL("""
            CREATE TABLE specialty(
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                name TEXT UNIQUE
            )
        """.trimIndent())

        // --- 3. Tabla de Doctores ---
        db.execSQL("""
            CREATE TABLE doctor(
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                usuarioId INTEGER UNIQUE,
                fullName TEXT,
                specialtyId INTEGER,
                cmp TEXT,
                room TEXT,
                FOREIGN KEY(usuarioId) REFERENCES usuario(id)
            )
        """.trimIndent())

        // --- 4. Tabla: Horarios ---
        db.execSQL("""
            CREATE TABLE doctor_schedule(
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                doctorId INTEGER,
                dayOfWeek TEXT,
                startTime TEXT,
                endTime TEXT,
                FOREIGN KEY(doctorId) REFERENCES doctor(id)
            )
        """.trimIndent())

        // --- 5. Tabla de Citas ---
        db.execSQL("""
            CREATE TABLE appointment(
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                patientId INTEGER, 
                doctorId INTEGER,
                date TEXT,
                time TEXT,
                reason TEXT,
                status TEXT,
                FOREIGN KEY(patientId) REFERENCES usuario(id),
                FOREIGN KEY(doctorId) REFERENCES doctor(id)
            )
        """.trimIndent())

        insertInitialData(db)
    }

    private fun insertInitialData(db: SQLiteDatabase) {
        // 1. ESPECIALIDADES
        val specs = arrayOf("Medicina General", "Pediatría", "Cardiología", "Dermatología", "Ginecología", "Oftalmología")
        specs.forEach { db.execSQL("INSERT INTO specialty(name) VALUES('$it')") }

        // 2. USUARIOS (Admin, Médicos y Pacientes)
        // Admin
        db.execSQL("INSERT INTO usuario(username, email, password, rol, dni, fullName) VALUES('admin', 'admin@clinica.com', 'admin123', 'admin', '00000000', 'Administrador General')")

        // Usuarios para Médicos (IDs del 2 al 8)
        val medicosData = arrayOf(
            "('dr_garcia', 'garcia@med.com', '123', 'medico', '11111111', 'Dr. Ricardo García')",
            "('dra_lopez', 'lopez@med.com', '123', 'medico', '22222222', 'Dra. Elena López')",
            "('dr_torres', 'torres@med.com', '123', 'medico', '33333333', 'Dr. Hugo Torres')",
            "('dra_mendoza', 'mendoza@med.com', '123', 'medico', '44444444', 'Dra. Sara Mendoza')",
            "('dr_vargas', 'vargas@med.com', '123', 'medico', '55555555', 'Dr. Javier Vargas')",
            "('dra_salas', 'salas@med.com', '123', 'medico', '66666666', 'Dra. Claudia Salas')",
            "('dr_peralta', 'peralta@med.com', '123', 'medico', '77777777', 'Dr. Oscar Peralta')"
        )
        medicosData.forEach { db.execSQL("INSERT INTO usuario(username, email, password, rol, dni, fullName) VALUES $it") }

        // Usuarios Pacientes (IDs 9 y 10)
        db.execSQL("INSERT INTO usuario(username, email, password, rol, dni, fullName) VALUES('paciente1', 'perez@gmail.com', '123', 'usuario', '88888888', 'Juan Pérez')")
        db.execSQL("INSERT INTO usuario(username, email, password, rol, dni, fullName) VALUES('paciente2', 'rojas@gmail.com', '123', 'usuario', '99999999', 'Ana Rojas')")

        // 3. DOCTORES (Vínculo con Usuarios y Especialidades)
        db.execSQL("INSERT INTO doctor(usuarioId, fullName, specialtyId, cmp, room) VALUES(2, 'Dr. Ricardo García', 1, 'CMP 10001', 'C-101')")
        db.execSQL("INSERT INTO doctor(usuarioId, fullName, specialtyId, cmp, room) VALUES(3, 'Dra. Elena López', 2, 'CMP 10002', 'C-102')")
        db.execSQL("INSERT INTO doctor(usuarioId, fullName, specialtyId, cmp, room) VALUES(4, 'Dr. Hugo Torres', 3, 'CMP 10003', 'C-103')")
        db.execSQL("INSERT INTO doctor(usuarioId, fullName, specialtyId, cmp, room) VALUES(5, 'Dra. Sara Mendoza', 4, 'CMP 10004', 'C-104')")
        db.execSQL("INSERT INTO doctor(usuarioId, fullName, specialtyId, cmp, room) VALUES(6, 'Dr. Javier Vargas', 5, 'CMP 10005', 'C-105')")
        db.execSQL("INSERT INTO doctor(usuarioId, fullName, specialtyId, cmp, room) VALUES(7, 'Dra. Claudia Salas', 6, 'CMP 10006', 'C-106')")
        db.execSQL("INSERT INTO doctor(usuarioId, fullName, specialtyId, cmp, room) VALUES(8, 'Dr. Oscar Peralta', 1, 'CMP 10007', 'C-107')")

        // 4. CITAS DE EJEMPLO
        db.execSQL("INSERT INTO appointment(patientId, doctorId, date, time, reason, status) VALUES(9, 1, '2026-03-05', '09:00', 'Chequeo anual', 'Programada')")
        db.execSQL("INSERT INTO appointment(patientId, doctorId, date, time, reason, status) VALUES(10, 2, '2026-03-05', '11:30', 'Consulta pediátrica', 'Programada')")
        db.execSQL("INSERT INTO appointment(patientId, doctorId, date, time, reason, status) VALUES(9, 3, '2026-03-06', '15:00', 'Dolor en el pecho', 'Programada')")
    }

    override fun onUpgrade(db: SQLiteDatabase, old: Int, new: Int) {
        db.execSQL("DROP TABLE IF EXISTS appointment")
        db.execSQL("DROP TABLE IF EXISTS doctor_schedule")
        db.execSQL("DROP TABLE IF EXISTS doctor")
        db.execSQL("DROP TABLE IF EXISTS specialty")
        db.execSQL("DROP TABLE IF EXISTS usuario")
        onCreate(db)
    }
}

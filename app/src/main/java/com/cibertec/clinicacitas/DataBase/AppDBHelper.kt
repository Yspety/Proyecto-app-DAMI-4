package com.cibertec.clinicacitas.DataBase

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class AppDBHelper(context: Context) : SQLiteOpenHelper(context, "app.db", null, 4) { // Versión incrementada a 4

    override fun onCreate(db: SQLiteDatabase) {
        // --- Tabla de Usuarios ---
        db.execSQL("""
            CREATE TABLE usuario(
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                username TEXT NOT NULL UNIQUE,
                password TEXT NOT NULL,
                rol TEXT NOT NULL
            )
        """.trimIndent())

        // --- Tabla de Especialidades ---
        db.execSQL("""
            CREATE TABLE specialty(
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                name TEXT NOT NULL UNIQUE
            )
        """.trimIndent())

        // --- Tabla de Doctores (con vínculo a usuario) ---
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

        // --- Tabla de Citas ---
        db.execSQL("""
            CREATE TABLE appointment(
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                patientName TEXT NOT NULL,
                doctorId INTEGER NOT NULL,
                date TEXT NOT NULL,
                time TEXT NOT NULL,
                reason TEXT NOT NULL,
                status TEXT NOT NULL,
                FOREIGN KEY (doctorId) REFERENCES doctor(id)
            )
        """.trimIndent())

        insertInitialData(db)
    }

    private fun insertInitialData(db: SQLiteDatabase) {
        // Usuarios
        db.execSQL("INSERT INTO usuario(id, username, password, rol) VALUES(1, 'admin','admin123','admin')")
        db.execSQL("INSERT INTO usuario(id, username, password, rol) VALUES(2, 'medico','medico123','medico')")
        db.execSQL("INSERT INTO usuario(id, username, password, rol) VALUES(3, 'usuario','usuario123','usuario')")

        // Especialidades y Doctores
        db.execSQL("INSERT INTO specialty(id, name) VALUES(1, 'Medicina General')")
        db.execSQL("INSERT INTO specialty(id, name) VALUES(2, 'Pediatría')")
        db.execSQL("INSERT INTO specialty(id, name) VALUES(3, 'Cardiología')")

        // El doctor (id=1) se vincula al usuario 'medico' (usuarioId=2)
        db.execSQL("INSERT INTO doctor(id, usuarioId, fullName, specialtyId, cmp, room) VALUES(1, 2, 'Dr. Luis García', 1, 'CMP 23456', 'Consultorio 105')")

        // Citas de ejemplo para el Dr. Luis García (doctorId = 1)
        db.execSQL("INSERT INTO appointment(patientName, doctorId, date, time, reason, status) VALUES('Juan Pérez', 1, '2026-03-10', '10:00', 'Revisión general', 'Programada')")
        db.execSQL("INSERT INTO appointment(patientName, doctorId, date, time, reason, status) VALUES('Ana Gómez', 1, '2026-03-12', '11:30', 'Dolor de garganta', 'Programada')")
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS appointment")
        db.execSQL("DROP TABLE IF EXISTS doctor")
        db.execSQL("DROP TABLE IF EXISTS specialty")
        db.execSQL("DROP TABLE IF EXISTS usuario")
        onCreate(db)
    }
}

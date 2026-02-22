    package com.cibertec.clinicacitas.DataBase

    import android.content.ContentValues
    import android.content.Context
    import android.database.sqlite.SQLiteDatabase
    import android.database.sqlite.SQLiteOpenHelper

    class AppDBHelper(context: Context) : SQLiteOpenHelper(context,"app.db",null,1){


        override fun onCreate(db: SQLiteDatabase) {
            db.execSQL(
                """
                    Create Table usuario(
                    
                    id INTEGER Primary Key autoincrement,
                    username TEXT Not null UNIQUE,
                    password text not null,
                    rol TEXT NOT NULL 
                   
                    )
    
                """.trimIndent()
            )

            db.execSQL("INSERT INTO usuario(username,password, rol) VALUES('admin','admin123','admin')")

            db.execSQL("INSERT INTO usuario(username,password, rol) VALUES('medico','medico123','medico')")

            db.execSQL("INSERT INTO usuario(username,password, rol) VALUES('usuario','usuario123','usuario')")
        }

        override fun onUpgrade(db:SQLiteDatabase,oldVersion:Int, newVersion:Int){
                    db.execSQL("Drop table if exists usuario")
                onCreate(db)
            }

    }
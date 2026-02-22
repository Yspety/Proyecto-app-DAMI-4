package com.cibertec.clinicacitas.DAO

import android.app.DownloadManager
import android.content.ContentValues
import android.content.Context
import com.cibertec.clinicacitas.DataBase.AppDBHelper
import kotlin.coroutines.coroutineContext

class UsuarioDAO(context: Context) {

    private val dbHelper= AppDBHelper(context)

    fun login(username: String , password: String):String? {

        val db= dbHelper.readableDatabase

        val query= "Select rol from Usuario where username =? and password =?"

        val cursor= db.rawQuery(query,arrayOf(username,password))

        var rol: String? =null

        if(cursor.moveToFirst()){
            rol =cursor.getString(0)
        }
        cursor.close()
        db.close()
        return rol
    }

    fun Registrar(username:String,pass:String,rol:String):Long{
        val db= dbHelper.writableDatabase
        val values= ContentValues().apply {

            put("Username",username)
            put("password",pass)
            put("rol",rol)
        }
        val result=db.insert("Usuario",null,values)
        db.close()
        return result
    }




}
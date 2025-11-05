package com.example.ejemplo_sqlite

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class AdminSQLite(context: Context, bdName: String, version: Int): SQLiteOpenHelper(context, bdName, null, version) {

    override fun onCreate(db: SQLiteDatabase?) {
        db?.execSQL("CREATE TABLE alumnos(matricula TEXT PRIMARY KEY, nombre TEXT, carrera TEXT)")
        db?.execSQL("CREATE TABLE materia(codigoM TEXT PRIMARY KEY, nombre TEXT, creditos TEXT)")
        db?.execSQL("CREATE TABLE cursa(matricula TEXT, codigoM TEXT, " +
                "FOREIGN KEY(matricula) REFERENCES alumnos(matricula), " +
                "FOREIGN KEY(codigoM) REFERENCES materia(codigoM))")
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db?.execSQL("DROP TABLE IF EXISTS cursa")
        db?.execSQL("DROP TABLE IF EXISTS alumnos")
        db?.execSQL("DROP TABLE IF EXISTS materia")
        onCreate(db)
    }
}
package com.example.ejemplo_sqlite

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class AdminSQLite (context: Context, bdName: String, version: Int): SQLiteOpenHelper(context, bdName,null, version){
    override fun onCreate(db: SQLiteDatabase?) {
        db?.execSQL("create table alumnos(matricula TEXT,nombre TEXT,carrera TEXT)")
        db?.execSQL("create table materia(codigoM TEXT,nombre TEXT,creditos TEXT)")
        db?.execSQL("create table cursa(matricula TEXT,codigoM TEXT)")
    }

    override fun onUpgrade(
        db: SQLiteDatabase?,
        oldVersion: Int,
        newVersion: Int
    ) {

    }
}
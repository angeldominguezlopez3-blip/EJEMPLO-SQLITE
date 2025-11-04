package com.example.ejemplo_sqlite

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.ejemplo_sqlite.AdminSQLite

class MainActivity2 : AppCompatActivity() {

    var adminSQLite: AdminSQLite? = null
    var conector: SQLiteDatabase? = null
    var fila: Cursor? = null

    lateinit var spn1: Spinner
    lateinit var spn2: Spinner

    lateinit var adaptadorAlumno: ArrayAdapter<String>
    lateinit var adaptadorMateria: ArrayAdapter<String>

    var listaAlumnos: MutableList<Alumno> = mutableListOf()
    var listaMateria: MutableList<Materia> = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main2)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        spn1 = findViewById(R.id.spinnerAlumno)
        spn2 = findViewById(R.id.spinnerMateria)

        // Inicializar las listas antes de usarlas en los adaptadores
        val nombresAlumnos = listaAlumnos.map { it.nombre }.toMutableList()
        val nombresMaterias = listaMateria.map { it.nombre }.toMutableList()

        adaptadorAlumno = ArrayAdapter<String>(this@MainActivity2, android.R.layout.simple_spinner_dropdown_item, nombresAlumnos)
        adaptadorMateria = ArrayAdapter<String>(this@MainActivity2, android.R.layout.simple_spinner_dropdown_item, nombresMaterias)

        spn1.adapter = adaptadorAlumno
        spn2.adapter = adaptadorMateria

        llenarspinnerAlumno(this@MainActivity2, "Escuela", "select * from alumnos", 1)
        llenarspinnerMateria()

        spn1.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, p1: View?, indice: Int, p3: Long) {
                val datosAlert = arrayOfNulls<String>(1)

                if (indice < listaAlumnos.size) {
                    datosAlert[0] = listaAlumnos[indice].nombre

                    val builder = AlertDialog.Builder(this@MainActivity2)

                    builder.setTitle("Alumno seleccionado")
                        .setItems(datosAlert){ dialog, pos ->
                            // Acción al seleccionar
                        }

                    builder.create().show()
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                // No se seleccionó nada
            }
        }
    }

    private fun llenarspinnerAlumno(contexto: Context, bd: String, cadenaSQL: String, version: Int) {
        adminSQLite = AdminSQLite(contexto, bd, null, version)
        conector = adminSQLite?.readableDatabase

        val cursor = conector?.rawQuery(cadenaSQL, null)

        if (cursor != null) {
            while (cursor.moveToNext()) {
                val alumno = Alumno(cursor.getString(0), cursor.getString(1))
                listaAlumnos.add(alumno)
                adaptadorAlumno.add(alumno.nombre)
            }
            cursor.close()
        }
        conector?.close()
        adaptadorAlumno.notifyDataSetChanged()
    }

    private fun llenarspinnerMateria() {
        // Asumiendo que Materia.datos devuelve una lista de materias
        listaMateria = Materia.datos.toMutableList()

        for (i in 0 until listaMateria.size) {
            adaptadorMateria.add(listaMateria[i].nombre)
        }
        adaptadorMateria.notifyDataSetChanged()
    }
}
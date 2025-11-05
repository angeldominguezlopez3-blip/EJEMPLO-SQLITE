package com.example.ejemplo_sqlite

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Spinner
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class MainActivity2 : AppCompatActivity() {

    private lateinit var spn1: Spinner
    private lateinit var spn2: Spinner
    private lateinit var btn1: Button
    private lateinit var btn2: Button

    private lateinit var adaptadorAlumno: ArrayAdapter<String>
    private lateinit var adaptadorMateria: ArrayAdapter<String>

    private var listaAlumnos: MutableList<Alumno> = mutableListOf()
    private var listaMateria: MutableList<Materia> = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main2)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        inicializarVistas()
        configurarSpinners()
        llenarSpinners()
        configurarListeners()
    }

    private fun inicializarVistas() {
        spn1 = findViewById(R.id.spinnerAlumno)
        spn2 = findViewById(R.id.spinnerMateria)
        btn1 = findViewById(R.id.btn1)
        btn2 = findViewById(R.id.btn2)
    }

    private fun configurarSpinners() {
        adaptadorAlumno = ArrayAdapter(this, android.R.layout.simple_spinner_item, mutableListOf<String>())
        adaptadorAlumno.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        adaptadorMateria = ArrayAdapter(this, android.R.layout.simple_spinner_item, mutableListOf<String>())
        adaptadorMateria.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        spn1.adapter = adaptadorAlumno
        spn2.adapter = adaptadorMateria
    }

    private fun llenarSpinners() {
        llenarSpinnerAlumno()
        llenarSpinnerMateria()
    }

    private fun llenarSpinnerAlumno() {
        val adminSQLite = AdminSQLite(this, "Escuela", 1)
        val conector = adminSQLite.readableDatabase
        var cursor = conector.rawQuery("SELECT * FROM alumnos", null)

        listaAlumnos.clear()
        adaptadorAlumno.clear()

        if (cursor != null) {
            while (cursor.moveToNext()) {
                val alumno = Alumno(
                    cursor.getString(0),
                    cursor.getString(1),
                    cursor.getString(2)
                )
                listaAlumnos.add(alumno)
                adaptadorAlumno.add(alumno.nombre)
            }
            cursor.close()
        }
        conector.close()
        adaptadorAlumno.notifyDataSetChanged()
    }

    private fun llenarSpinnerMateria() {
        listaMateria = Materia.datos.toMutableList()
        adaptadorMateria.clear()

        for (materia in listaMateria) {
            adaptadorMateria.add(materia.nombre)
        }
        adaptadorMateria.notifyDataSetChanged()
    }

    private fun configurarListeners() {
        spn1.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                if (position >= 0 && position < listaAlumnos.size) {
                    val alumnoSeleccionado = listaAlumnos[position]
                    // Aquí puedes realizar acciones con el alumno seleccionado
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                // No se seleccionó nada
            }
        }

        btn1.setOnClickListener {
            // Lógica para guardar relación alumno-materia
            guardarRelacion()
        }

        btn2.setOnClickListener {
            // Lógica para mostrar lista
            mostrarLista()
        }
    }

    private fun guardarRelacion() {
        val alumnoPos = spn1.selectedItemPosition
        val materiaPos = spn2.selectedItemPosition

        if (alumnoPos >= 0 && materiaPos >= 0) {
            val alumno = listaAlumnos[alumnoPos]
            val materia = listaMateria[materiaPos]

            // Guardar en tabla cursa
            val adminSQLite = AdminSQLite(this, "Escuela", 1)
            val conector = adminSQLite.writableDatabase

            try {
                val valores = android.content.ContentValues().apply {
                    put("matricula", alumno.matricula)
                    put("codigoM", materia.codigoM)
                }

                conector.insert("cursa", null, valores)
                android.widget.Toast.makeText(this, "Relación guardada", android.widget.Toast.LENGTH_SHORT).show()
            } catch (e: Exception) {
                android.widget.Toast.makeText(this, "Error al guardar: ${e.message}", android.widget.Toast.LENGTH_SHORT).show()
            } finally {
                conector.close()
            }
        }
    }

    private fun mostrarLista() {
        // Implementar lógica para mostrar lista de relaciones
        android.widget.Toast.makeText(this, "Funcionalidad de lista", android.widget.Toast.LENGTH_SHORT).show()
    }
}
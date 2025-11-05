package com.example.ejemplo_sqlite

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class MainActivity : AppCompatActivity() {
    private lateinit var edt1: EditText
    private lateinit var edt2: EditText
    private lateinit var edt3: EditText
    private lateinit var btn1: Button
    private lateinit var btn2: Button
    private lateinit var btn3: Button
    private lateinit var btn4: Button
    private lateinit var btn5: Button
    private lateinit var btn6: Button

    private var datosArray: Array<String?> = arrayOfNulls(0)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        inicializarVistas()

        btn1.setOnClickListener { guardarAlumno() }
        btn2.setOnClickListener { crearAlertDialog() }
        btn3.setOnClickListener { eliminarAlumno() }
        btn4.setOnClickListener { actualizarAlumno() }
        btn5.setOnClickListener { consultarAlumno() }
        btn6.setOnClickListener { consultarAlumnoClass() }
    }

    private fun inicializarVistas() {
        edt1 = findViewById(R.id.edtext1)
        edt2 = findViewById(R.id.edtext2)
        edt3 = findViewById(R.id.edtext3)
        btn1 = findViewById(R.id.btn1)
        btn2 = findViewById(R.id.btn2)
        btn3 = findViewById(R.id.btn3)
        btn4 = findViewById(R.id.btn4)
        btn5 = findViewById(R.id.btn5)
        btn6 = findViewById(R.id.btn6)
    }

    private fun guardarAlumno() {
        val matricula = edt1.text.toString()
        val nombre = edt2.text.toString()
        val carrera = edt3.text.toString()

        if (matricula.isBlank() || nombre.isBlank() || carrera.isBlank()) {
            Toast.makeText(this, "Complete todos los campos", Toast.LENGTH_SHORT).show()
            return
        }

        val datos = ContentValues().apply {
            put("matricula", matricula)
            put("nombre", nombre)
            put("carrera", carrera)
        }

        val adminSQLite = AdminSQLite(this, "Escuela", 1)
        val conector = adminSQLite.writableDatabase

        try {
            conector.insert("alumnos", null, datos)
            Toast.makeText(this, "Alumno guardado", Toast.LENGTH_SHORT).show()
            limpiarCampos()
        } catch (e: Exception) {
            Toast.makeText(this, "Error al guardar: ${e.message}", Toast.LENGTH_SHORT).show()
        } finally {
            conector.close()
        }
    }

    private fun eliminarAlumno() {
        val matricula = edt1.text.toString()

        if (matricula.isBlank()) {
            Toast.makeText(this, "Ingrese una matrícula", Toast.LENGTH_SHORT).show()
            return
        }

        val adminSQLite = AdminSQLite(this, "Escuela", 1)
        val conector = adminSQLite.writableDatabase

        try {
            val filasEliminadas = conector.delete("alumnos", "matricula = ?", arrayOf(matricula))
            if (filasEliminadas > 0) {
                Toast.makeText(this, "Alumno eliminado", Toast.LENGTH_SHORT).show()
                limpiarCampos()
            } else {
                Toast.makeText(this, "No se encontró el alumno", Toast.LENGTH_SHORT).show()
            }
        } catch (e: Exception) {
            Toast.makeText(this, "Error al eliminar: ${e.message}", Toast.LENGTH_SHORT).show()
        } finally {
            conector.close()
        }
    }

    private fun actualizarAlumno() {
        val matricula = edt1.text.toString()
        val nombre = edt2.text.toString()
        val carrera = edt3.text.toString()

        if (matricula.isBlank()) {
            Toast.makeText(this, "Ingrese una matrícula", Toast.LENGTH_SHORT).show()
            return
        }

        val datos = ContentValues().apply {
            put("nombre", nombre)
            put("carrera", carrera)
        }

        val adminSQLite = AdminSQLite(this, "Escuela", 1)
        val conector = adminSQLite.writableDatabase

        try {
            val filasActualizadas = conector.update("alumnos", datos, "matricula = ?", arrayOf(matricula))
            if (filasActualizadas > 0) {
                Toast.makeText(this, "Alumno actualizado", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "No se encontró el alumno", Toast.LENGTH_SHORT).show()
            }
        } catch (e: Exception) {
            Toast.makeText(this, "Error al actualizar: ${e.message}", Toast.LENGTH_SHORT).show()
        } finally {
            conector.close()
        }
    }

    private fun consultarAlumno() {
        val matricula = edt1.text.toString()

        if (matricula.isBlank()) {
            Toast.makeText(this, "Ingrese una matrícula", Toast.LENGTH_SHORT).show()
            return
        }

        val adminSQLite = AdminSQLite(this, "Escuela", 1)
        val conector = adminSQLite.readableDatabase
        var cursor: Cursor? = null

        try {
            cursor = conector.query(
                "alumnos",
                arrayOf("matricula", "nombre", "carrera"),
                "matricula = ?",
                arrayOf(matricula),
                null, null, null
            )

            edt2.text.clear()
            edt3.text.clear()

            if (cursor != null && cursor.moveToFirst()) {
                edt2.setText(cursor.getString(1))
                edt3.setText(cursor.getString(2))
                Toast.makeText(this, "Alumno encontrado", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Alumno no encontrado", Toast.LENGTH_SHORT).show()
            }
        } catch (e: Exception) {
            Toast.makeText(this, "Error en consulta: ${e.message}", Toast.LENGTH_SHORT).show()
        } finally {
            cursor?.close()
            conector.close()
        }
    }

    private fun consultarAlumnoClass() {
        val matricula = edt1.text.toString()

        if (matricula.isBlank()) {
            Toast.makeText(this, "Ingrese una matrícula", Toast.LENGTH_SHORT).show()
            return
        }

        val lista = consultarDatosClass("select * from alumnos where matricula = ?", arrayOf(matricula))
        if (lista.isNotEmpty()) {
            edt2.setText(lista[0].nombre)
            edt3.setText(lista[0].carrera)
            Toast.makeText(this, "Datos cargados", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(this, "No encontrado", Toast.LENGTH_SHORT).show()
        }
    }

    private fun consultarDatosClass(query: String, selectionArgs: Array<String>): MutableList<Alumno> {
        val listaAlumnos: MutableList<Alumno> = mutableListOf()
        val adminSQLite = AdminSQLite(this, "Escuela", 1)
        val conector = adminSQLite.readableDatabase
        var cursor: Cursor? = null

        try {
            cursor = conector.rawQuery(query, selectionArgs)
            if (cursor != null) {
                while (cursor.moveToNext()) {
                    val alumno = Alumno(
                        cursor.getString(0),
                        cursor.getString(1),
                        cursor.getString(2)
                    )
                    listaAlumnos.add(alumno)
                }
            }
        } catch (e: Exception) {
            Toast.makeText(this, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
        } finally {
            cursor?.close()
            conector.close()
        }
        return listaAlumnos
    }

    private fun crearAlertDialog() {
        val alumnos = consultarDatosClass("select * from alumnos", emptyArray())
        val nombres = alumnos.map { it.nombre }.toTypedArray()

        val builder = AlertDialog.Builder(this)
        builder.setTitle("Alumnos Disponibles")
            .setItems(nombres) { _, which ->
                val alumnoSeleccionado = alumnos[which]
                edt1.setText(alumnoSeleccionado.matricula)
                edt2.setText(alumnoSeleccionado.nombre)
                edt3.setText(alumnoSeleccionado.carrera)
                Toast.makeText(this, "Seleccionado: ${alumnoSeleccionado.nombre}", Toast.LENGTH_SHORT).show()
            }
            .setNegativeButton("Cerrar", null)
        builder.show()
    }

    private fun limpiarCampos() {
        edt1.text.clear()
        edt2.text.clear()
        edt3.text.clear()
    }
}
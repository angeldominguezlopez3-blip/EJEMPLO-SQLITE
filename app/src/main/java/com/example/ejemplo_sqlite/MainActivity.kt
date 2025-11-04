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
    lateinit var edt1: EditText
    lateinit var edt2: EditText
    lateinit var edt3: EditText
    lateinit var btn1: Button
    lateinit var btn2: Button
    lateinit var btn3: Button
    lateinit var btn4: Button
    lateinit var btn5: Button
    lateinit var btn6: Button
    lateinit var datosArray: Array<String?>
    //Componentes del SQL
    var adminSQLite: AdminSQLite? = null
    var conector: SQLiteDatabase? = null
    var datos: ContentValues? = null
    var fila: Cursor? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        edt1 = findViewById(R.id.edtext1)
        edt2 = findViewById(R.id.edtext2)
        edt3 = findViewById(R.id.edtext3)
        btn1 = findViewById(R.id.btn1)
        btn2 = findViewById(R.id.btn2)
        btn3 = findViewById(R.id.btn3)
        btn4 = findViewById(R.id.btn4)
        btn5 = findViewById(R.id.btn5)
        btn6 = findViewById(R.id.btn6)
        //Boton de guardar datos
        btn1.setOnClickListener {
            // Guardar alumno
            datos = ContentValues().apply {
                put("matricula", edt1.text.toString())
                put("nombre", edt2.text.toString())
                put("carrera",edt3.text.toString())
            }
            guardarDatos(this@MainActivity, "Escuela", 1, datos)
            Toast.makeText(this, "Guardado", Toast.LENGTH_SHORT).show()
        }

        btn2.setOnClickListener {
            crearAlertDialog()
        }

        btn3.setOnClickListener {
            // Eliminar alumno por matrícula
            val sql = "delete from alumnos where matricula = '${edt1.text.toString()}'"
            actualizarDatos(this@MainActivity, "Escuela", sql, 1)
            Toast.makeText(this, "Eliminado si existía", Toast.LENGTH_SHORT).show()
        }

        btn4.setOnClickListener {
            // Actualizar nombre por matrícula
            val CadenaSQL =
                "update alumnos set nombre = '${edt2.text.toString()}',carrera ='${edt3.text.toString()}' where matricula = '${edt1.text.toString()}'"
            actualizarDatos(this@MainActivity, "Escuela", CadenaSQL, 1)
            Toast.makeText(this, "Actualizado", Toast.LENGTH_SHORT).show()
        }

        btn5.setOnClickListener {
            // Consultar por matrícula y mostrar nombre
            val cursor = consultarDatos(
                this@MainActivity,
                "Escuela",
                "select * from alumnos where matricula = '${edt1.text.toString()}'",
                1
            )
            edt2.text.clear()
            edt3.text.clear()
            if (cursor != null) {
                while (cursor.moveToNext()) {
                    edt2.setText(cursor.getString(1).toString())
                    edt3.setText(cursor.getString(2).toString())
                }
                cursor.close()
            }
            conector?.close()
        }

        btn6.setOnClickListener {
            // Ejemplo: obtener lista de objetos Alumno y mostrar el primero si existe
            val lista = consultarDatosClass(
                this@MainActivity,
                "Escuela",
                "select * from alumnos where matricula = '${edt1.text.toString()}'",
                1
            )
            if (lista.isNotEmpty()) {
                edt2.setText(lista[0].nombre)
                edt3.setText(lista[0].carrera)
            } else {
                Toast.makeText(this, "No encontrado", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun guardarDatos(contexto: Context, bd: String, version: Int, dataSet: ContentValues?) {
        adminSQLite = AdminSQLite(contexto, bd, version)
        conector = adminSQLite?.writableDatabase
        conector?.insert("alumnos", null, dataSet)
        conector?.close()
    }

    private fun consultarDatos(contexto: Context, bd: String, CadenaSQL: String, version: Int): Cursor? {
        adminSQLite = AdminSQLite(contexto, bd, version)
        conector = adminSQLite?.readableDatabase

        fila = conector?.rawQuery(CadenaSQL, null)
        if (fila != null) {
            datosArray = arrayOfNulls(fila!!.count)
            for (i in 0 until fila!!.count) {
                fila!!.moveToPosition(i)
                datosArray[i] = fila!!.getString(1)
            }
            // posicionar antes de la primera fila para que quien reciba el cursor pueda usar moveToNext()
            fila!!.moveToPosition(-1)
        } else {
            datosArray = arrayOfNulls(0)
        }
        return fila
    }

    private fun consultarDatosClass(contexto: Context, bd: String, CadenaSQL: String, version: Int): MutableList<Alumno> {
        val listaAlumnos: MutableList<Alumno> = mutableListOf()
        adminSQLite = AdminSQLite(contexto, bd, version)
        conector = adminSQLite?.readableDatabase

        val cursor = conector?.rawQuery(CadenaSQL, null)
        if (cursor != null) {
            while (cursor.moveToNext()) {
                val alumno = Alumno(cursor.getString(0), cursor.getString(1))
                listaAlumnos.add(alumno)
            }
            cursor.close()
        }
        conector?.close()
        return listaAlumnos
    }

    private fun crearAlertDialog() {
        val builder = AlertDialog.Builder(this@MainActivity)
        val datosbd = consultarDatos(this@MainActivity, "Escuela", "select * from alumnos", 1)

        val infoAlert = if (::datosArray.isInitialized) datosArray else arrayOfNulls<String>(0)

        builder.setTitle("Alumnos Disponibles")
            .setItems(infoAlert) { _, which ->
                // Muestra nombre seleccionado en edt2
                edt2.setText(infoAlert[which])
                Toast.makeText(this@MainActivity, infoAlert[which], Toast.LENGTH_SHORT).show()
            }
        builder.show()

        datosbd?.close()
        conector?.close()
    }

    public fun actualizarDatos(contexto: Context, bd: String, CadenaSQL: String, version: Int) {
        adminSQLite = AdminSQLite(contexto, bd, version)
        conector = adminSQLite?.writableDatabase
        // Para operaciones DDL/DML sin resultado
        conector?.execSQL(CadenaSQL)
        conector?.close()
    }
}

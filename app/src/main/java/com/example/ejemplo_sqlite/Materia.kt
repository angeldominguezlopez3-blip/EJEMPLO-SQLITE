package com.example.ejemplo_sqlite

data class Materia(
    val codigoM: String,
    val nombre: String,
    val creditos: String
){
    companion object {
        val datos
            get() = arrayListOf(
                Materia(
                    codigoM = "1234",
                    nombre = "Desarrollo de Aplicaciones para Moviles",
                    creditos = "5"
                ),
                Materia(
                    codigoM = "5678",
                    nombre = "Legislacion Informatica",
                    creditos = "4"
                ),
                Materia(
                    codigoM = "9012",
                    nombre = "Calidad de Software",
                    creditos = "5"
                )
            )
    }
}

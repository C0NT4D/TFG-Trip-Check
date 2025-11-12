package com.example.myapplication

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val editOrigen = findViewById<EditText>(R.id.editOrigen)
        val editDestino = findViewById<EditText>(R.id.editDestino)
        val editFechaIda = findViewById<EditText>(R.id.editFechaIda)
        val editFechaVuelta = findViewById<EditText>(R.id.editFechaVuelta)
        val btnBuscarVuelos = findViewById<Button>(R.id.btnBuscarVuelos)

        val bottomNavigation = findViewById<BottomNavigationView>(R.id.bottom_navigation)

        btnBuscarVuelos.setOnClickListener {
            val origen = editOrigen.text.toString().trim()
            val destino = editDestino.text.toString().trim()
            val fechaIda = editFechaIda.text.toString().trim()

            if (origen.isNotEmpty() || destino.isNotEmpty() || fechaIda.isNotEmpty()) {
                val intent = Intent(this, Vuelos::class.java).apply {
                    putExtra("EXTRA_ORIGEN", origen)
                    putExtra("EXTRA_DESTINO", destino)
                    putExtra("EXTRA_FECHA_IDA", fechaIda)
                }
                startActivity(intent)
            } else {
                Toast.makeText(this, "Introduce al menos un criterio de búsqueda", Toast.LENGTH_SHORT).show()
            }
        }

        bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.navigation_vuelos -> {
                    startActivity(Intent(this, Vuelos::class.java))
                    true
                }
                R.id.navigation_hoteles -> {
                    Toast.makeText(this, "Hoteles seleccionado", Toast.LENGTH_SHORT).show()
                    true
                }
                R.id.navigation_reservas -> {
                    Toast.makeText(this, "Mis Viajes seleccionado", Toast.LENGTH_SHORT).show()
                    true
                }
                else -> false
            }
        }
    }
}

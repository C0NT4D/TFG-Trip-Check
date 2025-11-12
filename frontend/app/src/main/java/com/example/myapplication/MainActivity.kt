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

        // Enlaces a los componentes de la interfaz de vuelos
        val editOrigen = findViewById<EditText>(R.id.editOrigen)
        val editDestino = findViewById<EditText>(R.id.editDestino)
        val editFechaIda = findViewById<EditText>(R.id.editFechaIda)
        val editFechaVuelta = findViewById<EditText>(R.id.editFechaVuelta)
        val btnBuscarVuelos = findViewById<Button>(R.id.btnBuscarVuelos)

        val bottomNavigation = findViewById<BottomNavigationView>(R.id.bottom_navigation)

        btnBuscarVuelos.setOnClickListener {
            // Lógica de ejemplo para el botón de búsqueda
            val origen = editOrigen.text.toString()
            val destino = editDestino.text.toString()
            val fechaIda = editFechaIda.text.toString()
            val fechaVuelta = editFechaVuelta.text.toString()

            if (origen.isNotEmpty() && destino.isNotEmpty() && fechaIda.isNotEmpty()) {
                Toast.makeText(this, "Buscando vuelos de $origen a $destino", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Por favor, rellena origen, destino y fecha de ida", Toast.LENGTH_SHORT).show()
            }
        }

        bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.navigation_vuelos -> {
                    Toast.makeText(this, "Vuelos seleccionado", Toast.LENGTH_SHORT).show()
                    // Aquí iría la lógica para mostrar el fragmento de vuelos
                    true
                }
                R.id.navigation_hoteles -> {
                    Toast.makeText(this, "Hoteles seleccionado", Toast.LENGTH_SHORT).show()
                    // Aquí iría la lógica para mostrar el fragmento de hoteles
                    true
                }
                R.id.navigation_reservas -> {
                    // Abrir la actividad MisViajes
                    val intent = Intent(this, MisViajes::class.java)
                    startActivity(intent)
                    true
                }
                else -> false
            }
        }
    }
}

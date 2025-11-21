package com.example.myapplication

import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.bottomnavigation.BottomNavigationView
import java.util.Calendar

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val editOrigen = findViewById<EditText>(R.id.editOrigen)
        val editDestino = findViewById<EditText>(R.id.editDestino)
        val editFechaIda = findViewById<EditText>(R.id.editFechaIda)
        val btnBuscarVuelos = findViewById<Button>(R.id.btnBuscarVuelos)
        val bottomNavigation = findViewById<BottomNavigationView>(R.id.bottom_navigation)

        editFechaIda.isFocusable = false
        editFechaIda.setOnClickListener {
            val calendar = Calendar.getInstance()
            val year = calendar.get(Calendar.YEAR)
            val month = calendar.get(Calendar.MONTH)
            val day = calendar.get(Calendar.DAY_OF_MONTH)

            val datePickerDialog = DatePickerDialog(this, {
                    _, selectedYear, selectedMonth, selectedDay ->
                val apiDate = String.format("%d-%02d", selectedYear, selectedMonth + 1)

                val displayDate = String.format("%02d/%02d/%d", selectedDay, selectedMonth + 1, selectedYear)

                editFechaIda.setText(displayDate)
                editFechaIda.tag = apiDate
            }, year, month, day)


            datePickerDialog.show()
        }

        btnBuscarVuelos.setOnClickListener {
            val origen = editOrigen.text.toString().trim()
            val destino = editDestino.text.toString().trim()


            val fechaIdaApi = editFechaIda.tag as? String

            if (origen.isNotEmpty() && destino.isNotEmpty()) {
                val intent = Intent(this, Vuelos::class.java).apply {
                    putExtra("EXTRA_ORIGEN", origen)
                    putExtra("EXTRA_DESTINO", destino)
                    if (!fechaIdaApi.isNullOrEmpty()) {
                        putExtra("EXTRA_FECHA_IDA", fechaIdaApi)
                    }
                }
                startActivity(intent)
            } else {
                Toast.makeText(this, "El origen y el destino son obligatorios", Toast.LENGTH_SHORT).show()
            }
        }


        bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.navigation_vuelos -> {

                    true
                }
                R.id.navigation_hoteles -> {
                    Toast.makeText(this, "Hoteles seleccionado", Toast.LENGTH_SHORT).show()
                    true
                }
                R.id.navigation_reservas -> {
                    startActivity(Intent(this, HistorialReservasActivity::class.java))
                    true
                }
                else -> false
            }
        }
    }

    override fun onResume() {
        super.onResume()
        val bottomNavigation = findViewById<BottomNavigationView>(R.id.bottom_navigation)
        bottomNavigation.selectedItemId = R.id.navigation_vuelos
    }
}

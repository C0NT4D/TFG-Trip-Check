package com.example.myapplication

import android.app.DatePickerDialog
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.textfield.TextInputEditText
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class BuscarVuelosActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_buscar_vuelos)

        val etOrigen = findViewById<TextInputEditText>(R.id.etOrigen)
        val etDestino = findViewById<TextInputEditText>(R.id.etDestino)
        val etFecha = findViewById<TextInputEditText>(R.id.etFecha)
        val btnBuscarVuelos = findViewById<Button>(R.id.btnBuscarVuelos)

        // --- Lógica para el selector de fecha ---
        etFecha.setOnClickListener {
            mostrarDialogoDeFecha(etFecha)
        }

        // --- Lógica para el botón de búsqueda ---
        btnBuscarVuelos.setOnClickListener {
            val origen = etOrigen.text.toString().trim()
            val destino = etDestino.text.toString().trim()
            val fecha = etFecha.text.toString().trim()

            if (origen.isEmpty() || destino.isEmpty() || fecha.isEmpty()) {
                Toast.makeText(this, "Por favor, rellena todos los campos", Toast.LENGTH_SHORT).show()
            } else {
                // TODO: Aquí irá la lógica para pasar a la siguiente pantalla con los resultados
                Toast.makeText(this, "Buscando vuelos de $origen a $destino el $fecha", Toast.LENGTH_LONG).show()

                // Ejemplo de cómo pasarías a la siguiente actividad (cuando la tengas)
                // val intent = Intent(this, VuelosResultadosActivity::class.java).apply {
                //     putExtra("EXTRA_ORIGEN", origen)
                //     putExtra("EXTRA_DESTINO", destino)
                //     putExtra("EXTRA_FECHA", fecha)
                // }
                // startActivity(intent)
            }
        }
    }

    private fun mostrarDialogoDeFecha(etFecha: TextInputEditText) {
        val calendario = Calendar.getInstance()
        val dateSetListener = DatePickerDialog.OnDateSetListener { _, year, month, dayOfMonth ->
            calendario.set(Calendar.YEAR, year)
            calendario.set(Calendar.MONTH, month)
            calendario.set(Calendar.DAY_OF_MONTH, dayOfMonth)

            val formato = "dd/MM/yyyy"
            val sdf = SimpleDateFormat(formato, Locale.getDefault())
            etFecha.setText(sdf.format(calendario.time))
        }

        DatePickerDialog(
            this,
            dateSetListener,
            calendario.get(Calendar.YEAR),
            calendario.get(Calendar.MONTH),
            calendario.get(Calendar.DAY_OF_MONTH)
        ).show()
    }
}

package com.example.myapplication

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.network.FlightData
import com.example.myapplication.network.RetrofitClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class Vuelos : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var vuelosAdapter: VuelosAdapter // Ahora esta referencia es correcta
    private val token = "ae5a1eb3f8e274fedb604ed83f9516aa"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_vuelos)

        recyclerView = findViewById(R.id.recyclerViewVuelos)
        setupRecyclerView()

        val origen = intent.getStringExtra("EXTRA_ORIGEN")
        val destino = intent.getStringExtra("EXTRA_DESTINO")
        val fechaIda = intent.getStringExtra("EXTRA_FECHA_IDA")

        if (!origen.isNullOrEmpty() && !destino.isNullOrEmpty()) {
            cargarVuelos(origen, destino, fechaIda)
        } else {
            Toast.makeText(this, "Usa el formulario principal para buscar un vuelo", Toast.LENGTH_LONG).show()
        }
    }

    private fun setupRecyclerView() {
        vuelosAdapter = VuelosAdapter(emptyList())
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = vuelosAdapter
    }

    private fun cargarVuelos(origen: String, destino: String, fecha: String?) {
        lifecycleScope.launch(Dispatchers.IO) {
            try {
                // Pasamos el token como primer argumento, que se usará como Header
                val response = RetrofitClient.publicFlightService.getCalendarFlights(token, origen, destino, fecha)

                withContext(Dispatchers.Main) {
                    // dentro de withContext(Dispatchers.Main) { ... }
                    if (response.success && response.data.isNotEmpty()) {
                        // Convertimos los valores del mapa (los vuelos) en una lista
                        val listaDeVuelos = response.data.values.toList()
                        vuelosAdapter.updateData(listaDeVuelos)
                    } else {
                        vuelosAdapter.updateData(emptyList())
                        Toast.makeText(this@Vuelos, "No se encontraron vuelos para esa ruta y fecha", Toast.LENGTH_LONG).show()
                    }

                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@Vuelos, "Error de conexión: ${e.message}", Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    // --- COMIENZA LA DEFINICIÓN DE VUELOSADAPTER (MOVIDA AQUÍ) ---
    inner class VuelosAdapter(private var vuelos: List<FlightData>) : RecyclerView.Adapter<VuelosAdapter.VueloViewHolder>() {

        inner class VueloViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            val txtOrigenDestino: TextView = itemView.findViewById(R.id.txtOrigenDestino)
            val txtFechas: TextView = itemView.findViewById(R.id.txtFechas)
            val txtPrecio: TextView = itemView.findViewById(R.id.txtPrecio)
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VueloViewHolder {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.list_item_vuelo, parent, false)
            return VueloViewHolder(view)
        }

        override fun onBindViewHolder(holder: VueloViewHolder, position: Int) {
            val vuelo = vuelos[position]

            holder.txtOrigenDestino.text = "${vuelo.origin} → ${vuelo.destination}"
            holder.txtPrecio.text = String.format("%d €", vuelo.price)

            val formatter = DateTimeFormatter.ofPattern("dd MMM yyyy, HH:mm")
            try {
                val fechaSalida = LocalDateTime.parse(vuelo.departureAt, DateTimeFormatter.ISO_DATE_TIME)
                holder.txtFechas.text = "Salida: ${fechaSalida.format(formatter)} (${vuelo.airline})"
            } catch (e: Exception) {
                holder.txtFechas.text = "Salida: ${vuelo.departureAt}"
            }
        }

        override fun getItemCount(): Int = vuelos.size

        fun updateData(newVuelos: List<FlightData>) {
            this.vuelos = newVuelos
            notifyDataSetChanged()
        }
    }
    // --- FIN DE LA DEFINICIÓN DE VUELOSADAPTER ---
}

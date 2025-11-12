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
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class Vuelos : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var vuelosAdapter: VuelosAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_vuelos)

        recyclerView = findViewById(R.id.recyclerViewVuelos)
        setupRecyclerView()

        // Recibir los datos del filtro
        val origen = intent.getStringExtra("EXTRA_ORIGEN")
        val destino = intent.getStringExtra("EXTRA_DESTINO")
        val fechaIda = intent.getStringExtra("EXTRA_FECHA_IDA")

        cargarVuelos(origen, destino, fechaIda)
    }

    private fun setupRecyclerView() {
        vuelosAdapter = VuelosAdapter(emptyList())
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = vuelosAdapter
    }

    private fun cargarVuelos(origen: String?, destino: String?, fechaIda: String?) {
        lifecycleScope.launch(Dispatchers.IO) {
            try {
                val todosLosVuelos = RetrofitClient.apiService.getVuelos()

                // Aplicar filtros
                val vuelosFiltrados = todosLosVuelos.filter { vuelo ->
                    val filtroOrigen = origen.isNullOrEmpty() || vuelo.origen.contains(origen, ignoreCase = true)
                    val filtroDestino = destino.isNullOrEmpty() || vuelo.destino.contains(destino, ignoreCase = true)
                    val filtroFecha = fechaIda.isNullOrEmpty() || vuelo.fechaSalida.startsWith(fechaIda)
                    filtroOrigen && filtroDestino && filtroFecha
                }

                withContext(Dispatchers.Main) {
                    vuelosAdapter.updateData(vuelosFiltrados)
                    if (vuelosFiltrados.isEmpty()) {
                        Toast.makeText(this@Vuelos, "No se encontraron vuelos con esos criterios", Toast.LENGTH_LONG).show()
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@Vuelos, "Error al cargar los vuelos: ${e.message}", Toast.LENGTH_LONG).show()
                }
            }
        }
    }
}

class VuelosAdapter(private var vuelos: List<Vuelo>) : RecyclerView.Adapter<VuelosAdapter.VueloViewHolder>() {

    class VueloViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
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

        holder.txtOrigenDestino.text = "${vuelo.origen} → ${vuelo.destino}"
        holder.txtPrecio.text = String.format("%.2f €", vuelo.precio)

        val formatter = DateTimeFormatter.ofPattern("dd MMM yyyy, HH:mm")
        try {
            val fechaSalida = LocalDateTime.parse(vuelo.fechaSalida, DateTimeFormatter.ISO_DATE_TIME)
            holder.txtFechas.text = "Salida: ${fechaSalida.format(formatter)}"
        } catch (e: Exception) {
            holder.txtFechas.text = "Salida: ${vuelo.fechaSalida}"
        }
    }

    override fun getItemCount(): Int = vuelos.size

    fun updateData(newVuelos: List<Vuelo>) {
        this.vuelos = newVuelos
        notifyDataSetChanged()
    }
}
package com.example.myapplication

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class MisViajes : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: ReservasAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_mis_viajes)

        recyclerView = findViewById(R.id.recycler_view_reservas)
        recyclerView.layoutManager = LinearLayoutManager(this)

        // Lista de ejemplo. La reemplazaremos con los datos reales de la API
        val reservasDeEjemplo = listOf("Reserva 1", "Reserva 2", "Reserva 3")

        adapter = ReservasAdapter(reservasDeEjemplo)
        recyclerView.adapter = adapter
    }
}

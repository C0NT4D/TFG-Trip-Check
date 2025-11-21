package com.example.myapplication

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.network.RetrofitClient
import kotlinx.coroutines.launch

class MisVuelosFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: ReservasVuelosAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_mis_vuelos, container, false)
        recyclerView = view.findViewById(R.id.recycler_view_vuelos)
        setupRecyclerView()
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        fetchReservas()
    }

    private fun setupRecyclerView() {
        recyclerView.layoutManager = LinearLayoutManager(context)
        adapter = ReservasVuelosAdapter(emptyList())
        recyclerView.adapter = adapter
    }

    private fun fetchReservas() {
        val userId = context?.let { SessionManager.getUserId(it) }

        if (userId == null || userId == -1L) {
            Toast.makeText(context, "Inicia sesión para ver tus reservas", Toast.LENGTH_LONG).show()
            return
        }

        lifecycleScope.launch {
            try {
                val reservas = RetrofitClient.myBackendService.getReservasVuelosUsuario(userId)
                adapter.updateData(reservas)
            } catch (e: Exception) {
                Log.e("MisVuelosFragment", "Error al obtener las reservas de vuelos", e)
                Toast.makeText(context, "Error al cargar las reservas", Toast.LENGTH_SHORT).show()
            }
        }
    }
}

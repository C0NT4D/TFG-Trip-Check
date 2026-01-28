package com.example.myapplication

import android.app.AlertDialog
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
import com.example.myapplication.network.Reserva
import com.example.myapplication.network.RetrofitClient
import kotlinx.coroutines.launch

class MisVuelosFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: ReservasVuelosAdapter
    private val listaDeReservas = mutableListOf<Reserva>()

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
        adapter = ReservasVuelosAdapter(listaDeReservas) { reserva ->
            mostrarDialogoDeConfirmacion(reserva)
        }
        recyclerView.layoutManager = LinearLayoutManager(context)
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
                listaDeReservas.clear()
                listaDeReservas.addAll(reservas)
                adapter.notifyDataSetChanged() // Notificar al adapter que los datos han cambiado
            } catch (e: Exception) {
                Log.e("MisVuelosFragment", "Error al obtener las reservas de vuelos", e)
                Toast.makeText(context, "Error al cargar las reservas", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun mostrarDialogoDeConfirmacion(reserva: Reserva) {
        AlertDialog.Builder(context)
            .setTitle("Confirmar Eliminación")
            .setMessage("¿Estás seguro de que quieres eliminar esta reserva?")
            .setPositiveButton("Eliminar") { _, _ ->
                eliminarReserva(reserva)
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    private fun eliminarReserva(reserva: Reserva) {
        if (reserva.idReserva == null) return

        lifecycleScope.launch {
            try {
                val response = RetrofitClient.myBackendService.deleteReserva(reserva.idReserva)
                if (response.isSuccessful) {
                    Toast.makeText(context, "Reserva eliminada", Toast.LENGTH_SHORT).show()
                    val position = listaDeReservas.indexOf(reserva)
                    if (position != -1) {
                        listaDeReservas.removeAt(position)
                        adapter.notifyItemRemoved(position)
                    }
                } else {
                    Toast.makeText(context, "Error al eliminar la reserva", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Log.e("MisVuelosFragment", "Error al eliminar la reserva", e)
                Toast.makeText(context, "Error de conexión", Toast.LENGTH_SHORT).show()
            }
        }
    }
}

package com.example.myapplication

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class ReservasAdapter(private val reservas: List<Any>) : RecyclerView.Adapter<ReservasAdapter.ReservaViewHolder>() {

    // De momento, el tipo de dato es 'Any'. Lo cambiaremos cuando tengamos los modelos de datos

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReservaViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_reserva, parent, false)
        return ReservaViewHolder(view)
    }

    override fun onBindViewHolder(holder: ReservaViewHolder, position: Int) {
        val reserva = reservas[position]
        // Aquí configuraremos la vista con los datos de la reserva
        // holder.bind(reserva)
    }

    override fun getItemCount(): Int = reservas.size

    class ReservaViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        // Aquí irían los TextView para el hotel, vuelo, etc.

        fun bind(reserva: Any) {
            // Ejemplo:
            // itemView.findViewById<TextView>(R.id.txtNombreHotel).text = reserva.hotel.nombre
        }
    }
}

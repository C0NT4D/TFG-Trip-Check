package com.example.myapplication

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.network.Reserva

class ReservasVuelosAdapter(private var reservas: List<Reserva>) : RecyclerView.Adapter<ReservasVuelosAdapter.ReservaViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReservaViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_reserva_vuelo, parent, false)
        return ReservaViewHolder(view)
    }

    override fun onBindViewHolder(holder: ReservaViewHolder, position: Int) {
        holder.bind(reservas[position])
    }

    override fun getItemCount() = reservas.size

    fun updateData(newReservas: List<Reserva>) {
        reservas = newReservas
        notifyDataSetChanged()
    }

    class ReservaViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val fechaReservaTextView: TextView = itemView.findViewById(R.id.text_fecha_reserva)
        private val estadoReservaTextView: TextView = itemView.findViewById(R.id.text_estado_reserva)

        fun bind(reserva: Reserva) {
            fechaReservaTextView.text = "Fecha de Reserva: ${reserva.fechaReserva}"
            estadoReservaTextView.text = reserva.estado
        }
    }
}

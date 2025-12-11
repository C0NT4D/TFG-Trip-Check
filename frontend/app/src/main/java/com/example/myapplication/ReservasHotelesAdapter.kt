package com.example.myapplication

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.network.Reserva

class ReservasHotelesAdapter(private var reservas: List<Reserva>) : RecyclerView.Adapter<ReservasHotelesAdapter.ReservaHotelViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReservaHotelViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_reserva_hotel, parent, false)
        return ReservaHotelViewHolder(view)
    }

    override fun onBindViewHolder(holder: ReservaHotelViewHolder, position: Int) {
        holder.bind(reservas[position])
    }

    override fun getItemCount() = reservas.size

    fun updateData(newReservas: List<Reserva>) {
        reservas = newReservas
        notifyDataSetChanged()
    }

    class ReservaHotelViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val fechaReservaTextView: TextView = itemView.findViewById(R.id.text_fecha_reserva_hotel)
        private val estadoReservaTextView: TextView = itemView.findViewById(R.id.text_estado_reserva_hotel)

        fun bind(reserva: Reserva) {
            fechaReservaTextView.text = "Fecha de Reserva: ${reserva.fechaReserva}"
            estadoReservaTextView.text = reserva.estado
        }
    }
}

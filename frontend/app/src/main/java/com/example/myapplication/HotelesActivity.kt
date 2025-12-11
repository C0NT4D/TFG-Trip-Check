package com.example.myapplication

import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.network.HotelPropertyWrapper
import com.example.myapplication.network.RetrofitClient
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.squareup.picasso.Picasso
import kotlinx.coroutines.launch
import java.util.Calendar

class HotelesActivity : AppCompatActivity() {

    private lateinit var editDestinoHotel: EditText
    private lateinit var editFechaCheckIn: EditText
    private lateinit var editFechaCheckOut: EditText
    private lateinit var btnBuscarHoteles: Button
    private lateinit var recyclerViewHoteles: RecyclerView
    private lateinit var hotelesAdapter: HotelesAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_hoteles)

        editDestinoHotel = findViewById(R.id.editDestinoHotel)
        editFechaCheckIn = findViewById(R.id.editFechaCheckIn)
        editFechaCheckOut = findViewById(R.id.editFechaCheckOut)
        btnBuscarHoteles = findViewById(R.id.btnBuscarHoteles)
        recyclerViewHoteles = findViewById(R.id.recyclerViewHoteles)
        val bottomNavigation = findViewById<BottomNavigationView>(R.id.bottom_navigation_hoteles)

        setupRecyclerView()

        editFechaCheckIn.setOnClickListener { showDatePickerDialog(editFechaCheckIn) }
        editFechaCheckOut.setOnClickListener { showDatePickerDialog(editFechaCheckOut) }

        btnBuscarHoteles.setOnClickListener {
            val destino = editDestinoHotel.text.toString().trim()
            val checkIn = editFechaCheckIn.text.toString().trim()
            val checkOut = editFechaCheckOut.text.toString().trim()

            if (destino.isNotEmpty() && checkIn.isNotEmpty() && checkOut.isNotEmpty()) {
                buscarDestinoYHoteles(destino, checkIn, checkOut)
            } else {
                Toast.makeText(this, "Por favor, completa todos los campos", Toast.LENGTH_SHORT).show()
            }
        }


        bottomNavigation.selectedItemId = R.id.navigation_hoteles
        bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.navigation_vuelos -> {
                    startActivity(Intent(this, MainActivity::class.java))
                    true
                }
                R.id.navigation_hoteles -> {
                    // No hacer nada, ya estamos aquí
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

    private fun setupRecyclerView() {
        hotelesAdapter = HotelesAdapter(emptyList()) { hotelSeleccionado ->
            val ciudadBuscada = editDestinoHotel.text.toString().trim()
            val intent = Intent(this, ReservaHotelActivity::class.java).apply {
                putExtra("HOTEL_DATA", hotelSeleccionado)
                putExtra("HOTEL_CIUDAD", ciudadBuscada)
            }
            startActivity(intent)
        }
        recyclerViewHoteles.layoutManager = LinearLayoutManager(this)
        recyclerViewHoteles.adapter = hotelesAdapter
    }

    private fun buscarDestinoYHoteles(ciudad: String, checkIn: String, checkOut: String) {
        lifecycleScope.launch {
            try {
                val destResponse = RetrofitClient.bookingService.searchDestination(cityName = ciudad)
                if (destResponse.data.isEmpty() || destResponse.data[0].searchType == null) {
                    Toast.makeText(this@HotelesActivity, "No se encontró un destino con ese nombre", Toast.LENGTH_SHORT).show()
                    return@launch
                }
                val primerDestino = destResponse.data[0]
                val destId = primerDestino.destId
                val searchType = primerDestino.searchType!!

                val hotelsResponse = RetrofitClient.bookingService.searchHotels(
                    destId = destId,
                    searchType = searchType,
                    arrivalDate = checkIn,
                    departureDate = checkOut
                )

                if (hotelsResponse.data.hotels.isNotEmpty()) {
                    hotelesAdapter.updateData(hotelsResponse.data.hotels)
                } else {
                    hotelesAdapter.updateData(emptyList())
                    Toast.makeText(this@HotelesActivity, "No se encontraron hoteles para esas fechas", Toast.LENGTH_SHORT).show()
                }

            } catch (e: Exception) {
                Toast.makeText(this@HotelesActivity, "Error en la búsqueda: ${e.message}", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun showDatePickerDialog(editText: EditText) {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(this, {
                _, selectedYear, selectedMonth, selectedDay ->
            val selectedDate = String.format("%d-%02d-%02d", selectedYear, selectedMonth + 1, selectedDay)
            editText.setText(selectedDate)
        }, year, month, day)

        datePickerDialog.show()
    }
}

class HotelesAdapter(
    private var hoteles: List<HotelPropertyWrapper>,
    private val onItemClicked: (HotelPropertyWrapper) -> Unit
) : RecyclerView.Adapter<HotelesAdapter.HotelViewHolder>() {

    inner class HotelViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val imgHotel: ImageView = itemView.findViewById(R.id.imgHotel)
        private val txtHotelName: TextView = itemView.findViewById(R.id.txtHotelName)
        private val txtHotelPrice: TextView = itemView.findViewById(R.id.txtHotelPrice)
        private val txtHotelReviewScore: TextView = itemView.findViewById(R.id.txtHotelReviewScore)

        fun bind(hotelWrapper: HotelPropertyWrapper) {
            val hotel = hotelWrapper.property
            itemView.setOnClickListener { onItemClicked(hotelWrapper) }

            txtHotelName.text = hotel.name
            txtHotelPrice.text = String.format("%.2f %s", hotel.priceBreakdown.grossPrice.value, hotel.priceBreakdown.grossPrice.currency)
            txtHotelReviewScore.text = hotel.reviewScore?.toString() ?: "-"

            if (hotel.photoUrls != null && hotel.photoUrls.isNotEmpty()) {
                Picasso.get().load(hotel.photoUrls[0]).into(imgHotel)
            } else {
                imgHotel.setImageResource(R.drawable.ic_launcher_background)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HotelViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.list_item_hotel, parent, false)
        return HotelViewHolder(view)
    }

    override fun onBindViewHolder(holder: HotelViewHolder, position: Int) {
        holder.bind(hoteles[position])
    }

    override fun getItemCount(): Int = hoteles.size

    fun updateData(newHoteles: List<HotelPropertyWrapper>) {
        this.hoteles = newHoteles
        notifyDataSetChanged()
    }
}

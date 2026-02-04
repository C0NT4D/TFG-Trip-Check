package com.example.myapplication

import android.app.DatePickerDialog
import android.content.Intent
import android.os.Build
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
import com.example.myapplication.network.FlightData
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
    private var vueloSeleccionado: FlightData? = null
    private lateinit var txtNoGraciasHotel: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_hoteles)

        editDestinoHotel = findViewById(R.id.editDestinoHotel)
        editFechaCheckIn = findViewById(R.id.editFechaCheckIn)
        editFechaCheckOut = findViewById(R.id.editFechaCheckOut)
        btnBuscarHoteles = findViewById(R.id.btnBuscarHoteles)
        recyclerViewHoteles = findViewById(R.id.recyclerViewHoteles)
        txtNoGraciasHotel = findViewById(R.id.txtNoGraciasHotel)
        val bottomNavigation = findViewById<BottomNavigationView>(R.id.bottom_navigation_hoteles)

        setupRecyclerView()

        vueloSeleccionado = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getSerializableExtra("EXTRA_VUELO_DATA", FlightData::class.java)
        } else {
            @Suppress("DEPRECATION")
            intent.getSerializableExtra("EXTRA_VUELO_DATA") as? FlightData
        }

        val destinoVuelo = intent.getStringExtra("EXTRA_DESTINO")
        val fechaCheckInVuelo = intent.getStringExtra("EXTRA_CHECK_IN_DATE")

        if (destinoVuelo != null && fechaCheckInVuelo != null) {
            editDestinoHotel.setText(destinoVuelo)
            editFechaCheckIn.setText(fechaCheckInVuelo)
        }

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

        txtNoGraciasHotel.setOnClickListener {
            if (SessionManager.isLoggedIn(this)) {
                vueloSeleccionado?.let { vuelo ->
                    confirmarVueloYSalir(vuelo)
                } ?: run {
                    Toast.makeText(this, "No hay información de vuelo para guardar", Toast.LENGTH_SHORT).show()
                    startActivity(Intent(this, MainActivity::class.java))
                }
            } else {
                Toast.makeText(this, "Debes iniciar sesión para confirmar el vuelo", Toast.LENGTH_LONG).show()
                startActivity(Intent(this, Login::class.java))
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

    private fun confirmarVueloYSalir(vueloData: FlightData) {
        val userId = SessionManager.getUserId(this)

        lifecycleScope.launch {
            try {
                // Formatting dates similar to ReservaVueloActivity
                val departureZonedDateTime = java.time.ZonedDateTime.parse(vueloData.departureAt)
                val arrivalString = if (vueloData.returnAt.isNullOrEmpty()) vueloData.departureAt else vueloData.returnAt
                val arrivalZonedDateTime = java.time.ZonedDateTime.parse(arrivalString)

                val departureLocalDateTime = departureZonedDateTime.toLocalDateTime()
                val arrivalLocalDateTime = arrivalZonedDateTime.toLocalDateTime()

                val formatter = java.time.format.DateTimeFormatter.ISO_LOCAL_DATE_TIME
                val fechaSalidaBackend = departureLocalDateTime.format(formatter)
                val fechaLlegadaBackend = arrivalLocalDateTime.format(formatter)

                val priceInEuros = vueloData.price / 100.0

                val nuevoVuelo = com.example.myapplication.network.Vuelo(
                    origen = vueloData.origin,
                    destino = vueloData.destination,
                    fechaSalida = fechaSalidaBackend,
                    fechaLlegada = fechaLlegadaBackend,
                    precio = priceInEuros,
                    plazasDisponibles = 50
                )

                val vueloGuardado = RetrofitClient.myBackendService.addVuelo(nuevoVuelo)
                val idVueloGuardado = vueloGuardado.idVuelo

                if (idVueloGuardado != null) {
                    val nuevaReserva = com.example.myapplication.network.Reserva(
                        idUsuario = userId,
                        tipo = "vuelo",
                        idVuelo = idVueloGuardado,
                        idHotel = null,
                        fechaReserva = java.time.LocalDate.now().toString(),
                        estado = "activa"
                    )

                    val reservaGuardada = RetrofitClient.myBackendService.addReserva(nuevaReserva)

                    if (reservaGuardada.idReserva != null) {
                        Toast.makeText(this@HotelesActivity, "¡Vuelo reservado correctamente!", Toast.LENGTH_LONG).show()
                        val intent = Intent(this@HotelesActivity, MainActivity::class.java)
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
                        startActivity(intent)
                        finishAffinity()
                    } else {
                        Toast.makeText(this@HotelesActivity, "Error al guardar la reserva del vuelo.", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(this@HotelesActivity, "Error al guardar el vuelo.", Toast.LENGTH_SHORT).show()
                }

            } catch (e: Exception) {
                Toast.makeText(this@HotelesActivity, "Error al confirmar la reserva: ${e.message}", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun setupRecyclerView() {
        hotelesAdapter = HotelesAdapter(emptyList()) { hotelSeleccionado ->
            val ciudadBuscada = editDestinoHotel.text.toString().trim()
            val intent = Intent(this, ReservaHotelActivity::class.java).apply {
                putExtra("HOTEL_DATA", hotelSeleccionado)
                putExtra("HOTEL_CIUDAD", ciudadBuscada)
                vueloSeleccionado?.let { putExtra("EXTRA_VUELO_DATA", it) }
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

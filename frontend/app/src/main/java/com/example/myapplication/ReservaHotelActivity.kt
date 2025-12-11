package com.example.myapplication

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.myapplication.network.Hotel
import com.example.myapplication.network.HotelPropertyWrapper
import com.example.myapplication.network.Reserva
import com.example.myapplication.network.RetrofitClient
import com.squareup.picasso.Picasso
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.util.Locale

class ReservaHotelActivity : AppCompatActivity() {

    private var hotelWrapper: HotelPropertyWrapper? = null
    private var ciudad: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reserva_hotel)

        hotelWrapper = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getSerializableExtra("HOTEL_DATA", HotelPropertyWrapper::class.java)
        } else {
            @Suppress("DEPRECATION")
            intent.getSerializableExtra("HOTEL_DATA") as? HotelPropertyWrapper
        }
        ciudad = intent.getStringExtra("HOTEL_CIUDAD")

        if (hotelWrapper == null) {
            Toast.makeText(this, "Error: No se pudieron cargar los datos del hotel", Toast.LENGTH_LONG).show()
            finish()
            return
        }

        setupUI(hotelWrapper!!)

        val btnConfirmarReserva = findViewById<Button>(R.id.btnConfirmarReservaHotel)
        val btnVolver = findViewById<Button>(R.id.btnVolverHotel)

        btnConfirmarReserva.setOnClickListener {
            if (SessionManager.isLoggedIn(this)) {
                confirmarReserva(hotelWrapper!!)
            } else {
                Toast.makeText(this, "Debes iniciar sesión para poder reservar", Toast.LENGTH_LONG).show()
                startActivity(Intent(this, Login::class.java))
            }
        }

        btnVolver.setOnClickListener {
            finish()
        }
    }

    private fun setupUI(wrapper: HotelPropertyWrapper) {
        val hotel = wrapper.property

        val imgHotel: ImageView = findViewById(R.id.imgHotelReserva)
        val txtNombre: TextView = findViewById(R.id.txtHotelNombreReserva)
        val txtPuntuacion: TextView = findViewById(R.id.txtHotelPuntuacionReserva)
        val txtPrecio: TextView = findViewById(R.id.txtHotelPrecioReserva)

        txtNombre.text = hotel.name
        txtPuntuacion.text = "Puntuación: ${hotel.reviewScore?.toString() ?: "N/A"}"
        txtPrecio.text = String.format(Locale.getDefault(), "%.2f %s", hotel.priceBreakdown.grossPrice.value, hotel.priceBreakdown.grossPrice.currency)

        if (hotel.photoUrls != null && hotel.photoUrls.isNotEmpty()) {
            Picasso.get().load(hotel.photoUrls[0]).into(imgHotel)
        } else {
            imgHotel.setImageResource(R.drawable.ic_launcher_background)
        }
    }

    private fun confirmarReserva(wrapper: HotelPropertyWrapper) {
        val hotelDetails = wrapper.property
        val userId = SessionManager.getUserId(this)

        lifecycleScope.launch {
            try {
                val estrellas = if (hotelDetails.qualityClass == null || hotelDetails.qualityClass == 0) {
                    3
                } else {
                    hotelDetails.qualityClass
                }

                val nuevoHotel = Hotel(
                    nombre = hotelDetails.name,
                    ciudad = ciudad ?: "Desconocida",
                    precioPorNoche = hotelDetails.priceBreakdown.grossPrice.value,
                    estrellas = estrellas,
                    habitacionesDisponibles = 1
                )
                val hotelGuardado = RetrofitClient.myBackendService.addHotel(nuevoHotel)
                val idHotelGuardado = hotelGuardado.idHotel

                if (idHotelGuardado != null) {
                    val nuevaReserva = Reserva(
                        idUsuario = userId,
                        tipo = "hotel",
                        idVuelo = null,
                        idHotel = idHotelGuardado,
                        fechaReserva = LocalDate.now().toString(),
                        estado = "activa"
                    )
                    RetrofitClient.myBackendService.addReserva(nuevaReserva)

                    Toast.makeText(this@ReservaHotelActivity, "¡Reserva de hotel confirmada!", Toast.LENGTH_LONG).show()
                    

                    val intent = Intent(this@ReservaHotelActivity, MainActivity::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
                    startActivity(intent)
                    finishAffinity()
                } else {
                    Toast.makeText(this@ReservaHotelActivity, "Error al guardar el hotel en el backend.", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Toast.makeText(this@ReservaHotelActivity, "Error al confirmar la reserva: ${e.message}", Toast.LENGTH_LONG).show()
            }
        }
    }
}

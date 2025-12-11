package com.example.myapplication

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.myapplication.network.FlightData
import com.example.myapplication.network.Reserva
import com.example.myapplication.network.RetrofitClient
import com.example.myapplication.network.Vuelo
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale

class ReservaVueloActivity : AppCompatActivity() {

    private var flightData: FlightData? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reserva_vuelo)

        flightData = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getSerializableExtra("EXTRA_VUELO_DATA", FlightData::class.java)
        } else {
            @Suppress("DEPRECATION")
            intent.getSerializableExtra("EXTRA_VUELO_DATA") as? FlightData
        }

        if (flightData == null) {
            Toast.makeText(this, "Error: No se pudieron cargar los datos del vuelo", Toast.LENGTH_LONG).show()
            finish()
            return
        }

        setupUI(flightData!!)

        val btnConfirmarReserva = findViewById<Button>(R.id.btnConfirmarReserva)
        val btnVolver = findViewById<Button>(R.id.btnVolver)

        btnConfirmarReserva.setOnClickListener {
            if (SessionManager.isLoggedIn(this)) {
                confirmarReserva(flightData!!)
            } else {
                Toast.makeText(this, "Debes iniciar sesión para poder reservar", Toast.LENGTH_LONG).show()
                startActivity(Intent(this, Login::class.java))
            }
        }

        btnVolver.setOnClickListener {
            finish()
        }
    }

    private fun setupUI(vuelo: FlightData) {
        val txtVueloOrigenDestino = findViewById<TextView>(R.id.txtVueloOrigenDestino)
        val txtVueloFecha = findViewById<TextView>(R.id.txtVueloFecha)
        val txtVueloAerolinea = findViewById<TextView>(R.id.txtVueloAerolinea)
        val txtVueloPrecio = findViewById<TextView>(R.id.txtVueloPrecio)

        txtVueloOrigenDestino.text = "${vuelo.origin} → ${vuelo.destination}"
        txtVueloAerolinea.text = "Aerolínea: ${vuelo.airline}"

        val priceInEuros = vuelo.price / 100.0
        txtVueloPrecio.text = String.format(Locale.getDefault(), "Precio: %.2f €", priceInEuros)

        try {
            val fechaSalida = ZonedDateTime.parse(vuelo.departureAt)
            val formatter = DateTimeFormatter.ofPattern("dd MMM yyyy, HH:mm")
            txtVueloFecha.text = "Salida: ${fechaSalida.format(formatter)}"
        } catch (e: Exception) {
            txtVueloFecha.text = "Salida: ${vuelo.departureAt}"
        }
    }

    private fun confirmarReserva(vueloData: FlightData) {
        val userId = SessionManager.getUserId(this)

        lifecycleScope.launch {
            try {

                val departureZonedDateTime = ZonedDateTime.parse(vueloData.departureAt)
                val arrivalString = if (vueloData.returnAt.isNullOrEmpty()) vueloData.departureAt else vueloData.returnAt
                val arrivalZonedDateTime = ZonedDateTime.parse(arrivalString)

                val departureLocalDateTime = departureZonedDateTime.toLocalDateTime()
                val arrivalLocalDateTime = arrivalZonedDateTime.toLocalDateTime()

                val fechaSalidaBackend = departureLocalDateTime.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)
                val fechaLlegadaBackend = arrivalLocalDateTime.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)

                val priceInEuros = vueloData.price / 100.0

                val nuevoVuelo = Vuelo(
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
                    val nuevaReserva = Reserva(
                        idUsuario = userId,
                        tipo = "vuelo",
                        idVuelo = idVueloGuardado,
                        idHotel = null,
                        fechaReserva = LocalDate.now().toString(),
                        estado = "activa"
                    )

                    RetrofitClient.myBackendService.addReserva(nuevaReserva)

                    Toast.makeText(this@ReservaVueloActivity, "¡Reserva confirmada con éxito!", Toast.LENGTH_LONG).show()
                    val intent = Intent(this@ReservaVueloActivity, MainActivity::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)
                    startActivity(intent)
                    finish()
                } else {
                    Toast.makeText(this@ReservaVueloActivity, "Error al guardar el vuelo.", Toast.LENGTH_SHORT).show()
                }

            } catch (e: Exception) {
                Toast.makeText(this@ReservaVueloActivity, "Error al confirmar la reserva: ${e.message}", Toast.LENGTH_LONG).show()
            }
        }
    }
}

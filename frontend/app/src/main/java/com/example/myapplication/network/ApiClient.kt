package com.example.myapplication.network

import com.example.myapplication.BuildConfig
import com.google.gson.annotations.SerializedName
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Query
import java.io.Serializable

// --- MODELOS DE DATOS ---

data class Usuario(
    @SerializedName("id_usuario")
    val idUsuario: Long? = null,
    val nombre: String,
    val email: String,
    @SerializedName("contraseña")
    val contrasena: String,
    val rol: String = "cliente"
)

data class TravelpayoutsResponse(
    val success: Boolean,
    val data: Map<String, FlightData>,
    val currency: String
)

data class FlightData(
    val origin: String,
    val destination: String,
    val price: Int,
    val airline: String,
    @SerializedName("flight_number") val flightNumber: Int,
    @SerializedName("departure_at") val departureAt: String,
    @SerializedName("return_at") val returnAt: String,
    @SerializedName("expires_at") val expiresAt: String
) : Serializable // <-- Implementar Serializable

// NUEVOS MODELOS PARA TU BACKEND
// CORRECCIÓN FINAL: Alineados los @SerializedName con los nombres de campo del backend (camelCase)
data class Vuelo(
    @SerializedName("idVuelo")
    val idVuelo: Long? = null,
    val origen: String,
    val destino: String,
    @SerializedName("fechaSalida")
    val fechaSalida: String,
    @SerializedName("fechaLlegada")
    val fechaLlegada: String,
    val precio: Double,
    @SerializedName("plazasDisponibles")
    val plazasDisponibles: Int
) : Serializable

data class Reserva(
    @SerializedName("id_reserva")
    val idReserva: Long? = null, // Nulable para creación
    @SerializedName("id_usuario")
    val idUsuario: Long,
    val tipo: String,
    @SerializedName("id_vuelo")
    val idVuelo: Long?,
    @SerializedName("id_hotel")
    val idHotel: Long?,
    @SerializedName("fecha_reserva")
    val fechaReserva: String,
    val estado: String
) : Serializable


// --- INTERFACES DE SERVICIO ---

interface MyBackendService {
    @POST("usuarios")
    suspend fun registrarUsuario(@Body usuario: Usuario): Usuario

    @GET("usuarios")
    suspend fun getUsuarios(): List<Usuario>

    @POST("api/vuelos")
    suspend fun addVuelo(@Body vuelo: Vuelo): Vuelo

    @POST("reservas")
    suspend fun addReserva(@Body reserva: Reserva): Reserva
}

interface TravelpayoutsService {
    @GET("v1/prices/calendar")
    suspend fun getCalendarFlights(
        @Header("X-Access-Token") token: String,
        @Query("origin") origin: String,
        @Query("destination") destination: String,
        @Query("depart_date") departDate: String?
    ): TravelpayoutsResponse
}


// --- CLIENTE RETROFIT (SINGLETON) ---
object RetrofitClient {

    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    private val okHttpClient = OkHttpClient.Builder()
        .addInterceptor(loggingInterceptor)
        .build()

    private val myBackendRetrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BuildConfig.BACKEND_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    private val publicApiRetrofit by lazy {
        Retrofit.Builder()
            .baseUrl("https://api.travelpayouts.com/")
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    val myBackendService: MyBackendService by lazy {
        myBackendRetrofit.create(MyBackendService::class.java)
    }

    val publicFlightService: TravelpayoutsService by lazy {
        publicApiRetrofit.create(TravelpayoutsService::class.java)
    }
}

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
import retrofit2.http.Path
import retrofit2.http.Query
import java.io.Serializable

// --- MODELOS DE DATOS --- (SIN CAMBIOS)

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
) : Serializable

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
    val idReserva: Long? = null,
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

// --- MODELOS PARA HOTELES ---

data class Hotel(
    @SerializedName("id_hotel")
    val idHotel: Long? = null,
    val nombre: String,
    @SerializedName("ubicacion")
    val ciudad: String,
    @SerializedName("precio_noche")
    val precioPorNoche: Double,
    val estrellas: Int,
    @SerializedName("habitaciones_disponibles")
    val habitacionesDisponibles: Int
) : Serializable

// --- MODELOS PARA LA API DE BOOKING (CORREGIDOS) ---
data class BookingSearchResponse(
    val data: List<BookingDestination>
)

data class BookingDestination(
    @SerializedName("dest_id") val destId: String,
    val label: String,
    @SerializedName("search_type") val searchType: String?
)

data class BookingHotelSearchResponse(
    val data: BookingHotelData
)

data class BookingHotelData(
    val hotels: List<HotelPropertyWrapper> // <-- Contiene la lista de hoteles
)

// El objeto principal de cada hotel en la lista
data class HotelPropertyWrapper(
    @SerializedName("property")
    val property: HotelDetails
) : Serializable

// Los detalles que nos interesan del hotel
data class HotelDetails(
    @SerializedName("name") val name: String,
    @SerializedName("reviewScore") val reviewScore: Double?,
    @SerializedName("photoUrls") val photoUrls: List<String>?,
    @SerializedName("priceBreakdown") val priceBreakdown: PriceBreakdown,
    @SerializedName("qualityClass") val qualityClass: Int?
) : Serializable

// El desglose del precio
data class PriceBreakdown(
    @SerializedName("grossPrice") val grossPrice: GrossPrice
) : Serializable

// El precio final
data class GrossPrice(
    @SerializedName("currency") val currency: String,
    @SerializedName("value") val value: Double
) : Serializable


interface MyBackendService {
    @POST("usuarios")
    suspend fun registrarUsuario(@Body usuario: Usuario): Usuario

    @GET("usuarios")
    suspend fun getUsuarios(): List<Usuario>

    @POST("api/vuelos")
    suspend fun addVuelo(@Body vuelo: Vuelo): Vuelo

    @POST("hoteles")
    suspend fun addHotel(@Body hotel: Hotel): Hotel

    @POST("reservas")
    suspend fun addReserva(@Body reserva: Reserva): Reserva

    @GET("reservas/usuario/{idUsuario}/vuelos")
    suspend fun getReservasVuelosUsuario(@Path("idUsuario") idUsuario: Long): List<Reserva>

    @GET("reservas/usuario/{idUsuario}/hoteles")
    suspend fun getReservasHotelesUsuario(@Path("idUsuario") idUsuario: Long): List<Reserva>
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

interface BookingService {
    @GET("api/v1/hotels/searchDestination")
    suspend fun searchDestination(
        @Header("X-RapidAPI-Key") apiKey: String = "442a55ce04msha6515ba2887d41ap1bf203jsn11dc4c1f0b68",
        @Header("X-RapidAPI-Host") apiHost: String = "booking-com15.p.rapidapi.com",
        @Query("query") cityName: String
    ): BookingSearchResponse

    @GET("api/v1/hotels/searchHotels")
    suspend fun searchHotels(
        @Header("X-RapidAPI-Key") apiKey: String = "442a55ce04msha6515ba2887d41ap1bf203jsn11dc4c1f0b68",
        @Header("X-RapidAPI-Host") apiHost: String = "booking-com15.p.rapidapi.com",
        @Query("dest_id") destId: String,
        @Query("search_type") searchType: String,
        @Query("arrival_date") arrivalDate: String,
        @Query("departure_date") departureDate: String,
        @Query("locale") locale: String = "es",
        @Query("currency") currency: String = "EUR"
    ): BookingHotelSearchResponse
}

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

    private val bookingRetrofit by lazy {
        Retrofit.Builder()
            .baseUrl("https://booking-com15.p.rapidapi.com/")
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

    val bookingService: BookingService by lazy {
        bookingRetrofit.create(BookingService::class.java)
    }
}

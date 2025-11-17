package com.example.myapplication.network

import com.example.myapplication.BuildConfig
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor // <-- Importar
import com.google.gson.annotations.SerializedName
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Query


// --- MODELOS DE DATOS PARA TU BACKEND (SIN CAMBIOS) ---
data class Usuario(
    val nombre: String,
    val email: String,
    @SerializedName("contraseña")
    val contrasena: String,
    val rol: String = "cliente"
)

data class TravelpayoutsResponse(
    val success: Boolean,
    // La clave es la fecha (String) y el valor es el objeto del vuelo (FlightData)
    val data: Map<String, FlightData>, // <-- Línea correcta
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
)

// --- INTERFACES DE SERVICIO SEPARADAS ---

interface MyBackendService {
    @POST("usuarios")
    suspend fun registrarUsuario(@Body usuario: Usuario): Usuario

    @GET("usuarios")
    suspend fun getUsuarios(): List<Usuario>
}
interface TravelpayoutsService {
    @GET("v1/prices/calendar")
    suspend fun getCalendarFlights(
        @Header("X-Access-Token") token: String, // <-- Línea correcta
        @Query("origin") origin: String,
        @Query("destination") destination: String,
        @Query("depart_date") departDate: String?
    ): TravelpayoutsResponse
}


// Fichero: ApiClient.kt

// --- CLIENTE RETROFIT (SINGLETON) ---
object RetrofitClient {

    // --- Inicio de la modificación ---

    // 1. Crear el interceptor de logging
    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY // BODY muestra toda la info: URL, headers, petición y respuesta
    }

    // 2. Crear un cliente OkHttp y añadirle el interceptor
    private val okHttpClient = OkHttpClient.Builder()
        .addInterceptor(loggingInterceptor)
        .build()

    // --- Fin de la modificación ---


    // --- INICIO DE LA CORRECCIÓN: Volvemos a añadir myBackendRetrofit ---
    // Cliente para TU backend
    private val myBackendRetrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BuildConfig.BACKEND_URL)
            .addConverterFactory(GsonConverterFactory.create())
            // Opcional: podrías añadir el client con logging aquí también si quieres depurar tu backend
            // .client(okHttpClient)
            .build()
    }
    // --- FIN DE LA CORRECCIÓN ---

    // Cliente para la API de Travelpayouts (¡AHORA USA EL CLIENTE CON LOGGING!)
    private val publicApiRetrofit by lazy {
        Retrofit.Builder()
            .baseUrl("https://api.travelpayouts.com/")
            .client(okHttpClient) // <-- AÑADIR ESTA LÍNEA
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    val myBackendService: MyBackendService by lazy {
        // Ahora esta línea es correcta de nuevo
        myBackendRetrofit.create(MyBackendService::class.java)
    }

    val publicFlightService: TravelpayoutsService by lazy {
        publicApiRetrofit.create(TravelpayoutsService::class.java)
    }
}

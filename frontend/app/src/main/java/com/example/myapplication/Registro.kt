package com.example.myapplication

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.myapplication.BuildConfig // Importación crucial
import com.google.gson.annotations.SerializedName
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

// --- MODELOS DE DATOS ---
data class Usuario(
    val nombre: String,
    val email: String,
    @SerializedName("contraseña")
    val contrasena: String,
    val rol: String = "cliente"
)

data class Vuelo(
    val idVuelo: Long,
    val origen: String,
    val destino: String,
    val fechaSalida: String, // Se tratará como String para simplicidad
    val fechaLlegada: String,
    val precio: Double,
    val plazasDisponibles: Int
)

// --- INTERFAZ DE LA API ---
interface ApiService {
    @POST("usuarios")
    suspend fun registrarUsuario(@Body usuario: Usuario): Usuario

    @GET("usuarios")
    suspend fun getUsuarios(): List<Usuario>

    @GET("api/vuelos") // Endpoint del backend para vuelos
    suspend fun getVuelos(): List<Vuelo>
}

// --- CLIENTE RETROFIT (SINGLETON) ---
object RetrofitClient {
    private val retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BuildConfig.BACKEND_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    val apiService: ApiService by lazy {
        retrofit.create(ApiService::class.java)
    }
}

// --- ACTIVIDAD DE REGISTRO ---
class Registro : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registro)

        val editTextNombre = findViewById<EditText>(R.id.editTextNombre)
        val editTextEmail = findViewById<EditText>(R.id.editTextEmail)
        val editTextContraseña = findViewById<EditText>(R.id.editTextContraseña)
        val buttonRegistro = findViewById<Button>(R.id.buttonRegistro)
        val txtIrALogin = findViewById<TextView>(R.id.txtIrALogin)

        buttonRegistro.setOnClickListener {
            val nombre = editTextNombre.text.toString()
            val email = editTextEmail.text.toString()
            val contrasena = editTextContraseña.text.toString()

            if (nombre.isNotEmpty() && email.isNotEmpty() && contrasena.isNotEmpty()) {
                val nuevoUsuario = Usuario(nombre, email, contrasena)
                CoroutineScope(Dispatchers.IO).launch {
                    try {
                        val usuarioCreado = RetrofitClient.apiService.registrarUsuario(nuevoUsuario)
                        runOnUiThread {
                            Toast.makeText(this@Registro, "Usuario registrado con éxito: ${usuarioCreado.nombre}", Toast.LENGTH_SHORT).show()
                        }
                    } catch (e: Exception) {
                        runOnUiThread {
                            Toast.makeText(this@Registro, "Error al registrar usuario: ${e.message}", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            } else {
                Toast.makeText(this, "Por favor, rellena todos los campos", Toast.LENGTH_SHORT).show()
            }
        }

        txtIrALogin.setOnClickListener {
            startActivity(Intent(this, Login::class.java))
        }
    }
}

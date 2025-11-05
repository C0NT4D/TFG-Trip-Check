package com.example.myapplication

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
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

data class Usuario(
    val nombre: String,
    val email: String,
    @SerializedName("contraseña") // Asegura el nombre correcto en el JSON
    val contrasena: String,
    val rol: String = "cliente" // Corregido: El rol por defecto es 'cliente'
)

interface ApiService {
    @POST("usuarios")
    suspend fun registrarUsuario(@Body usuario: Usuario): Usuario

    @GET("usuarios")
    suspend fun getUsuarios(): List<Usuario>
}

object RetrofitClient {
    private val retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BuildConfig.BACKEND_URL) // Usando la URL desde BuildConfig
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    val apiService: ApiService by lazy {
        retrofit.create(ApiService::class.java)
    }
}

class Registro : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registro)

        val editTextNombre = findViewById<EditText>(R.id.editTextNombre)
        val editTextEmail = findViewById<EditText>(R.id.editTextEmail)
        val editTextContraseña = findViewById<EditText>(R.id.editTextContraseña)
        val buttonRegistro = findViewById<Button>(R.id.buttonRegistro)

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
    }
}

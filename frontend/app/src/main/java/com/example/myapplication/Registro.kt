package com.example.myapplication

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.myapplication.network.RetrofitClient
import com.example.myapplication.network.Usuario
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

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
            val nombre = editTextNombre.text.toString().trim()
            val email = editTextEmail.text.toString().trim()
            val contrasena = editTextContraseña.text.toString().trim()

            if (nombre.isNotEmpty() && email.isNotEmpty() && contrasena.isNotEmpty()) {
                val nuevoUsuario = Usuario(nombre, email, contrasena)
                CoroutineScope(Dispatchers.IO).launch {
                    try {
                        val usuarioCreado = RetrofitClient.myBackendService.registrarUsuario(nuevoUsuario)
                        runOnUiThread {
                            // Corregido: La variable se llama usuarioCreado
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

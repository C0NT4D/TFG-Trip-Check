package com.example.myapplication

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONArray
import java.net.HttpURLConnection
import java.net.URL

class Login : AppCompatActivity() {

    private lateinit var editEmail: EditText
    private lateinit var editPassword: EditText
    private lateinit var btnLogin: Button
    private lateinit var txtRegistrar: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        editEmail = findViewById(R.id.editEmail)
        editPassword = findViewById(R.id.editPassword)
        btnLogin = findViewById(R.id.btnLogin)
        txtRegistrar = findViewById(R.id.txtRegistrar)

        // Acción del botón de inicio de sesión
        btnLogin.setOnClickListener {
            val email = editEmail.text.toString().trim()
            val password = editPassword.text.toString().trim()

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Por favor, completa todos los campos", Toast.LENGTH_SHORT).show()
            } else {
                verificarUsuario(email, password)
            }
        }

        // Texto para registrar un nuevo usuario
        txtRegistrar.setOnClickListener {
            Toast.makeText(this, "Pantalla de registro próximamente", Toast.LENGTH_SHORT).show()
            // Si ya tienes Registro.kt, puedes hacer:
            // startActivity(Intent(this, Registro::class.java))
        }
    }

    private fun verificarUsuario(email: String, password: String) {
        lifecycleScope.launch(Dispatchers.IO) {
            try {
                val url = URL("http://10.0.2.2:8080/usuarios")
                val connection = url.openConnection() as HttpURLConnection
                connection.requestMethod = "GET"
                connection.connectTimeout = 5000
                connection.readTimeout = 5000

                if (connection.responseCode == 200) {
                    val result = connection.inputStream.bufferedReader().use { it.readText() }
                    val jsonArray = JSONArray(result)
                    var usuarioEncontrado = false

                    for (i in 0 until jsonArray.length()) {
                        val user = jsonArray.getJSONObject(i)
                        val userEmail = user.getString("email")
                        val userPass = user.getString("contraseña")

                        if (userEmail == email && userPass == password) {
                            usuarioEncontrado = true
                            break
                        }
                    }

                    withContext(Dispatchers.Main) {
                        if (usuarioEncontrado) {
                            Toast.makeText(
                                this@Login,
                                "✅ Inicio de sesión correcto",
                                Toast.LENGTH_SHORT
                            ).show()

                            // Ejemplo: ir al menú principal
                            val intent = Intent(this@Login, MainActivity::class.java)
                            startActivity(intent)
                            finish()
                        } else {
                            Toast.makeText(
                                this@Login,
                                "❌ Email o contraseña incorrectos",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                } else {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(
                            this@Login,
                            "Error al conectar con el servidor",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }

                connection.disconnect()

            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(
                        this@Login,
                        "Error: ${e.message}",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }
    }
}

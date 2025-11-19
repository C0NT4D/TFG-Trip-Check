package com.example.myapplication

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.myapplication.network.RetrofitClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

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

        btnLogin.setOnClickListener {
            val email = editEmail.text.toString().trim()
            val password = editPassword.text.toString().trim()

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Por favor, completa todos los campos", Toast.LENGTH_SHORT).show()
            } else {
                verificarUsuario(email, password)
            }
        }

        txtRegistrar.setOnClickListener {
            startActivity(Intent(this, Registro::class.java))
        }
    }

    private fun verificarUsuario(email: String, password: String) {
        lifecycleScope.launch(Dispatchers.IO) {
            try {
                val usuarios = RetrofitClient.myBackendService.getUsuarios()
                val usuarioEncontrado = usuarios.find { it.email == email && it.contrasena == password }

                withContext(Dispatchers.Main) {
                    if (usuarioEncontrado != null && usuarioEncontrado.idUsuario != null) {
                        // Guardar el ID del usuario en la sesión
                        SessionManager.saveUserId(this@Login, usuarioEncontrado.idUsuario)

                        Toast.makeText(this@Login, "✅ Inicio de sesión correcto", Toast.LENGTH_SHORT).show()
                        val intent = Intent(this@Login, MainActivity::class.java)
                        startActivity(intent)
                        finish()
                    } else {
                        Toast.makeText(this@Login, "❌ Email o contraseña incorrectos", Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@Login, "Error: ${e.message}", Toast.LENGTH_LONG).show()
                }
            }
        }
    }
}

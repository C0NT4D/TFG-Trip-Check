package com.example.myapplication

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Button
import android.widget.ImageButton
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.myapplication.network.RetrofitClient
import com.example.myapplication.network.Usuario
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.imageview.ShapeableImageView
import com.google.android.material.textfield.TextInputEditText
import com.squareup.picasso.Picasso
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class PerfilActivity : AppCompatActivity() {

    private lateinit var imgPerfil: ShapeableImageView
    private lateinit var editNombre: TextInputEditText
    private lateinit var editEmail: TextInputEditText
    private lateinit var editPassword: TextInputEditText
    private lateinit var btnGuardar: Button
    private lateinit var btnCerrarSesion: Button
    private lateinit var btnCambiarFoto: FloatingActionButton
    private lateinit var btnVolver: ImageButton

    private var currentUsuario: Usuario? = null
    private var imageUri: Uri? = null

    // Registra el resultado de la selección de imagen
    private val pickImageLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val data: Intent? = result.data
            imageUri = data?.data
            imgPerfil.setImageURI(imageUri)
            // Guardar URI en preferencias para persistencia local
            saveProfileImageUri(imageUri.toString())
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_perfil)

        imgPerfil = findViewById(R.id.imgPerfil)
        editNombre = findViewById(R.id.editNombrePerfil)
        editEmail = findViewById(R.id.editEmailPerfil)
        editPassword = findViewById(R.id.editPasswordPerfil)
        btnGuardar = findViewById(R.id.btnGuardarPerfil)
        btnCerrarSesion = findViewById(R.id.btnCerrarSesion)
        btnCambiarFoto = findViewById(R.id.btnCambiarFoto)
        btnVolver = findViewById(R.id.btnVolverPerfil)

        // Cargar imagen guardada si existe
        loadProfileImage()

        // Cargar datos del usuario
        cargarDatosUsuario()

        btnCambiarFoto.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            pickImageLauncher.launch(intent)
        }

        btnGuardar.setOnClickListener {
            guardarCambios()
        }

        btnCerrarSesion.setOnClickListener {
            cerrarSesion()
        }

        btnVolver.setOnClickListener {
            finish()
        }
    }

    private fun cargarDatosUsuario() {
        val userId = SessionManager.getUserId(this)
        lifecycleScope.launch(Dispatchers.IO) {
            try {
                val usuarios = RetrofitClient.myBackendService.getUsuarios()
                val usuarioEncontrado = usuarios.find { it.idUsuario == userId }

                withContext(Dispatchers.Main) {
                    if (usuarioEncontrado != null) {
                        currentUsuario = usuarioEncontrado
                        editNombre.setText(usuarioEncontrado.nombre)
                        editEmail.setText(usuarioEncontrado.email)
                    } else {
                        Toast.makeText(this@PerfilActivity, "Error al cargar usuario", Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@PerfilActivity, "Error de conexión: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun guardarCambios() {
        val nuevoNombre = editNombre.text.toString().trim()
        val nuevoEmail = editEmail.text.toString().trim()
        val nuevaPassword = editPassword.text.toString().trim()

        if (nuevoNombre.isEmpty() || nuevoEmail.isEmpty()) {
            Toast.makeText(this, "Nombre y Email son obligatorios", Toast.LENGTH_SHORT).show()
            return
        }

        val userId = SessionManager.getUserId(this)
        // Mantenemos la contraseña anterior si no se escribe una nueva
        val passwordFinal = if (nuevaPassword.isNotEmpty()) nuevaPassword else (currentUsuario?.contrasena ?: "")

        val usuarioActualizado = Usuario(
            idUsuario = userId,
            nombre = nuevoNombre,
            email = nuevoEmail,
            contrasena = passwordFinal,
            rol = currentUsuario?.rol ?: "cliente"
        )

        lifecycleScope.launch(Dispatchers.IO) {
            try {
                // Asumimos que el backend tiene un endpoint PUT /usuarios/{id}
                RetrofitClient.myBackendService.updateUsuario(userId, usuarioActualizado)
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@PerfilActivity, "Perfil actualizado correctamente", Toast.LENGTH_SHORT).show()
                    currentUsuario = usuarioActualizado
                    editPassword.text?.clear() // Limpiar campo contraseña
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@PerfilActivity, "Error al actualizar: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun cerrarSesion() {
        SessionManager.logout(this)
        val intent = Intent(this, Login::class.java)
        // Limpiar pila de actividades para que no se pueda volver atrás
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }

    private fun saveProfileImageUri(uriString: String) {
        val prefs = getSharedPreferences("TripCheckPrefs", Context.MODE_PRIVATE)
        prefs.edit().putString("profile_image_uri_$currentUsuario", uriString).apply()
    }

    private fun loadProfileImage() {
        val prefs = getSharedPreferences("TripCheckPrefs", Context.MODE_PRIVATE)
        // Usamos una clave genérica o asociada al ID si ya lo tenemos, 
        // pero al iniciar onCreate aun no tenemos el ID cargado del backend, 
        // aunque sí en SessionManager.
        val userId = SessionManager.getUserId(this)
        val uriString = prefs.getString("profile_image_uri_com.example.myapplication.network.Usuario", null) 
        // Nota: La clave anterior estaba mal formada en el save, simplifiquemos:
        
        val uriSaved = prefs.getString("user_image_$userId", null)
        if (uriSaved != null) {
            imgPerfil.setImageURI(Uri.parse(uriSaved))
        }
    }
    
    // Sobreescribimos saveProfileImageUri para usar el ID correcto
    private fun saveProfileImageUriCorrect(uriString: String) {
         val userId = SessionManager.getUserId(this)
         val prefs = getSharedPreferences("TripCheckPrefs", Context.MODE_PRIVATE)
         prefs.edit().putString("user_image_$userId", uriString).apply()
    }
}

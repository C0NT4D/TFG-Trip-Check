package com.example.myapplication

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.bottomnavigation.BottomNavigationView
import me.relex.circleindicator.CircleIndicator3

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // --- Configuración del Botón "Crear Viaje" ---
        val btnCrearViaje = findViewById<Button>(R.id.btnCrearViaje)
        btnCrearViaje.setOnClickListener {
            val intent = Intent(this, BuscarVuelosActivity::class.java)
            startActivity(intent)
        }

        // --- Configuración del ViewPager2 (Carrusel) ---
        val viewPager = findViewById<ViewPager2>(R.id.viewPager)
        val indicator = findViewById<CircleIndicator3>(R.id.indicator)

        val imageList = listOf(
            R.drawable.imgcarrusel1,
            R.drawable.imgcarrusel2,
            R.drawable.imgcarrusel3
        )

        val adapter = ImageCarouselAdapter(imageList)
        viewPager.adapter = adapter
        indicator.setViewPager(viewPager)


        // --- Configuración de la BottomNavigationView ---
        val bottomNav = findViewById<BottomNavigationView>(R.id.bottom_navigation)
        bottomNav.selectedItemId = R.id.navigation_home

        bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.navigation_home -> true
                R.id.navigation_mis_viajes -> {
                    val intent = Intent(this, HistorialReservasActivity::class.java)
                    startActivity(intent)
                    true
                }
                R.id.navigation_perfil -> {
                    // val intent = Intent(this, PerfilActivity::class.java)
                    // startActivity(intent)
                    true
                }
                else -> false
            }
        }
    }

    override fun onResume() {
        super.onResume()
        val bottomNav = findViewById<BottomNavigationView>(R.id.bottom_navigation)
        if (bottomNav.selectedItemId != R.id.navigation_home) {
            bottomNav.selectedItemId = R.id.navigation_home
        }
    }
}

class ImageCarouselAdapter(private val imageList: List<Int>) :
    RecyclerView.Adapter<ImageCarouselAdapter.ImageViewHolder>() {

    inner class ImageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imageView: ImageView = itemView.findViewById(R.id.carousel_image_view)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_carousel_image, parent, false)
        return ImageViewHolder(view)
    }

    override fun onBindViewHolder(holder: ImageViewHolder, position: Int) {
        holder.imageView.setImageResource(imageList[position])
    }

    override fun getItemCount(): Int {
        return imageList.size
    }
}

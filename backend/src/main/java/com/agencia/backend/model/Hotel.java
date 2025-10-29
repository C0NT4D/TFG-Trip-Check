package com.agencia.backend.model;

import jakarta.persistence.*;

@Entity
@Table(name = "hoteles")
public class Hotel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id_hotel;

    private String nombre;
    private String ubicacion;
    private int estrellas;
    private double precio_noche;
    private int habitaciones_disponibles;

    // Getters y setters
    public Long getId_hotel() { return id_hotel; }
    public void setId_hotel(Long id_hotel) { this.id_hotel = id_hotel; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getUbicacion() { return ubicacion; }
    public void setUbicacion(String ubicacion) { this.ubicacion = ubicacion; }

    public int getEstrellas() { return estrellas; }
    public void setEstrellas(int estrellas) { this.estrellas = estrellas; }

    public double getPrecio_noche() { return precio_noche; }
    public void setPrecio_noche(double precio_noche) { this.precio_noche = precio_noche; }

    public int getHabitaciones_disponibles() { return habitaciones_disponibles; }
    public void setHabitaciones_disponibles(int habitaciones_disponibles) { this.habitaciones_disponibles = habitaciones_disponibles; }
}

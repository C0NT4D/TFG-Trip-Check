package com.agencia.backend.model;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "reservas")
public class Reserva {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id_reserva;

    private Long id_usuario;
    private String tipo;
    private Long id_vuelo;
    private Long id_hotel;
    private LocalDate fecha_reserva;
    private String estado;

    // Getters y setters
    public Long getId_reserva() { return id_reserva; }
    public void setId_reserva(Long id_reserva) { this.id_reserva = id_reserva; }

    public Long getId_usuario() { return id_usuario; }
    public void setId_usuario(Long id_usuario) { this.id_usuario = id_usuario; }

    public String getTipo() { return tipo; }
    public void setTipo(String tipo) { this.tipo = tipo; }

    public Long getId_vuelo() { return id_vuelo; }
    public void setId_vuelo(Long id_vuelo) { this.id_vuelo = id_vuelo; }

    public Long getId_hotel() { return id_hotel; }
    public void setId_hotel(Long id_hotel) { this.id_hotel = id_hotel; }

    public LocalDate getFecha_reserva() { return fecha_reserva; }
    public void setFecha_reserva(LocalDate fecha_reserva) { this.fecha_reserva = fecha_reserva; }

    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }
}

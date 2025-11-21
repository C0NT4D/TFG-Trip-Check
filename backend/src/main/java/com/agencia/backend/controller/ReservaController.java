package com.agencia.backend.controller;

import com.agencia.backend.model.Reserva;
import com.agencia.backend.repository.ReservaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/reservas")
@CrossOrigin(origins = "*")
public class ReservaController {

    @Autowired
    private ReservaRepository reservaRepository;

    @GetMapping
    public List<Reserva> getAll() {
        return reservaRepository.findAll();
    }

    @PostMapping
    public Reserva create(@RequestBody Reserva reserva) {
        return reservaRepository.save(reserva);
    }


    @GetMapping("/usuario/{idUsuario}")
    public List<Reserva> getReservasByUsuario(@PathVariable Long idUsuario) {
        return reservaRepository.findById_usuario(idUsuario);
    }
    
    @GetMapping("/usuario/{idUsuario}/vuelos")
    public List<Reserva> getVuelosByUsuario(@PathVariable Long idUsuario) {
        return reservaRepository.findById_usuarioAndTipo(idUsuario, "vuelo");
    }

    @GetMapping("/usuario/{idUsuario}/hoteles")
    public List<Reserva> getHotelesByUsuario(@PathVariable Long idUsuario) {
        return reservaRepository.findById_usuarioAndTipo(idUsuario, "hotel");
    }
}
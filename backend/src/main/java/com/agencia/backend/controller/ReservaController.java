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
}

package com.agencia.backend.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import com.agencia.backend.model.Vuelo;
import com.agencia.backend.repository.VueloRepository;

@RestController
@RequestMapping("/api/vuelos")
@CrossOrigin(origins = "*") 
public class VueloController {

    @Autowired
    private VueloRepository vueloRepository;

    @GetMapping
    public List<Vuelo> getAllVuelos() {
        return vueloRepository.findAll();
    }

    @PostMapping
    public Vuelo createVuelo(@RequestBody Vuelo vuelo) {
        return vueloRepository.save(vuelo);
    }
}

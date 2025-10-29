package com.agencia.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.agencia.backend.model.Vuelo;

public interface VueloRepository extends JpaRepository<Vuelo, Long> { }

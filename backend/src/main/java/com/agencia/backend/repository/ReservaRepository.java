package com.agencia.backend.repository;

import com.agencia.backend.model.Reserva;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;

public interface ReservaRepository extends JpaRepository<Reserva, Long> {
    @Query("SELECT r FROM Reserva r WHERE r.id_usuario = :idUsuario")
    List<Reserva> findById_usuario(@Param("idUsuario") Long idUsuario);
    
    @Query("SELECT r FROM Reserva r WHERE r.id_usuario = :idUsuario AND r.tipo = :tipo")
    List<Reserva> findById_usuarioAndTipo(@Param("idUsuario") Long idUsuario, @Param("tipo") String tipo);
}
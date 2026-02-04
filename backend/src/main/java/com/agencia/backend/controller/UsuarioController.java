package com.agencia.backend.controller;

import com.agencia.backend.model.Usuario;
import com.agencia.backend.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.http.ResponseEntity;
import java.util.Optional;
import java.util.Map;

@RestController
@RequestMapping("/usuarios")
@CrossOrigin(origins = "*")
public class UsuarioController {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @GetMapping
    public List<Usuario> getAll() {
        return usuarioRepository.findAll();
    }

    @PostMapping
    public Usuario create(@RequestBody Usuario usuario) {
        if (usuario.getContraseña() != null && !usuario.getContraseña().isEmpty()) {
            usuario.setContraseña(passwordEncoder.encode(usuario.getContraseña()));
        }
        return usuarioRepository.save(usuario);
    }

    @PutMapping("/{id}")
    public Usuario update(@PathVariable Long id, @RequestBody Usuario usuario) {
        usuario.setId_usuario(id);

        // Check if password is being updated
        if (usuario.getContraseña() != null && !usuario.getContraseña().isEmpty()) {
            usuario.setContraseña(passwordEncoder.encode(usuario.getContraseña()));
        } else {
            // Keep existing password if not provided (optional logic, but good practice)
            // Ideally we should fetch the existing user to preserve the old password if
            // it's null here
            // For now, following the pattern of the original code but with hashing if
            // present
            Usuario existingUser = usuarioRepository.findById(id).orElse(null);
            if (existingUser != null) {
                usuario.setContraseña(existingUser.getContraseña());
            }
        }
        return usuarioRepository.save(usuario);
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> credenciales) {
        String email = credenciales.get("email");
        String password = credenciales.get("password");

        if (email == null || password == null) {
            return ResponseEntity.badRequest().body("Email y contraseña son obligatorios");
        }

        Optional<Usuario> usuarioOpt = usuarioRepository.findByEmail(email);

        if (usuarioOpt.isPresent()) {
            Usuario usuario = usuarioOpt.get();
            if (passwordEncoder.matches(password, usuario.getContraseña())) {
                // Return user without password or with hashed password (safest is to nullify it
                // in response, but for now returning object)
                return ResponseEntity.ok(usuario);
            }
        }

        return ResponseEntity.status(401).body("Credenciales incorrectas");
    }
}

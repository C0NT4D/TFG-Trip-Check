package com.agencia.backend.controller;

import com.agencia.backend.service.OllamaService;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/ai")
public class AiController {

    private final OllamaService ollamaService;

    public AiController(OllamaService ollamaService) {
        this.ollamaService = ollamaService;
    }

    /**
     * Endpoint POST para recibir preguntas del usuario
     * Body JSON: { "message": "tu pregunta aquí" }
     * Devuelve: { "answer": "respuesta del bot" }
     */
    @PostMapping("/ask")
    public Map<String, String> ask(@RequestBody Map<String, String> body) {

        String userMessage = body.get("message");

        // Prompt seguro + contexto
        String prompt = """
                Eres un asistente de ayuda para el usuario de la aplicación Trip-Check.

                INFORMACIÓN OFICIAL:
                - La aplicación permite a los usuarios buscar y reservar viajes (vuelo y/o hotel).
                - Es necesario registrarse e iniciar sesión para hacer reservas.
                - Los usuarios pueden consultar sus reservas y gestionar su perfil.
                - La aplicación NO gestiona pagos.

                **Creadores de la Aplicación:**
                - La aplicación ha sido desarrollada por Iván Contador y Juanjo Barba, estudiantes del grado superior en Desarrollo de Aplicaciones Multiplataforma.

                **Flujo Inicial de la Aplicación:**
                1.  **Inicio:** Al abrir la app, se presenta la pantalla de "Login".
                2.  **Registro:** Si no tienes cuenta, puedes pulsar en "Regístrate aquí" para crear una con tu nombre, email y contraseña. Una vez creada, debes volver a la pantalla de Login para entrar.
                3.  **Acceso:** Introduce tu email y contraseña y pulsa "Iniciar Sesión".
                4.  **Pantalla Principal:** Una vez dentro, verás la pantalla de inicio con un carrusel de imágenes y el botón "Buscar Viaje". También tendrás un menú de navegación en la parte inferior.

                **Opciones del Menú de Navegación Inferior:**
                -   **Home (Inicio):** Te lleva a la pantalla principal para buscar un nuevo viaje.
                -   **Mis Viajes:** Muestra un historial con todas las reservas de vuelos y hoteles que has realizado.
                -   **Perfil:** Accede a tu perfil de usuario.

                **Flujo de reserva de un Viaje (Vuelo + Hotel):**
                1.  **Inicio:** En la pantalla principal, pulsa "Buscar Viaje".
                2.  **Búsqueda de Vuelo:** Introduce origen (siglas de aeropuerto, ej: MAD), destino y fecha. Pulsa "Buscar".
                3.  **Selección de Vuelo:** Te aparecerá una lista de vuelos. Selecciona el que prefieras.
                4.  **Búsqueda de Hotel:** Automáticamente, la app te llevará a la búsqueda de hoteles en la ciudad de destino, con la fecha de llegada ya puesta. Introduce la fecha de salida y pulsa "Buscar Hoteles".
                5.  **Selección de Hotel:** Se mostrará una lista de hoteles. Elige el que más te guste.
                6.  **Confirmación Final:** Verás un resumen del hotel. Pulsa "Confirmar Reserva" para finalizar.
                7.  **¡Viaje Reservado!:** El vuelo y el hotel quedarán registrados en la sección "Mis Viajes".

                **Flujo de reserva de un Hotel (Solo Hotel):**
                1.  **Búsqueda:** Inicia un flujo de viaje desde la pantalla principal. Después de seleccionar un vuelo, llegarás a la búsqueda de hoteles. O, si la app lo permite, navega directamente a la sección de hoteles.
                2.  **Selección y Confirmación:** Elige un hotel de la lista y pulsa "Confirmar Reserva" en la pantalla de detalle.
                3.  **¡Hotel Reservado!:** El hotel quedará registrado en "Mis Viajes".

                **Sección de Perfil:**
                -   **Cambiar foto de perfil:** Puedes seleccionar una nueva imagen de la galería.
                -   **Editar información personal:** Puedes modificar tu nombre, email y contraseña. Pulsa "Guardar" para aplicar los cambios.
                -   **Cerrar Sesión:** Tienes un botón para salir de tu cuenta de forma segura.

                REGLAS:
                - Responde únicamente basándote en la INFORMACIÓN OFICIAL proporcionada.
                - No inventes funcionalidades que no están en la lista.
                - Si no tienes la respuesta a una pregunta, indica que no dispones de esa información.

                PREGUNTA:
                %s
                """
                .formatted(userMessage);

        String answer = ollamaService.ask(prompt);

        return Map.of("answer", answer);
    }
}
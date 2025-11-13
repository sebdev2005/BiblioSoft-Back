package co.edu.univalle.Controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import co.edu.univalle.Models.UserModel;
import co.edu.univalle.Services.UserService;
import lombok.RequiredArgsConstructor;

import java.util.Map;

@CrossOrigin(origins = "http://localhost:5173")
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody UserModel user) {
        try {
            UserModel nuevo = userService.register(user);
            return ResponseEntity.ok(Map.of(
                "message", "Registro exitoso",
                "user", nuevo
            ));
        } catch (RuntimeException e) {
            // Errores esperados del UserService
            return ResponseEntity.badRequest().body(Map.of(
                "message", e.getMessage(),
                "errorType", e.getClass().getSimpleName()
            ));
        } catch (Exception e) {
            // Otros errores inesperados
            return ResponseEntity.internalServerError().body(Map.of(
                "message", "Error inesperado en el servidor: " + e.getMessage(),
                "errorType", e.getClass().getSimpleName()
            ));
        }
    }

    @PostMapping("/example")
    public String welcome() {
        return "Welcome";
    }
}

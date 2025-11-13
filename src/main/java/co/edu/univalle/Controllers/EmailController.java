package co.edu.univalle.Controllers;

import co.edu.univalle.Models.UserModel;
import co.edu.univalle.Repositories.UserRepository;
import co.edu.univalle.Services.EmailService;
import co.edu.univalle.Services.JWTService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping(path = "/auth")
@CrossOrigin(origins = "http://localhost:5173")
public class EmailController {

    @Autowired
    private EmailService emailService;
    @Autowired
    private JWTService jwtService;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;

    @PostMapping(path = "/email/send")
    public ResponseEntity<?> sendEmail(@RequestBody Map<String, String> body) {
        String email = body.get("email");

        String token = jwtService.generatePasswordResetToken(email);
        String resetLink = "http://localhost:5173/reset-password?token=" + token;
        emailService.sendMail(email, resetLink);

        return ResponseEntity.ok("Correo enviado exitosamente");
    }
    @GetMapping("/validate-token")
    public ResponseEntity<?> validateToken(@RequestParam String token) {
        try {
            String email = jwtService.getUsernameFromToken(token);
            System.out.println("Email extraído del token: " + email); // DEBUG
            return ResponseEntity.ok("Token válido para: " + email);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body("Token inválido o expirado");
        }
    }
    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@RequestBody Map<String, String> body) {
        String token = body.get("token");
        String newPassword = body.get("password");

        try {
            String email = jwtService.getUsernameFromToken(token);
            Optional<UserModel> usuarioOpt = userRepository.findByEmail(email);
            if (usuarioOpt.isEmpty()) {
                return ResponseEntity.badRequest().body("Usuario no encontrado");
            }

            UserModel usuario = usuarioOpt.get();
            usuario.setPassword(passwordEncoder.encode(newPassword));
            userRepository.save(usuario);

            return ResponseEntity.ok("Contraseña actualizada exitosamente");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body("Token inválido o expirado");
        }
    }
}

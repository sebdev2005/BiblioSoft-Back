package co.edu.univalle.Controllers;

import co.edu.univalle.Auth.AuthResponse;
import co.edu.univalle.Auth.RegisterRequest;
import co.edu.univalle.Models.UserModel;
import co.edu.univalle.Services.AuthService;
import co.edu.univalle.Auth.LoginRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import co.edu.univalle.Repositories.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import java.util.Map;


@RestController
@RequestMapping(path = "/auth")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:5173")

public class AuthController {

    private final AuthService authService;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @PostMapping(path = "/login")
    public ResponseEntity<AuthResponse> login(@RequestBody LoginRequest request){
        return ResponseEntity.ok(authService.login(request));
    }
    @PostMapping(path = "/register")
    public ResponseEntity<AuthResponse> register(@RequestBody RegisterRequest request){
        return ResponseEntity.ok(authService.register(request));
    }

    @PutMapping("/change-password")
    public ResponseEntity<?> changePassword(@RequestBody Map<String, String> request) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        String oldPassword = request.get("oldPassword");
        String newPassword = request.get("newPassword");


        System.out.println("Buscando usuario: " + username);
        UserModel user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
            return ResponseEntity.status(400).body("Contraseña anterior incorrecta");
        }

        if (oldPassword.equals(newPassword)) {
            return ResponseEntity.status(400).body("La nueva contraseña no puede ser igual a la anterior");
        }

        String passwordRegex = "^(?=.*[A-Z])(?=.*[a-z])(?=.*[0-9])(?=.*[!@#$%^&*.,;:?¡¿_+\\-=]).{8,30}$";
        if (!newPassword.matches(passwordRegex)) {
            return ResponseEntity.status(400).body("La nueva contraseña debe tener al menos 8 caracteres, una mayúscula, \" +\n" +
                    "                \"una minúscula, un número y un carácter especial");
        }

        user.setPassword(passwordEncoder.encode((newPassword)));
        userRepository.save(user);

        return ResponseEntity.ok("Contraseña cambiada con éxito");
    }

}


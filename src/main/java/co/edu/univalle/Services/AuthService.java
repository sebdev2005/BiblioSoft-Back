package co.edu.univalle.Services;

import co.edu.univalle.Auth.AuthResponse;
import co.edu.univalle.Auth.LoginRequest;
import co.edu.univalle.Auth.RegisterRequest;
import co.edu.univalle.Models.Role;
import co.edu.univalle.Models.UserModel;
import co.edu.univalle.Repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final JWTService jwtService;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;


    public AuthResponse login(LoginRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
        );

        UserModel user = userRepository.findByUsername(request.getUsername())

                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        String token = jwtService.getToken(user);
        return AuthResponse.builder()
                .token(token)
                .role(user.getRole().name())
                .message("Inicio de sesión exitoso.")
                .username(user.getUsername())
                .user(user)
                .build();
    }

    public AuthResponse register(RegisterRequest request) {

        if (request.getFirstname() == null || !request.getFirstname().matches("^[A-Za-zÁÉÍÓÚáéíóúñÑ\\s]{6,}$")) {
            return AuthResponse.builder()
                    .message("El nombre es obligatorio y debe tener más de 5 letras sin números ni símbolos.")
                    .build();
        }

        if (request.getLastname() == null || !request.getLastname().matches("^[A-Za-zÁÉÍÓÚáéíóúñÑ\\s]{6,}$")) {
            return AuthResponse.builder()
                    .message("El apellido es obligatorio y debe tener más de 5 letras sin números ni símbolos.")
                    .build();
        }

        if (request.getEmail() == null || !request.getEmail().matches("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$")) {
            return AuthResponse.builder()
                    .message("La dirección de email no es válida, verifique.")
                    .build();
        }

        if (request.getCode() == null || !request.getCode().matches("^[0-9]{1,20}$")) {
            return AuthResponse.builder()
                    .message("Código inválido, intente de nuevo.")
                    .build();
        }

        String passwordRegex = "^(?=.*[A-Z])(?=.*[0-9])(?=.*[^A-Za-z0-9]).{8,30}$";
        if (request.getPassword() == null || !Pattern.matches(passwordRegex, request.getPassword())) {
            return AuthResponse.builder()
                    .message("La contraseña debe contener 8-30 caracteres, 1 mayúscula, 1 símbolo y 1 número, por favor verifique.")
                    .build();
        }

        if (!request.getPassword().equals(request.getConfirmPassword())) {
            return AuthResponse.builder()
                    .message("Las contraseñas no coinciden, inténtelo de nuevo.")
                    .build();
        }

        boolean usernameExists = userRepository.findByUsername(request.getUsername()).isPresent();
        boolean codeExists = userRepository.findByUsername(request.getCode()).isPresent();
        boolean emailExists = userRepository.findAll().stream()
                .anyMatch(u -> u.getEmail().equalsIgnoreCase(request.getEmail()));

        if (codeExists || emailExists || usernameExists) {
            return AuthResponse.builder()
                    .message("El código o el email ya se encuentran registrados, por favor inicie sesión o verifique.")
                    .build();
        }

        UserModel userModel = UserModel.builder()
                .username(request.getUsername())
                .code(request.getCode())
                .password(passwordEncoder.encode(request.getPassword()))
                .firstname(request.getFirstname())
                .lastname(request.getLastname())
                .email(request.getEmail())
                .code(request.getCode())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(Role.USER)
                .build();

        userRepository.save(userModel);

        String token = jwtService.getToken(userModel);

        return AuthResponse.builder()
                .token(token)

                .role(userModel.getRole().name())
                .message("Usuario registrado exitosamente.")

                .build();
    }
}

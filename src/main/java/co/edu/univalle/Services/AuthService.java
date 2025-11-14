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

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final JWTService jwtService;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;

    public AuthResponse login(LoginRequest request){
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(request.getUsername() , request.getPassword()));
        UserModel user = userRepository.findByUsername(request.getUsername()).orElseThrow();
        String token =  jwtService.getToken(user);
        return AuthResponse.builder()
                .token(token)
                .role(user.getRole().name())
                .build();
    }
    public AuthResponse register(RegisterRequest request){
        UserModel userModel = UserModel.builder()
                .username(request.getUsername())
                .code(request.getCode())
                .password(passwordEncoder.encode(request.getPassword()))
                .firstname(request.getFirstname())
                .lastname(request.getLastname())
                .country(request.getCountry())
                .email(request.getEmail())
                .role(Role.USER).build();
        userRepository.save(userModel);
        String token = jwtService.getToken(userModel);
        return AuthResponse.builder()
                .token(token)
                .role(userModel.getRole().name())
                .build();
    }
}

package co.edu.univalle.Services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import co.edu.univalle.Models.UserModel;
import co.edu.univalle.Repositories.UserRepository;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder; // Para encriptar la contraseña

    public UserModel register(UserModel user) {
        // Validaciones básicas
        if (userRepository.findByUsername(user.getUsername()).isPresent()) {
            throw new RuntimeException("El nombre de usuario ya existe");
        }

        if (userRepository.findByCode(user.getCode()).isPresent()) {
            throw new RuntimeException("El código ya está registrado");
        }
        // Encriptar contraseña antes de guardar
        user.setPassword(passwordEncoder.encode(user.getPassword()));

        
        if (user.getRole() == null) {
            user.setRole(co.edu.univalle.Models.Role.USER);
        }

        return userRepository.save(user);
    }
}

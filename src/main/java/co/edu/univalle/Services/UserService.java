package co.edu.univalle.Services;


import co.edu.univalle.Models.LoanModel;
import co.edu.univalle.Repositories.LoanRepository;
import co.edu.univalle.Repositories.UserRepository;
import co.edu.univalle.Models.UserModel;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class UserService {

    private final UserRepository userRepository;
    private final LoanRepository loanRepository;
    private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public Optional<UserModel> findByUsernames(String username) {
        return userRepository.findByUsername(username);
    }

    public void changePassword(String username, String newPassword) {
        userRepository.findByUsername(username).ifPresent(userModel -> {
            userModel.setPassword(passwordEncoder.encode(newPassword));
            userRepository.save(userModel);
        });
    }

    public UserModel save(UserModel userModel) {
        userModel.setPassword(passwordEncoder.encode(userModel.getPassword()));
        return userRepository.save(userModel);
    }

    public UserModel buscarPorCodigo(String code) {
        return userRepository.findByCode(code).orElse(null);
    }

    public List<LoanModel> obtenerPrestamosUsuario(String codigo) {
        return loanRepository.findByUsuarioCode(codigo);

    }

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

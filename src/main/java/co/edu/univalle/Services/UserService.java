package co.edu.univalle.Services;

import co.edu.univalle.Models.PrestamoModel;
import co.edu.univalle.Repositories.PrestamoRepository;
import co.edu.univalle.Repositories.UserRepository;
import co.edu.univalle.Models.UserModel;
import lombok.RequiredArgsConstructor;
import org.apache.catalina.User;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
@RequiredArgsConstructor
@Service
public class UserService {

    private final UserRepository userRepository;
    private final PrestamoRepository prestamoRepository;
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

    public List<PrestamoModel> obtenerPrestamosUsuario(String codigo) {
        return prestamoRepository.findByUsuarioCode(codigo);
    }
}

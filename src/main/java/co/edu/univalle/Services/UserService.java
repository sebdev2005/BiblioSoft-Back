package co.edu.univalle.Services;

import co.edu.univalle.Repositories.UserRepository;
import co.edu.univalle.Models.UserModel;
import lombok.RequiredArgsConstructor;
import org.apache.catalina.User;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public Optional<UserModel> findByUsernames (String username) {
        return userRepository.findByUsername(username);
    }

    public void changePassword (String username, String newPassword) {
        userRepository.findByUsername(username).ifPresent(userModel -> {
            userModel.setPassword(passwordEncoder.encode(newPassword));
            userRepository.save(userModel);
        });
    }
    public UserModel save(UserModel userModel) {
        userModel.setPassword(passwordEncoder.encode(userModel.getPassword()));
        return userRepository.save(userModel);
    }

}
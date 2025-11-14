package co.edu.univalle.Repositories;

import co.edu.univalle.Models.UserModel;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<UserModel,Integer> {

    Optional<UserModel> findByUsername(String username);
    Optional<UserModel> findByCode(String code);
    Optional<UserModel> findByEmail(String email);

}

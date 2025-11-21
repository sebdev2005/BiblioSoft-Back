package co.edu.univalle.Repositories;

import co.edu.univalle.Models.Loan;
import co.edu.univalle.Models.PrestamoModel;
import co.edu.univalle.Models.UserModel;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PrestamoRepository extends JpaRepository<PrestamoModel, Long> {
    List<PrestamoModel> findByUsuario(UserModel usuario);
    List<PrestamoModel> findByUsuarioCode(String code);
    List<PrestamoModel> findByEstado(Loan estado);
}
package co.edu.univalle.Repositories;

import co.edu.univalle.Models.Loan;
import co.edu.univalle.Models.LoanModel;
import co.edu.univalle.Models.UserModel;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LoanRepository extends JpaRepository<LoanModel, Long> {
    List<LoanModel> findByUsuario(UserModel usuario);
    List<LoanModel> findByUsuarioCode(String code);
    List<LoanModel> findByEstado(Loan estado);
}
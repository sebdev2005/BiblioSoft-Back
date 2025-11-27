package co.edu.univalle.Repositories;

import co.edu.univalle.Models.FineModel;
import co.edu.univalle.Models.FineStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface FineRepository extends JpaRepository<FineModel, Long> {

    List<FineModel> findByPrestamo_Usuario_IdAndStatus(Long userId, FineStatus status);
    List<FineModel> findByPrestamo_UsuarioCode(String code);
    List<FineModel> findByPrestamoId(Long prestamoId);
    List<FineModel> findByStatus(FineStatus status);

}

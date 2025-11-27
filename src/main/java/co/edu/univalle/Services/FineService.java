package co.edu.univalle.Services;

import co.edu.univalle.Auth.FineRequest;
import co.edu.univalle.DTO.FineResponseDTO;
import co.edu.univalle.Exceptions.BadRequestException;
import co.edu.univalle.Models.FineModel;
import co.edu.univalle.Models.FineStatus;
import co.edu.univalle.Models.PrestamoModel;
import co.edu.univalle.Models.UserModel;
import co.edu.univalle.Repositories.FineRepository;
import co.edu.univalle.Repositories.PrestamoRepository;
import co.edu.univalle.Repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class FineService {

    private final FineRepository fineRepository;
    private final PrestamoRepository prestamoRepository;
    private final UserRepository userRepository;
    private static final double TARIFA_DIARIA = 1000.0;

    public FineResponseDTO getFineById(Long id) {
        FineModel fineModel = fineRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Multa no encontrada"));
        return matToDTO(fineModel);
    }

    public long calculateOverdueDays (Long prestamoId) {
        PrestamoModel prestamo = prestamoRepository.findById(prestamoId)
                .orElseThrow(() -> new IllegalArgumentException("Préstamo no encontrado"));

        LocalDate fechaDevolucion = prestamo.getFechaDevolucion();
        LocalDate fechaEntrega = prestamo.getFechaEntrega();

        if (fechaEntrega == null || fechaDevolucion == null) {
            throw new IllegalArgumentException("Fechas inválidas para calcular los días de atraso");
        }

        long diasAtraso = ChronoUnit.DAYS.between(fechaDevolucion, fechaEntrega);
        return Math.max(diasAtraso, 0); // Retorna 0 si no hay atraso
    }

    // ------------------ DTO HELPERS ------------------
    private FineResponseDTO matToDTO(FineModel fineModel) {
        return FineResponseDTO.builder()
                .id(fineModel.getId())
                .idPrestamo(fineModel.getPrestamo().getId())
                .userCode(fineModel.getPrestamo().getUsuarioCode())
                .userName(fineModel.getPrestamo().getUsuario().getFirstname()
                        + " " + fineModel.getPrestamo().getUsuario().getLastname())
                .valor(fineModel.getValor())
                .fechaMulta(LocalDate.now())
                .bookTitle(fineModel.getPrestamo().getLibro().getTitulo())
                .diasAtraso(calculateOverdueDays(fineModel.getPrestamo().getId()))
                .justificacion(fineModel.getJustication())
                .build();
    }

    // ------------------ CREATE FINE ------------------
    public FineResponseDTO createFine(Long id) {

        PrestamoModel prestamo = prestamoRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Préstamo no encontrado"));

        if(fineRepository.existsById(id)){
            throw new IllegalArgumentException("Multa ya existe para este préstamo");
        }

        long diasAtraso = calculateOverdueDays(id);

        // Si hay días de atraso, calcular la multa
        if (diasAtraso > 0) {
            double montoMulta = diasAtraso * TARIFA_DIARIA;

            FineModel fine = FineModel.builder()
                    .prestamo(prestamo)
                    .valor(montoMulta)
                    .status(FineStatus.PENDING)
                    .justication(null)
                    .build();

            FineModel savedFine = fineRepository.save(fine);

            UserModel user = prestamo.getUsuario();
            //user.setHasPendingFines(true);
            userRepository.save(user);

            return matToDTO(savedFine);

        }

        // Si no hay días de atraso, no se crea multa
        return FineResponseDTO.builder()
                .id(null)
                .valor(0.0)
                .build();
    }

    public List<FineResponseDTO> getAllFines() {
        List<FineModel> fines = fineRepository.findAll();
        return fines.stream()
                .map(this::matToDTO)
                .toList();
    }

    public List<FineResponseDTO> getFinesByUserCode(String userCode) {
        List<FineModel> fines = fineRepository.findByPrestamo_UsuarioCode(userCode);
        return fines.stream().map(this::matToDTO).toList();
    }

    // ------------------ EXONERATE FINE ------------------
    public FineResponseDTO exonerateFine(FineRequest request) {
        FineModel fineModel = fineRepository.findById(request.getFineId())
                .orElseThrow(() -> new IllegalArgumentException("Multa no encontrada"));

        UserModel user = fineModel.getPrestamo().getUsuario();
        //user.setHasPendingFines(false);
        userRepository.save(user);

        if(fineModel.getStatus() == FineStatus.EXONERATED){
            throw new BadRequestException("La multa ya ha sido exonerada");
        }

        FineModel exoneratedFine = FineModel.builder()
                .id(fineModel.getId())
                .prestamo(fineModel.getPrestamo())
                .valor(0.0)
                .status(FineStatus.EXONERATED)
                .justication(request.getJustification())
                .build();


        return matToDTO(fineRepository.save(exoneratedFine));
    }




}

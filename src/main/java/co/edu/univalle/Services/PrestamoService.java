package co.edu.univalle.Services;

import co.edu.univalle.Auth.LoanRequest;
import co.edu.univalle.DTO.LoanResponseDTO;
import co.edu.univalle.Exceptions.BadRequestException;
import co.edu.univalle.Exceptions.ResourceNotFoundException;
import co.edu.univalle.Models.BookModel;
import co.edu.univalle.Models.Estado;
import co.edu.univalle.Models.PrestamoModel;
import co.edu.univalle.Models.UserModel;
import co.edu.univalle.Repositories.BookRepository;
import co.edu.univalle.Repositories.PrestamoRepository;
import co.edu.univalle.Repositories.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PrestamoService {

    private final PrestamoRepository prestamoRepository;
    private final BookRepository bookRepository;
    private final UserRepository userRepository;

    // ------------------ DTO HELPERS ------------------

    private LoanResponseDTO matToDTO(PrestamoModel prestamoModel) {

        String fullName = prestamoModel.getUsuario().getFirstname() + " " + prestamoModel.getUsuario().getLastname();

        return LoanResponseDTO.builder()
                .id(prestamoModel.getId())
                .userFullName(fullName)
                .userCode(prestamoModel.getUsuarioCode())
                .bookId(prestamoModel.getLibro().getId())
                .bookTitle(prestamoModel.getLibro().getTitulo())
                .loanDate(prestamoModel.getFechaPrestamo())
                .returnDate(prestamoModel.getFechaDevolucion())
                .status(prestamoModel.getEstado())
                .build();
    }

    private List<LoanResponseDTO> matToDTOList(List<PrestamoModel> loanModels) {
        return loanModels.stream()
                .map(this::matToDTO)
                .toList();
    }


    // ------------------ RETURN LOAN ------------------

    @Transactional
    public LoanResponseDTO returnLoan(Long prestamoId) {

        PrestamoModel prestamo = prestamoRepository.findById(prestamoId)
                .orElseThrow(() -> new ResourceNotFoundException("Préstamo no encontrado"));

        if (prestamo.getEstado() == Estado.DEVUELTO) {
            throw new BadRequestException("El préstamo ya ha sido devuelto");
        }

        BookModel book = prestamo.getLibro();
        if (book == null) {
            throw new ResourceNotFoundException("Libro asociado al préstamo no encontrado");
        }

        prestamo.setEstado(Estado.DEVUELTO);
        prestamo.setFechaDevolucion(LocalDate.now());
        prestamoRepository.save(prestamo);

        Integer disponible = (book.getCantidadDisponible() == null) ? 0 : book.getCantidadDisponible();
        disponible++;

        if (book.getCantidadTotal() != null && disponible > book.getCantidadTotal()) {
            throw new BadRequestException("La cantidad disponible no puede ser mayor a la cantidad total");
        }

        book.setCantidadDisponible(disponible);
        bookRepository.save(book);

        return matToDTO(prestamo);
    }


    // ------------------ CREATE LOAN (ADMIN) ------------------

    public LoanResponseDTO createLoan(LoanRequest request) {
        UserModel user = userRepository.findByCode(request.getUserCode())
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));

        BookModel book = bookRepository.findById(request.getBookId())
                .orElseThrow(() -> new ResourceNotFoundException("Libro no encontrado"));

        if (book.getCantidadDisponible() <= 0) {
            throw new BadRequestException("No hay copias disponibles del libro solicitado");
        }

        PrestamoModel loan = PrestamoModel.builder()
                .usuario(user)
                .usuarioCode(user.getCode())
                .libro(book)
                .fechaPrestamo(LocalDate.now())
                .fechaDevolucion(LocalDate.now().plusDays(15))
                .estado(Estado.SOLICITADO)
                .build();

        PrestamoModel saved = prestamoRepository.save(loan);
        return matToDTO(saved);
    }


    // ------------------ SOLICITAR PRESTAMO (USER) ------------------

    public PrestamoModel solicitarPrestamo(Long userId, Long bookId) {

        UserModel user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        BookModel book = bookRepository.findById(bookId)
                .orElseThrow(() -> new RuntimeException("No se encuentra este libro"));

        if (book.getCantidadDisponible() <= 0) {
            throw new RuntimeException("No hay libros disponibles");
        }

        PrestamoModel prestamo = PrestamoModel.builder()
                .usuarioCode(user.getCode())
                .usuario(user)
                .libro(book)
                .estado(Estado.SOLICITADO)
                .renovaciones(0)
                .fechaSolicitud(LocalDate.now())
                .fechaPrestamo(LocalDate.now())
                .fechaDevolucion(LocalDate.now().plusDays(15))
                .build();

        return prestamoRepository.save(prestamo);
    }
    public PrestamoModel renovarPrestamo(Long prestamoId, String username){
        PrestamoModel prestamo = prestamoRepository.findById(prestamoId)
                .orElseThrow(() -> new RuntimeException("Prestamo no encontrado"));

        UserModel user = userRepository.findByUsername(username)
                .orElseThrow(()-> new RuntimeException("No se pudo encontrar el usuario"));
        if (!prestamo.getUsuario().getId().equals(user.getId())) {
            throw new RuntimeException("No puedes renovar un préstamo que no es tuyo");
        }

        if(prestamo.getEstado() != Estado.PRESTADO){
            throw new RuntimeException("El libro no ha sido prestado");
        }
        if(prestamo.getRenovaciones() >= 1 ){
            throw new RuntimeException("Limite de renovaciones alcanzado");
        }

        prestamo.setRenovaciones(prestamo.getRenovaciones() + 1);
        prestamo.setFechaDevolucion(prestamo.getFechaDevolucion().plusDays(15));
        return prestamoRepository.save(prestamo);

    }


    // ------------------ APROBAR PRESTAMO (USER PANEL) ------------------

    public PrestamoModel aprobarPrestamo(Long prestamoId) {

        PrestamoModel prestamo = prestamoRepository.findById(prestamoId)
                .orElseThrow(() -> new RuntimeException("El prestamo no existe"));

        if (prestamo.getEstado() != Estado.SOLICITADO) {
            throw new RuntimeException("El prestamo no ha sido solicitado");
        }

        BookModel book = prestamo.getLibro();

        if (book.getCantidadDisponible() <= 0) {
            throw new RuntimeException("No hay libros disponibles para aprobar este préstamo.");
        }

        book.setCantidadDisponible(book.getCantidadDisponible() - 1);
        bookRepository.save(book);

        prestamo.setEstado(Estado.PRESTADO);
        prestamo.setFechaPrestamo(LocalDate.now());
        prestamo.setFechaDevolucion(LocalDate.now().plusDays(15));

        return prestamoRepository.save(prestamo);
    }


    // ------------------ APROBAR PRESTAMO (ADMIN) ------------------

    public LoanResponseDTO approveLoan(Long loanId) {

        PrestamoModel loan = prestamoRepository.findById(loanId)
                .orElseThrow(() -> new ResourceNotFoundException("Préstamo no encontrado"));

        if (loan.getEstado() != Estado.SOLICITADO) {
            throw new BadRequestException("Solo se pueden aprobar préstamos en estado SOLICITADO");
        }
        BookModel book = loan.getLibro();
        book.setCantidadDisponible(book.getCantidadDisponible() - 1);
        loan.setEstado(Estado.PRESTADO);
        PrestamoModel updatedLoan = prestamoRepository.save(loan);

        return matToDTO(updatedLoan);
    }


    // ------------------ DATA GETTERS ------------------

    public List<LoanResponseDTO> getLoansByUserCode(String userCode) {
        return matToDTOList(prestamoRepository.findByUsuarioCode(userCode));
    }

    public List<LoanResponseDTO> getRequestedLoans() {
        return matToDTOList(prestamoRepository.findByEstado(Estado.SOLICITADO));
    }

    public List<LoanResponseDTO> getAllLoans() {
        return matToDTOList(prestamoRepository.findAll());
    }

    public List<LoanResponseDTO> getActiveLoans() {
        return matToDTOList(prestamoRepository.findByEstado(Estado.PRESTADO));
    }

    public List<LoanResponseDTO> getReturnedLoans() {
        return matToDTOList(prestamoRepository.findByEstado(Estado.DEVUELTO));
    }


    // ------------------ RECHAZAR / DEVOLVER ------------------

    public PrestamoModel rechazarPrestamo(Long prestamoId) {

        PrestamoModel prestamo = prestamoRepository.findById(prestamoId)
                .orElseThrow(() -> new RuntimeException("Préstamo no encontrado"));

        if (prestamo.getEstado() != Estado.SOLICITADO) {
            throw new RuntimeException("No se puede rechazar un préstamo que no está SOLICITADO.");
        }

        prestamo.setEstado(Estado.RECHAZADO);
        return prestamoRepository.save(prestamo);
    }

    public void devolverLibro(Long prestamoId) {

        PrestamoModel prestamo = prestamoRepository.findById(prestamoId)
                .orElseThrow(() -> new RuntimeException("Prestamo no encontrado"));

        prestamo.setEstado(Estado.DEVUELTO);

        BookModel book = prestamo.getLibro();
        book.setCantidadDisponible(book.getCantidadDisponible() + 1);
        bookRepository.save(book);

        prestamoRepository.save(prestamo);
    }

    public List<PrestamoModel> obtenerPrestamosPorUsuario(Long userId) {
        return prestamoRepository.findByUsuarioId(userId);
    }

    public List<PrestamoModel> obtenerTodosLosPrestamos() {
        return prestamoRepository.findAll();
    }
}

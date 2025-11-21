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
    private final PrestamoRepository loanRepository;
    private final BookRepository bookRepository;
    private final UserRepository userRepository;

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

    @Transactional
    public LoanResponseDTO returnLoan(Long prestamoId) {
        PrestamoModel prestamo = loanRepository.findById(prestamoId)
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
        loanRepository.save(prestamo);

        int disponible = book.getCantidadDisponible() == null ? 0 : book.getCantidadDisponible();
        disponible = disponible + 1;

        if (book.getCantidadTotal() != null && disponible > book.getCantidadTotal() ) {
            throw new BadRequestException(("La cantidad disponible no puede ser mayor a la cantidad total"));
        }

        book.setCantidadDisponible(disponible);
        bookRepository.save(book);

        return matToDTO(prestamo);
    }

    public LoanResponseDTO createLoan(LoanRequest request) {
        UserModel user = userRepository.findByCode(request.getUserCode())
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));
        BookModel book = bookRepository.findById(request.getBookId())
                .orElseThrow(() -> new ResourceNotFoundException("Libro no encontrado"));
        if (book.getCantidadDisponible() <= 0){
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

        book.setCantidadDisponible(book.getCantidadDisponible() - 1);
        bookRepository.save(book);

        PrestamoModel savedLoan = loanRepository.save(loan);

        return matToDTO(savedLoan);

        }

    public List<LoanResponseDTO> getLoansByUserCode(String  userCode)  {
        return matToDTOList(loanRepository.findByUsuarioCode(userCode));
    }

    public List<LoanResponseDTO> getRequestedLoans()  {
        return matToDTOList(loanRepository.findByEstado(Estado.SOLICITADO));
    }

    public LoanResponseDTO approveLoan(Long loanId) {
        PrestamoModel loan = loanRepository.findById(loanId)
                .orElseThrow(() -> new ResourceNotFoundException("Préstamo no encontrado"));

        if (loan.getEstado() != Estado.SOLICITADO) {
            throw new BadRequestException("Solo se pueden aprobar préstamos en estado SOLICITADO");
        }

        loan.setEstado(Estado.PRESTADO);
        PrestamoModel updatedLoan = loanRepository.save(loan);

        return matToDTO(updatedLoan);
    }

    public List<LoanResponseDTO> getAllLoans()  {
        return matToDTOList(loanRepository.findAll());
    }

    public List<LoanResponseDTO> getActiveLoans()  {
        return matToDTOList(loanRepository.findByEstado(Estado.PRESTADO));
    }

    public List<LoanResponseDTO> getReturnedLoans()  {
        return matToDTOList(loanRepository.findByEstado(Estado.DEVUELTO));
    }

}


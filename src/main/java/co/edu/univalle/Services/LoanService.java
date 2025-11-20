package co.edu.univalle.Services;

import co.edu.univalle.Auth.LoanRequest;
import co.edu.univalle.DTO.LoanResponseDTO;
import co.edu.univalle.Exceptions.BadRequestException;
import co.edu.univalle.Exceptions.ResourceNotFoundException;
import co.edu.univalle.Models.BookModel;
import co.edu.univalle.Models.Loan;
import co.edu.univalle.Models.LoanModel;
import co.edu.univalle.Models.UserModel;
import co.edu.univalle.Repositories.BookRepository;
import co.edu.univalle.Repositories.LoanRepository;
import co.edu.univalle.Repositories.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class LoanService {
    private final LoanRepository loanRepository;
    private final BookRepository bookRepository;
    private final UserRepository userRepository;

    private LoanResponseDTO matToDTO(LoanModel loanModel) {

        String fullName = loanModel.getUsuario().getFirstname() + " " + loanModel.getUsuario().getLastname();

        return LoanResponseDTO.builder()
                .id(loanModel.getId())
                .userFullName(fullName)
                .userCode(loanModel.getUsuarioCode())
                .bookId(loanModel.getLibro().getId())
                .bookTitle(loanModel.getLibro().getTitulo())
                .loanDate(loanModel.getFechaPrestamo())
                .returnDate(loanModel.getFechaDevolucion())
                .status(loanModel.getEstado())
                .build();
    }

    private List<LoanResponseDTO> matToDTOList(List<LoanModel> loanModels) {
        return loanModels.stream()
                .map(this::matToDTO)
                .toList();
    }

    @Transactional
    public LoanResponseDTO returnLoan(Long prestamoId) {
        LoanModel prestamo = loanRepository.findById(prestamoId)
                .orElseThrow(() -> new ResourceNotFoundException("Préstamo no encontrado"));
        if (prestamo.getEstado() == Loan.DEVUELTO) {
            throw new BadRequestException("El préstamo ya ha sido devuelto");
        }

        BookModel book = prestamo.getLibro();
        if (book == null) {
            throw new ResourceNotFoundException("Libro asociado al préstamo no encontrado");
        }
        prestamo.setEstado(Loan.DEVUELTO);
        prestamo.setFechaDevolucion(LocalDate.now());
        loanRepository.save(prestamo);

        Integer disponible = book.getCantidadDisponible() == null ? 0 : book.getCantidadDisponible();
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

        LoanModel loan = LoanModel.builder()
                .usuario(user)
                .usuarioCode(user.getCode())
                .libro(book)
                .fechaPrestamo(LocalDate.now())
                .fechaDevolucion(LocalDate.now().plusDays(15))
                .estado(Loan.PRESTADO)
                .build();

        book.setCantidadDisponible(book.getCantidadDisponible() - 1);
        bookRepository.save(book);

        LoanModel savedLoan = loanRepository.save(loan);

        return matToDTO(savedLoan);

        }

    public List<LoanResponseDTO> getAllLoans()  {
        return matToDTOList(loanRepository.findAll());
    }

    public List<LoanResponseDTO> getActiveLoans()  {
        return matToDTOList(loanRepository.findByEstado(Loan.PRESTADO));
    }

    public List<LoanResponseDTO> getReturnedLoans()  {
        return matToDTOList(loanRepository.findByEstado(Loan.DEVUELTO));
    }

}


package co.edu.univalle.Services;

import co.edu.univalle.Models.*;
import co.edu.univalle.Repositories.BookRepository;
import co.edu.univalle.Repositories.PrestamoRepository;
import co.edu.univalle.Repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PrestamoService {
    private final PrestamoRepository prestamoRepository;
    private final BookRepository bookRepository;
    private final UserRepository userRepository;

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
                .fechaSolicitud(LocalDate.now())
                .build();

        return prestamoRepository.save(prestamo);

    }

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

        prestamo.setFechaPrestamo(LocalDate.now());
        prestamo.setFechaDevolucion(LocalDate.now().plusDays(15));
        prestamo.setEstado(Estado.PRESTADO);

        return prestamoRepository.save(prestamo);
    }

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
    public List<PrestamoModel> obtenerTodosLosPrestamos(){
        return prestamoRepository.findAll();
    }
}

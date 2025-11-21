package co.edu.univalle.Controllers;

import co.edu.univalle.Models.PrestamoModel;
import co.edu.univalle.Models.UserModel;
import co.edu.univalle.Repositories.UserRepository;
import co.edu.univalle.Services.PrestamoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping(path = "/api/prestamo")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:5173")
public class PrestamoController {

    private final UserRepository userRepository;
    private final PrestamoService prestamoService;

    @PostMapping("/solicitar-libro")
    public ResponseEntity<?> solicitarPrestamo(
            @RequestParam Long bookId,
            Principal principal
    ) {
        // CORRECCIÓN: principal.getName() devuelve el USERNAME, no el email
        String username = principal.getName();

        System.out.println("🔍 [Controller] Username del Principal: " + username);

        // Buscar por USERNAME en lugar de email
        UserModel user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado con username: " + username));

        System.out.println("✅ [Controller] Usuario encontrado: " + user.getEmail());

        PrestamoModel prestamo = prestamoService.solicitarPrestamo(user.getId(), bookId);

        return ResponseEntity.ok(prestamo);
    }

    @PutMapping("/aprobar/{prestamoId}")
    public ResponseEntity<?> aprobarPrestamo(@PathVariable Long prestamoId) {
        PrestamoModel aprobado = prestamoService.aprobarPrestamo(prestamoId);
        return ResponseEntity.ok(aprobado);
    }

    @PutMapping("/rechazar/{prestamoId}")
    public ResponseEntity<?> rechazarPrestamo(@PathVariable Long prestamoId) {
        PrestamoModel rechazado = prestamoService.rechazarPrestamo(prestamoId);
        return ResponseEntity.ok(rechazado);
    }

    @PutMapping(path = "/devolver-libro/{prestamoId}")
    public ResponseEntity<String> devolver(@PathVariable Long prestamoId) {
        prestamoService.devolverLibro(prestamoId);
        return ResponseEntity.ok("Libro devuelto");
    }

    @GetMapping("/mis-prestamos")
    public ResponseEntity<?> obtenerMisPrestamos(Principal principal) {
        // CORRECCIÓN: Buscar por username
        String username = principal.getName();

        UserModel user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado con username: " + username));

        List<PrestamoModel> prestamos = prestamoService.obtenerPrestamosPorUsuario(user.getId());

        return ResponseEntity.ok(prestamos);
    }

    @GetMapping("/prestamos")
    public ResponseEntity<?> obtenerPrestamos() {
        return ResponseEntity.ok(prestamoService.obtenerTodosLosPrestamos());
    }
}
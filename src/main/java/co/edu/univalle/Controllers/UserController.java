package co.edu.univalle.Controllers;

import co.edu.univalle.Models.BookModel;
import co.edu.univalle.Models.PrestamoModel;
import co.edu.univalle.Models.UserModel;
import co.edu.univalle.Repositories.PrestamoRepository;
import co.edu.univalle.Repositories.UserRepository;
import co.edu.univalle.Services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.HttpStatus;

import java.util.*;

@CrossOrigin(origins = "http://localhost:5173")
@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/buscar/{codigo}")
    public ResponseEntity<?> buscarUsuarioPorCodigo(@PathVariable String codigo) {

        // 1. Validación del código
        if (codigo.length() > 20 || !codigo.matches("\\d+")) {
            return ResponseEntity
                    .badRequest()
                    .body("codigo invalido");
        }

        // 2. Buscar usuario
        UserModel usuario = userService.buscarPorCodigo(codigo);
        if (usuario == null) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body("usuario no encontrado");
        }

        // 3. Obtener préstamos
        List<PrestamoModel> prestamos = userService.obtenerPrestamosUsuario(codigo);

        // 4. Construir respuesta
        Map<String, Object> respuesta = new HashMap<>();
        respuesta.put("codigo", usuario.getCode());
        respuesta.put("nombre", usuario.getFirstname() + " " + usuario.getLastname());
        respuesta.put("prestamosRealizados", prestamos);

        // 5. Libros en poder
        List<BookModel> librosEnPoder = prestamos.stream()
                .filter(p -> p.getFechaDevolucion() == null)
                .map(PrestamoModel::getLibro)
                .toList();

        respuesta.put("librosEnPoder", librosEnPoder);

        return ResponseEntity.ok(respuesta);
    }

    @GetMapping("/{codigo}/prestamos")
    public ResponseEntity<?> obtenerPrestamos(@PathVariable String codigo) {
        return ResponseEntity.ok(userService.obtenerPrestamosUsuario(codigo));
    }
}

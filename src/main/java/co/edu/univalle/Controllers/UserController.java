package co.edu.univalle.Controllers;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import co.edu.univalle.Models.BookModel;
import co.edu.univalle.Models.Estado;
import co.edu.univalle.Models.PrestamoModel;
import co.edu.univalle.Models.UserModel;

import co.edu.univalle.Services.UserService;
import lombok.RequiredArgsConstructor;

@CrossOrigin(origins = "http://localhost:5173")
@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/buscar/{codigo}")
    public ResponseEntity<?> buscarUsuarioPorCodigo(@PathVariable String codigo) {

        if (codigo.length() > 20 || !codigo.matches("\\d+")) {
            return ResponseEntity.badRequest().body("codigo invalido");
        }

        UserModel usuario = userService.buscarPorCodigo(codigo);
        if (usuario == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("usuario no encontrado");
        }

        List<PrestamoModel> prestamos = userService.obtenerPrestamosUsuario(codigo);

        Map<String, Object> respuesta = new HashMap<>();
        respuesta.put("codigo", usuario.getCode());
        respuesta.put("nombre", usuario.getFirstname() + " " + usuario.getLastname());
        respuesta.put("prestamosRealizados", prestamos);

        List<BookModel> librosEnPoder = prestamos.stream()
                .filter(p -> p.getEstado() == Estado.PRESTADO)
                .map(PrestamoModel::getLibro)
                .toList();

        respuesta.put("librosEnPoder", librosEnPoder);

        return ResponseEntity.ok(respuesta);
    }

    @GetMapping("/{codigo}/prestamos")
    public ResponseEntity<?> obtenerPrestamos(@PathVariable String codigo) {
        return ResponseEntity.ok(userService.obtenerPrestamosUsuario(codigo));
    }

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody UserModel user) {
        try {
            UserModel nuevo = userService.register(user);

            return ResponseEntity.ok(Map.of(
                    "message", "Registro exitoso",
                    "user", nuevo
            ));

        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "message", e.getMessage(),
                    "errorType", e.getClass().getSimpleName()
            ));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of(
                    "message", "Error inesperado en el servidor: " + e.getMessage(),
                    "errorType", e.getClass().getSimpleName()
            ));
        }
    }

    @PostMapping("/example")
    public String welcome() {
        return "Welcome";
    }
}

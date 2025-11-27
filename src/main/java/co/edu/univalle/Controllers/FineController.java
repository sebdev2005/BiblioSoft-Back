package co.edu.univalle.Controllers;

import co.edu.univalle.Auth.FineRequest;
import co.edu.univalle.DTO.FineResponseDTO;
import co.edu.univalle.Services.FineService;
import co.edu.univalle.Services.PrestamoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/fines")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:5173")
public class FineController {

    private final PrestamoService prestamoService;
    private final FineService fineService;

    //@PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/fines-user/{userCode}")
    public ResponseEntity<List<FineResponseDTO>> getFinesByUserCode(@PathVariable String userCode) {
        return ResponseEntity.ok(prestamoService.getUserFines(userCode));
    }

    //@PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/all-fines")
    public ResponseEntity<List<FineResponseDTO>> getAllFines() {
        return ResponseEntity.ok(fineService.getAllFines());
    }


    //@PreAuthorize(("hasRole('ADMIN')"))
    @PutMapping("/exone-fine")
    public ResponseEntity<FineResponseDTO> exoneFine(@RequestBody FineRequest request) {
        FineResponseDTO exonedFine = fineService.exonerateFine(request);
        return ResponseEntity.ok(exonedFine);

    }

}

package co.edu.univalle.Controllers;


import co.edu.univalle.Auth.LoanRequest;
import co.edu.univalle.Auth.MessageResponse;
import co.edu.univalle.DTO.LoanResponseDTO;
import co.edu.univalle.Exceptions.BadRequestException;
import co.edu.univalle.Exceptions.ResourceNotFoundException;
import co.edu.univalle.Services.PrestamoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/loans")
@RequiredArgsConstructor
public class PrestamoController {
    private final PrestamoService loanService;

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/create")
    public ResponseEntity<?> createLoan(@RequestBody LoanRequest request) {
        try {
            LoanResponseDTO loan = loanService.createLoan(request);
            return ResponseEntity.ok(loan);
        } catch (Exception ex) {
            return ResponseEntity.status(400).body(new MessageResponse(ex.getMessage()));
        }
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/return/{id}")
    public ResponseEntity<?> returnLoan(@PathVariable("id") Long id) {
        try {
            LoanResponseDTO loan = loanService.returnLoan(id);
            return ResponseEntity.ok(loan);
        } catch (ResourceNotFoundException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new MessageResponse(ex.getMessage()));
        } catch (BadRequestException ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new MessageResponse(ex.getMessage()));
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new MessageResponse("Error inesperado ocurrido."));
        }
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/all")
    public ResponseEntity<?> getAllLoans() {
        return ResponseEntity.ok(loanService.getAllLoans());
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/active")
    public ResponseEntity<?> getActiveLoans() {
        return ResponseEntity.ok(loanService.getActiveLoans());
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/returned")
    public ResponseEntity<?> getReturnedLoans() {
        return ResponseEntity.ok(loanService.getReturnedLoans());
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/requesteds")
    public List<LoanResponseDTO> getRequestedLoans() {
        return loanService.getRequestedLoans();
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("approve/{id}")
    public ResponseEntity<LoanResponseDTO> approveLoan(@PathVariable Long id) {
        return ResponseEntity.ok(loanService.approveLoan(id));
    }

}

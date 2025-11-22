package co.edu.univalle.Auth;

import lombok.Data;

import java.time.LocalDate;

@Data
public class LoanRequest {
    String userCode;
    Long bookId;
    private LocalDate fechaPrestamo;
    private LocalDate fechaDevolucion;
}

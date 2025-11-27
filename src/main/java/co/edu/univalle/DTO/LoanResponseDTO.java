package co.edu.univalle.DTO;

import co.edu.univalle.Models.Estado;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

@Data
@Builder
@AllArgsConstructor
public class LoanResponseDTO {
    private Long id;
    private String userCode;
    private String userFullName;
    private Long bookId;
    private String bookTitle;
    private LocalDate loanDate;
    private LocalDate returnDate;
    private LocalDate deliveryDate;
    private Estado status;
}

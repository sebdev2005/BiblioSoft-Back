package co.edu.univalle.DTO;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.time.LocalDate;


@Data
@AllArgsConstructor
@Builder
public class FineResponseDTO {
    private Long id;
    private String userCode;
    private String userName;
    private Long idPrestamo;
    private Double valor;
    private Long diasAtraso;
    private LocalDate fechaMulta;
    private String bookTitle;
    private String justificacion;
}

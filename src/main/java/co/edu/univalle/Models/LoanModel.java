package co.edu.univalle.Models;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "loans")
public class LoanModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 20)
    private String usuarioCode;

    @ManyToOne
    @JoinColumn(name = "usuario_id", nullable = false)
    private UserModel usuario;

    @ManyToOne
    @JoinColumn(name = "libro_id", nullable = false)
    private BookModel libro;

    @Column(nullable = false)
    private LocalDate fechaPrestamo;

    private LocalDate fechaDevolucion;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private Loan estado = Loan.PRESTADO;
}

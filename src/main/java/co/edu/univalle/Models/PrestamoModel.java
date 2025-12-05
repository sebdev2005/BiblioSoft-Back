package co.edu.univalle.Models;

import jakarta.persistence.*;
import lombok.*;


import java.time.LocalDate;


@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "prestamos")
public class PrestamoModel {

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

    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    @Builder.Default// Especificar longitud suficiente para el enum
    private Estado estado = Estado.PRESTADO;

    @Column
    @Builder.Default
    private Integer renovaciones = 0;

    @Column(nullable = false)
    private LocalDate fechaSolicitud;


    // CORRECCIÓN: fechaPrestamo puede ser null cuando el estado es SOLICITADO
    @Column(nullable = true)  // Cambiado de false a true
    private LocalDate fechaPrestamo;

    // fechaDevolucion ya permite null, está correcto
    @Column(nullable = true)
    private LocalDate fechaDevolucion;

    @Column(nullable = true)
    private LocalDate fechaEntrega;




}


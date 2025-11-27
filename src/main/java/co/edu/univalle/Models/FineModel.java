package co.edu.univalle.Models;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "fines")
public class FineModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "prestamo_id")
    private PrestamoModel prestamo;

    @Column(nullable = false)
    private Double valor;

    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    @Builder.Default
    private FineStatus status = FineStatus.PENDING;

    @Column (nullable = true, length = 200)
    private String justication;


}

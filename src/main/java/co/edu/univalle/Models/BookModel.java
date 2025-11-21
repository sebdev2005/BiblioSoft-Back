package co.edu.univalle.Models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
//import org.springframework.data.repository.support.Repositories;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "books")
public class BookModel  {

    @Id

    @Column(name = "bk-id",nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "bk-titulo" ,length = 50, nullable = false)
    private String titulo;
    @Column(name = "bk-autor",length = 70, nullable = false)
    private String autor;
    @Column(name = "bk-anio" ,nullable = false)
    private int anio;
    @Column(name = "bk-editorial" ,length = 70, nullable = false)
    private String editorial;
    @Column (name = "bk-cantidad_total" ,nullable = false)
    private Integer cantidadTotal;
    @Column (name = "bk-cantidad_disponible" ,nullable = false)
    private Integer cantidadDisponible;
}

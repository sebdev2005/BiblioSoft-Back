package co.edu.univalle.Repositories;

import co.edu.univalle.Models.BookModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BookRepository extends JpaRepository<BookModel,Long> {
    List<BookModel> findByTituloContainingIgnoreCaseOrAutorContainingIgnoreCaseOrEditorialContainingIgnoreCase(
            String titulo, String autor, String editorial);
}

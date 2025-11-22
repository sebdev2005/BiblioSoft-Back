package co.edu.univalle.Controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import co.edu.univalle.Models.BookModel;
import co.edu.univalle.Services.BookService;
@CrossOrigin(origins = "http://localhost:5173")
@RestController()
@RequestMapping(path = "/api/book")
public class BookController {
    private final BookService bookService;

    @Autowired
    public BookController(BookService bookService) {
        this.bookService = bookService;
    }

    @GetMapping(path = "/allBooks")
    public List<BookModel> findAllBooks() {
        return bookService.findAll();
    }

    @PostMapping(path = "/save")
    public BookModel createBook(@RequestBody BookModel bookModel){
        return bookService.createBook(bookModel);
    }
    @PutMapping(path = "/edit")
    public BookModel editBook(@RequestBody BookModel bookModel){
        return bookService.updateBook(bookModel);
    }
    @DeleteMapping(path = "/deleteBook/{id}")
    public ResponseEntity<String> deleteBook(@PathVariable Long id){
        bookService.deleteBook(id);

        return ResponseEntity.ok("Libro eliminado");

    }

    @GetMapping("/search")
    public List<BookModel> searchBooks(@RequestParam("query") String query) {
        return bookService.searchBooks(query);
    }

    @GetMapping("/ping")
    public String ping() {
        return "OK";
    }

}


package edu.ku.book_api.controller;

import edu.ku.book_api.model.Book;
import edu.ku.book_api.model.BookInput;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/v1/books")
public class BookController {

    private final List<Book> books = new ArrayList<>(List.of(
            new Book(1L, "Java Programming", "John Smith", 5),
            new Book(2L, "Web Development", "Sara Ahmad", 3),
            new Book(3L, "Database Systems", "Ali Khan", 4)
    ));

    // GET ALL BOOKS
    @GetMapping
    public List<Book> getAllBooks() {
        return books;
    }

    // UPDATE BOOK
    @PutMapping("/{bookId}")
    public ResponseEntity<Book> updateBook(
            @PathVariable Long bookId,
            @RequestBody BookInput input
    ) {

        for (int i = 0; i < books.size(); i++) {

            Book book = books.get(i);

            if (book.id().equals(bookId)) {

                Book updatedBook = new Book(
                        book.id(),
                        input.title(),
                        input.author(),
                        input.availableCopies()
                );

                books.set(i, updatedBook);

                return ResponseEntity.ok(updatedBook);
            }
        }

        return ResponseEntity.notFound().build();
    }

    // DELETE BOOK
    @DeleteMapping("/{bookId}")
    public ResponseEntity<Void> deleteBook(
            @PathVariable Long bookId
    ) {

        for (int i = 0; i < books.size(); i++) {

            Book book = books.get(i);

            if (book.id().equals(bookId)) {

                books.remove(i);

                return ResponseEntity.noContent().build();
            }
        }

        return ResponseEntity.notFound().build();
    }
}
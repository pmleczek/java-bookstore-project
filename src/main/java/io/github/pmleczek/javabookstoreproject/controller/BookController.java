package io.github.pmleczek.javabookstoreproject.controller;

import io.github.pmleczek.javabookstoreproject.entity.Book;
import io.github.pmleczek.javabookstoreproject.service.BookService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/books")
@RequiredArgsConstructor
public class BookController {

    private final BookService bookService;

    @GetMapping
    public List<Book> getAll() {
        return bookService.getAllBooks();
    }

    @GetMapping("/{id}")
    public Book getById(
            @PathVariable Long id
    ) {
        return bookService.getBook(id);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public Book create(
            @RequestBody Book book
    ) {
        return bookService.addBook(book);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public void delete(
            @PathVariable Long id
    ) {
        bookService.deleteBook(id);
    }
}

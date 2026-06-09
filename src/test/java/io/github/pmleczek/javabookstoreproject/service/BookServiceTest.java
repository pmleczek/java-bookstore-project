package io.github.pmleczek.javabookstoreproject.service;

import io.github.pmleczek.javabookstoreproject.entity.Book;
import io.github.pmleczek.javabookstoreproject.repository.BookRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BookServiceTest {

    @Mock
    private BookRepository bookRepository;

    @InjectMocks
    private BookService bookService;

    @Test
    void getAllBooks_returnsAllBooks() {
        List<Book> books = List.of(new Book(1L, "Title", "Author", 5));
        when(bookRepository.findAll()).thenReturn(books);

        assertThat(bookService.getAllBooks()).isEqualTo(books);
    }

    @Test
    void getBook_whenFound_returnsBook() {
        Book book = new Book(1L, "Title", "Author", 5);
        when(bookRepository.findById(1L)).thenReturn(Optional.of(book));

        assertThat(bookService.getBook(1L)).isEqualTo(book);
    }

    @Test
    void getBook_whenNotFound_throws() {
        when(bookRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> bookService.getBook(99L))
                .isInstanceOf(NoSuchElementException.class);
    }

    @Test
    void addBook_savesAndReturns() {
        Book book = new Book(null, "Title", "Author", 5);
        Book saved = new Book(1L, "Title", "Author", 5);
        when(bookRepository.save(book)).thenReturn(saved);

        assertThat(bookService.addBook(book)).isEqualTo(saved);
    }

    @Test
    void updateBook_updatesFieldsAndSaves() {
        Book existing = new Book(1L, "Old Title", "Old Author", 3);
        Book update = new Book(null, "New Title", "New Author", 10);
        when(bookRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(bookRepository.save(existing)).thenReturn(existing);

        Book result = bookService.updateBook(1L, update);

        assertThat(result.getTitle()).isEqualTo("New Title");
        assertThat(result.getAuthor()).isEqualTo("New Author");
        assertThat(result.getQuantity()).isEqualTo(10);
    }

    @Test
    void deleteBook_callsDeleteById() {
        bookService.deleteBook(1L);

        verify(bookRepository).deleteById(1L);
    }
}

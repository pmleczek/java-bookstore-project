package io.github.pmleczek.javabookstoreproject.service;

import io.github.pmleczek.javabookstoreproject.entity.Book;
import io.github.pmleczek.javabookstoreproject.entity.Reservation;
import io.github.pmleczek.javabookstoreproject.entity.User;
import io.github.pmleczek.javabookstoreproject.lib.UserRole;
import io.github.pmleczek.javabookstoreproject.repository.BookRepository;
import io.github.pmleczek.javabookstoreproject.repository.ReservationRepository;
import io.github.pmleczek.javabookstoreproject.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ReservationServiceTest {

    @Mock
    private ReservationRepository reservationRepository;
    @Mock
    private BookRepository bookRepository;
    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private ReservationService reservationService;

    @Test
    void reserveBook_happyPath_decrementsQuantityAndSavesReservation() {
        Book book = new Book(1L, "Title", "Author", 3);
        User user = User.builder().id(1L).username("alice").password("pw").role(UserRole.USER).build();
        when(bookRepository.findById(1L)).thenReturn(Optional.of(book));
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(reservationRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        Reservation result = reservationService.reserveBook(1L, 1L);

        assertThat(book.getQuantity()).isEqualTo(2);
        assertThat(result.isReturned()).isFalse();
        assertThat(result.getReservationDate()).isEqualTo(LocalDate.now());
        assertThat(result.getBook()).isEqualTo(book);
        assertThat(result.getUser()).isEqualTo(user);
    }

    @Test
    void reserveBook_whenQuantityIsZero_throwsRuntimeException() {
        Book book = new Book(1L, "Title", "Author", 0);
        when(bookRepository.findById(1L)).thenReturn(Optional.of(book));

        assertThatThrownBy(() -> reservationService.reserveBook(1L, 1L))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Book unavailable");
    }

    @Test
    void reserveBook_whenBookNotFound_throws() {
        when(bookRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> reservationService.reserveBook(1L, 99L))
                .isInstanceOf(NoSuchElementException.class);
    }

    @Test
    void reserveBook_whenUserNotFound_throws() {
        Book book = new Book(1L, "Title", "Author", 3);
        when(bookRepository.findById(1L)).thenReturn(Optional.of(book));
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> reservationService.reserveBook(99L, 1L))
                .isInstanceOf(NoSuchElementException.class);
    }

    @Test
    void getUserReservations_delegatesToRepository() {
        List<Reservation> reservations = List.of(new Reservation());
        when(reservationRepository.findByUserId(1L)).thenReturn(reservations);

        assertThat(reservationService.getUserReservations(1L)).isEqualTo(reservations);
    }

    @Test
    void getAllReservations_delegatesToRepository() {
        List<Reservation> reservations = List.of(new Reservation(), new Reservation());
        when(reservationRepository.findAll()).thenReturn(reservations);

        assertThat(reservationService.getAllReservations()).isEqualTo(reservations);
    }
}

package io.github.pmleczek.javabookstoreproject.service;

import io.github.pmleczek.javabookstoreproject.entity.Book;
import io.github.pmleczek.javabookstoreproject.entity.Reservation;
import io.github.pmleczek.javabookstoreproject.entity.User;
import io.github.pmleczek.javabookstoreproject.repository.BookRepository;
import io.github.pmleczek.javabookstoreproject.repository.ReservationRepository;
import io.github.pmleczek.javabookstoreproject.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ReservationService {

    private final ReservationRepository reservationRepository;
    private final BookRepository bookRepository;
    private final UserRepository userRepository;

    @Transactional
    public Reservation reserveBook(
            Long userId,
            Long bookId
    ) {

        Book book = bookRepository.findById(bookId)
                .orElseThrow();

        if(book.getQuantity() <= 0) {
            throw new RuntimeException("Book unavailable");
        }

        book.setQuantity(book.getQuantity() - 1);
        bookRepository.save(book);

        User user = userRepository.findById(userId)
                .orElseThrow();

        Reservation reservation = Reservation.builder()
                .book(book)
                .user(user)
                .reservationDate(LocalDate.now())
                .returned(false)
                .build();

        return reservationRepository.save(reservation);
    }

    public List<Reservation> getUserReservations(
            Long userId
    ) {
        return reservationRepository.findByUserId(userId);

    }

    public List<Reservation> getAllReservations() {
        return reservationRepository.findAll();
    }

}

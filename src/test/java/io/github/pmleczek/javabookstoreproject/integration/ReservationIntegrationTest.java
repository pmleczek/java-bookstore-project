package io.github.pmleczek.javabookstoreproject.integration;

import io.github.pmleczek.javabookstoreproject.entity.Book;
import io.github.pmleczek.javabookstoreproject.entity.Reservation;
import io.github.pmleczek.javabookstoreproject.entity.User;
import io.github.pmleczek.javabookstoreproject.lib.UserRole;
import io.github.pmleczek.javabookstoreproject.repository.BookRepository;
import io.github.pmleczek.javabookstoreproject.repository.ReservationRepository;
import io.github.pmleczek.javabookstoreproject.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class ReservationIntegrationTest extends BaseIntegrationTest {

    @Autowired private ReservationRepository reservationRepository;
    @Autowired private BookRepository bookRepository;
    @Autowired private UserRepository userRepository;
    @Autowired private PasswordEncoder passwordEncoder;

    @Test
    @DisplayName("POST /api/reservations/{bookId} creates reservation and decrements quantity")
    void reserve_createsReservationAndDecrementsQuantity() throws Exception {
        User savedUser = userRepository.save(User.builder()
                .username("alice")
                .password(passwordEncoder.encode("password"))
                .role(UserRole.USER)
                .build());
        Book savedBook = bookRepository.save(new Book(null, "Test Book", "Author", 3));

        mockMvc.perform(post("/api/reservations/" + savedBook.getId())
                        .with(user(savedUser.getId().toString()).roles("USER")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.returned").value(false))
                .andExpect(jsonPath("$.book.id").value(savedBook.getId()));

        assertThat(bookRepository.findById(savedBook.getId()).get().getQuantity()).isEqualTo(2);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("GET /api/admin/reservations returns all reservations")
    void getAllReservations_returnsAllReservations() throws Exception {
        User savedUser = userRepository.save(User.builder()
                .username("bob")
                .password(passwordEncoder.encode("password"))
                .role(UserRole.USER)
                .build());
        Book savedBook = bookRepository.save(new Book(null, "Book A", "Author", 5));
        reservationRepository.save(Reservation.builder()
                .user(savedUser).book(savedBook)
                .reservationDate(LocalDate.now())
                .returned(false)
                .build());

        mockMvc.perform(get("/api/admin/reservations"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("GET /api/admin/reservations/user/{id} returns reservations for a user")
    void getUserReservations_returnsUserReservations() throws Exception {
        User savedUser = userRepository.save(User.builder()
                .username("carol")
                .password(passwordEncoder.encode("password"))
                .role(UserRole.USER)
                .build());
        Book savedBook = bookRepository.save(new Book(null, "Book B", "Author", 2));
        reservationRepository.save(Reservation.builder()
                .user(savedUser).book(savedBook)
                .reservationDate(LocalDate.now())
                .returned(false)
                .build());

        mockMvc.perform(get("/api/admin/reservations/user/" + savedUser.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].user.id").value(savedUser.getId()));
    }
}

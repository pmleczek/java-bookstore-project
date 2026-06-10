package io.github.pmleczek.javabookstoreproject.controller;

import io.github.pmleczek.javabookstoreproject.entity.Reservation;
import io.github.pmleczek.javabookstoreproject.service.ReservationService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/reservations")
@RequiredArgsConstructor
public class ReservationController {

    private final ReservationService reservationService;

    @PreAuthorize("hasRole('USER')")
    @PostMapping("/{bookId}")
    public Reservation reserve(
            @AuthenticationPrincipal UserDetails user,
            @PathVariable Long bookId
    ) {

        return reservationService.reserveBook(
                Long.parseLong(user.getUsername()),
                bookId
        );
    }
}
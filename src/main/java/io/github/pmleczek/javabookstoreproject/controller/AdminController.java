package io.github.pmleczek.javabookstoreproject.controller;

import io.github.pmleczek.javabookstoreproject.entity.Reservation;
import io.github.pmleczek.javabookstoreproject.service.ReservationService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminController {

    private final ReservationService reservationService;

    @GetMapping("/reservations")
    @PreAuthorize("hasRole('ADMIN')")
    public List<Reservation> getAllReservations() {

        return reservationService.getAllReservations();
    }

    @GetMapping("/reservations/user/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public List<Reservation> getUserReservations(
            @PathVariable Long id
    ) {

        return reservationService
                .getUserReservations(id);
    }
}

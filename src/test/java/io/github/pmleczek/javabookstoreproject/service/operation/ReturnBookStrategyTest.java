package io.github.pmleczek.javabookstoreproject.service.operation;

import io.github.pmleczek.javabookstoreproject.entity.Reservation;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

class ReturnBookStrategyTest {

    private final ReturnBookStrategy strategy = new ReturnBookStrategy();

    @Test
    void execute_setsReturnedTrueAndSetsReturnDate() {
        Reservation reservation = new Reservation();

        strategy.execute(reservation);

        assertThat(reservation.isReturned()).isTrue();
        assertThat(reservation.getReturnDate()).isEqualTo(LocalDate.now());
    }
}

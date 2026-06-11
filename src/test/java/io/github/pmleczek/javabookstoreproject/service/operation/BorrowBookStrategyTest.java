package io.github.pmleczek.javabookstoreproject.service.operation;

import io.github.pmleczek.javabookstoreproject.entity.Reservation;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class BorrowBookStrategyTest {

    private final BorrowBookStrategy strategy = new BorrowBookStrategy();

    @Test
    void execute_setsReturnedFalse() {
        Reservation reservation = new Reservation();
        reservation.setReturned(true);

        strategy.execute(reservation);

        assertThat(reservation.isReturned()).isFalse();
    }
}

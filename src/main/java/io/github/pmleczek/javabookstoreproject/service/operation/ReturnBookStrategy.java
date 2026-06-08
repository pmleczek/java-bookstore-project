package io.github.pmleczek.javabookstoreproject.service.operation;

import io.github.pmleczek.javabookstoreproject.entity.Reservation;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
public class ReturnBookStrategy
        implements BookOperationStrategy {

    @Override
    public void execute(Reservation reservation) {

        reservation.setReturned(true);
        reservation.setReturnDate(LocalDate.now());
    }
}

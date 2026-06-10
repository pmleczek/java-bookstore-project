package io.github.pmleczek.javabookstoreproject.service.operation;


import io.github.pmleczek.javabookstoreproject.entity.Reservation;
import org.springframework.stereotype.Component;

@Component
public class BorrowBookStrategy
        implements BookOperationStrategy {

    @Override
    public void execute(Reservation reservation) {

        reservation.setReturned(false);
    }
}

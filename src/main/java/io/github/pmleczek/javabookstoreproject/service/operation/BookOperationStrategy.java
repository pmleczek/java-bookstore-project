package io.github.pmleczek.javabookstoreproject.service.operation;

import io.github.pmleczek.javabookstoreproject.entity.Reservation;

public interface BookOperationStrategy {

    void execute(Reservation reservation);
}
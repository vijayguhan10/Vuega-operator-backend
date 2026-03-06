package net.vuega.vuega_backend.Operator_pannel.exception;

public class PassengerNotFoundException extends RuntimeException {

    public PassengerNotFoundException(Long passengerId) {
        super("Passenger not found with id: " + passengerId);
    }
}

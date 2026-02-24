package net.vuega.vuega_backend.Exception;

public class PassengerNotFoundException extends RuntimeException {

    public PassengerNotFoundException(Long passengerId) {
        super("Passenger not found with id: " + passengerId);
    }
}

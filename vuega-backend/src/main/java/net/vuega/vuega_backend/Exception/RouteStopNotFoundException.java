package net.vuega.vuega_backend.Exception;

/**
 * Exception thrown when a route stop is not found.
 */
public class RouteStopNotFoundException extends RuntimeException {

    public RouteStopNotFoundException(Long stopId) {
        super("Route stop with ID " + stopId + " not found.");
    }

    public RouteStopNotFoundException(String message) {
        super(message);
    }
}

error id: file://<WORKSPACE>/src/main/java/net/vuega/vuega_backend/Operator_pannel/Controller/seats/booking/SeatBookingController.java:net/vuega/vuega_backend/Operator_pannel/DTO/seats/seat/bookings/BookingDTO#
file://<WORKSPACE>/src/main/java/net/vuega/vuega_backend/Operator_pannel/Controller/seats/booking/SeatBookingController.java
empty definition using pc, found symbol in pc: net/vuega/vuega_backend/Operator_pannel/DTO/seats/seat/bookings/BookingDTO#
empty definition using semanticdb
empty definition using fallback
non-local guesses:

offset: 1063
uri: file://<WORKSPACE>/src/main/java/net/vuega/vuega_backend/Operator_pannel/Controller/seats/booking/SeatBookingController.java
text:
```scala
package net.vuega.vuega_backend.Operator_pannel.Controller.seats.booking;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
//import org.springframework.transaction.annotation.Transactional;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import net.vuega.vuega_backend.Operator_pannel.DTO.ResponseDto;
import net.vuega.vuega_backend.Operator_pannel.DTO.bookings.MultiSeatBookingRequest;
import net.vuega.vuega_backend.Operator_pannel.DTO.bookings.MultiSeatBookingResponse;
import net.vuega.vuega_backend.Operator_pannel.DTO.seats.seat.bookings.@@BookingDTO;
import net.vuega.vuega_backend.Operator_pannel.Service.bookings.MultiSeatBookingService;
import net.vuega.vuega_backend.Operator_pannel.Service.seats.lock.SeatLockService;
import net.vuega.vuega_backend.Operator_pannel.exception.BookingNotFoundException;
import net.vuega.vuega_backend.Operator_pannel.exception.InvalidStopRangeException;
import net.vuega.vuega_backend.Operator_pannel.exception.SeatLockConflictException;
import net.vuega.vuega_backend.Operator_pannel.exception.SeatMismatchException;
import net.vuega.vuega_backend.Operator_pannel.exception.SeatNotAvailableException;
import net.vuega.vuega_backend.Operator_pannel.exception.SessionExpiredException;
import net.vuega.vuega_backend.Operator_pannel.exception.SessionNotFoundException;

/**
 * Single unified booking endpoint.
 * POST /api/seats/booking — creates bookings, passengers, booking_passengers,
 * seat_status
 * in one @Transactional call.
 */
@RestController
@RequestMapping("/api/seats/booking")
@RequiredArgsConstructor
public class SeatBookingController {

    private final MultiSeatBookingService multiSeatBookingService;
    private final SeatLockService seatLockService;

    /**
     * Atomic multi-seat booking — validates session & locks, then inserts into
     * bookings, passengers, booking_passengers, and seat_status in one transaction.
     */
    @PostMapping
    public ResponseEntity<ResponseDto<MultiSeatBookingResponse>> createBooking(
            @Valid @RequestBody MultiSeatBookingRequest request) {
        try {
            MultiSeatBookingResponse response = multiSeatBookingService.createBooking(request);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(ResponseDto.created(response));
        } catch (SessionNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ResponseDto.notFound(e.getMessage()));
        } catch (SessionExpiredException e) {
            return ResponseEntity.status(HttpStatus.GONE)
                    .body(ResponseDto.error(410, e.getMessage()));
        } catch (SeatMismatchException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ResponseDto.error(400, e.getMessage()));
        } catch (SeatNotAvailableException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(ResponseDto.error(409, e.getMessage()));
        } catch (InvalidStopRangeException e) {
            return ResponseEntity.status(422)
                    .body(ResponseDto.error(422, e.getMessage()));
        }
    }

    /**
     * Cancel a seat booking (soft delete — status set to CANCELLED).
     */
    @PostMapping("/{seatStatusId}/cancel")
    public ResponseEntity<ResponseDto<BookingDTO>> cancelBooking(
            @PathVariable Long seatStatusId,
            @RequestParam Long passengerId) {
        try {
            return ResponseEntity.ok(ResponseDto.success(seatLockService.cancelBooking(seatStatusId, passengerId)));
        } catch (BookingNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ResponseDto.notFound(e.getMessage()));
        } catch (SeatLockConflictException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(ResponseDto.error(403, e.getMessage()));
        }
    }

    /**
     * Booking history for a passenger.
     */
    @GetMapping
    public ResponseEntity<ResponseDto<List<BookingDTO>>> getBookingHistory(
            @RequestParam Long passengerId) {
        return ResponseEntity.ok(ResponseDto.success(seatLockService.getBookingHistory(passengerId)));
    }
}

```


#### Short summary: 

empty definition using pc, found symbol in pc: net/vuega/vuega_backend/Operator_pannel/DTO/seats/seat/bookings/BookingDTO#
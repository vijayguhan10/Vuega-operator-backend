error id: file:///C:/Projects/Vuega-backend/vuega-backend/src/main/java/net/vuega/vuega_backend/Controller/bookings/BookingController.java:net/vuega/vuega_backend/Exception/SeatNotAvailableException#
file:///C:/Projects/Vuega-backend/vuega-backend/src/main/java/net/vuega/vuega_backend/Controller/bookings/BookingController.java
empty definition using pc, found symbol in pc: net/vuega/vuega_backend/Exception/SeatNotAvailableException#
empty definition using semanticdb
empty definition using fallback
non-local guesses:

offset: 1232
uri: file:///C:/Projects/Vuega-backend/vuega-backend/src/main/java/net/vuega/vuega_backend/Controller/bookings/BookingController.java
text:
```scala
package net.vuega.vuega_backend.Controller.bookings;

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

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import net.vuega.vuega_backend.DTO.ResponseDto;
import net.vuega.vuega_backend.DTO.bookings.MultiSeatBookingRequest;
import net.vuega.vuega_backend.DTO.bookings.MultiSeatBookingResponse;
import net.vuega.vuega_backend.DTO.seats.seat.bookings.BookingDTO;
import net.vuega.vuega_backend.Exception.BookingNotFoundException;
import net.vuega.vuega_backend.Exception.InvalidStopRangeException;
import net.vuega.vuega_backend.Exception.SeatLockConflictException;
import net.vuega.vuega_backend.Exception.SeatMismatchException;
import net.vuega.vuega_backend.Exception.@@SeatNotAvailableException;
import net.vuega.vuega_backend.Exception.SessionExpiredException;
import net.vuega.vuega_backend.Exception.SessionNotFoundException;
import net.vuega.vuega_backend.Service.bookings.MultiSeatBookingService;
import net.vuega.vuega_backend.Service.seats.lock.SeatLockService;

@RestController
@RequestMapping("/api/bookings")
@RequiredArgsConstructor
public class BookingController {

    private final SeatLockService seatLockService;
    private final MultiSeatBookingService multiSeatBookingService;

    // Atomic multi-seat booking — validates session, locks, overlap, then books
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

    // Cancel a booking — status set to CANCELLED, record retained
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

    // Booking history for a passenger
    @GetMapping
    public ResponseEntity<ResponseDto<List<BookingDTO>>> getBookingHistory(
            @RequestParam Long passengerId) {
        return ResponseEntity.ok(ResponseDto.success(seatLockService.getBookingHistory(passengerId)));
    }
}

```


#### Short summary: 

empty definition using pc, found symbol in pc: net/vuega/vuega_backend/Exception/SeatNotAvailableException#
error id: file:///C:/Projects/Vuega-backend/vuega-backend/src/main/java/net/vuega/vuega_backend/Controller/seats/BulkSeatBookingController.java:_empty_/SeatLockService#bookMultipleSeats#
file:///C:/Projects/Vuega-backend/vuega-backend/src/main/java/net/vuega/vuega_backend/Controller/seats/BulkSeatBookingController.java
empty definition using pc, found symbol in pc: _empty_/SeatLockService#bookMultipleSeats#
empty definition using semanticdb
empty definition using fallback
non-local guesses:

offset: 2664
uri: file:///C:/Projects/Vuega-backend/vuega-backend/src/main/java/net/vuega/vuega_backend/Controller/seats/BulkSeatBookingController.java
text:
```scala
package net.vuega.vuega_backend.Controller.seats;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import net.vuega.vuega_backend.DTO.ResponseDto;
import net.vuega.vuega_backend.DTO.bookings.BulkBookingSummaryDTO;
import net.vuega.vuega_backend.DTO.seats.lock.BulkBookSeatsRequest;
import net.vuega.vuega_backend.Exception.InvalidStopRangeException;
import net.vuega.vuega_backend.Exception.SeatNotFoundException;
import net.vuega.vuega_backend.Service.seats.lock.SeatLockService;

/**
 * Endpoint for booking multiple seats in a single API call.
 *
 * Base path: /api/seats/bulk-book
 *
 * Supports two seat selection modes:
 *
 *   Mode A — Explicit list:
 *     {
 *       "partnerId": 1,
 *       "scheduleId": 10,
 *       "fromStopOrder": 0,
 *       "toStopOrder": 5,
 *       "seatIds": [1, 5, 6, 7]
 *     }
 *
 *   Mode B — Consecutive ID range (seats A through B inclusive):
 *     {
 *       "partnerId": 1,
 *       "scheduleId": 10,
 *       "fromStopOrder": 0,
 *       "toStopOrder": 5,
 *       "fromSeatId": 1,
 *       "toSeatId": 12
 *     }
 *
 * Optional fields (both modes):
 *   "idempotencyKeyPrefix" — when set, each seat's booking is submitted
 *   with the key "{prefix}:{seatId}", making the whole batch safely retriable.
 *
 * The response always returns HTTP 200 with a per-seat breakdown.
 * Clients must inspect BulkBookingSummaryDTO.results to see which
 * individual seats were booked and which failed (and why).
 */
@RestController
@RequestMapping("/api/seats/bulk-book")
@RequiredArgsConstructor
public class BulkSeatBookingController {

    private final SeatLockService service;

    /**
     * Books multiple seats for one partner in a single call.
     *
     * Always returns HTTP 200 with a summary of per-seat outcomes.
     * Individual seat failures (already booked, locked, not found)
     * are reported in the results list and do not cause a non-2xx status.
     *
     * HTTP 400 is returned only when the request itself is invalid
     * (e.g. validation errors, invalid stop range, empty seat-ID range).
     */
    @PostMapping
    public ResponseEntity<ResponseDto<BulkBookingSummaryDTO>> bookMultipleSeats(
            @Valid @RequestBody BulkBookSeatsRequest request) {
        try {
            BulkBookingSummaryDTO summary = service.bookMulti@@pleSeats(request);
            return ResponseEntity.ok(ResponseDto.success(summary));
        } catch (InvalidStopRangeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ResponseDto.error(400, e.getMessage()));
        } catch (SeatNotFoundException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ResponseDto.error(400,
                            "No seats found in the requested ID range."));
        }
    }
}

```


#### Short summary: 

empty definition using pc, found symbol in pc: _empty_/SeatLockService#bookMultipleSeats#
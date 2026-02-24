package net.vuega.vuega_backend.Controller.bookings;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import net.vuega.vuega_backend.DTO.ResponseDto;
import net.vuega.vuega_backend.DTO.seats.seat.bookings.BookingDTO;
import net.vuega.vuega_backend.Exception.BookingNotFoundException;
import net.vuega.vuega_backend.Exception.SeatLockConflictException;
import net.vuega.vuega_backend.Service.seats.lock.SeatLockService;

@RestController
@RequestMapping("/api/bookings")
@RequiredArgsConstructor
public class BookingController {

    private final SeatLockService service;

    // Cancel a booking — status set to CANCELLED, record retained
    @PostMapping("/{seatStatusId}/cancel")
    public ResponseEntity<ResponseDto<BookingDTO>> cancelBooking(
            @PathVariable Long seatStatusId,
            @RequestParam Long passengerId) {
        try {
            return ResponseEntity.ok(ResponseDto.success(service.cancelBooking(seatStatusId, passengerId)));
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
        return ResponseEntity.ok(ResponseDto.success(service.getBookingHistory(passengerId)));
    }
}

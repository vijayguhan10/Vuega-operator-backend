package net.vuega.vuega_backend.Controller.bookings.booking;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import net.vuega.vuega_backend.DTO.ResponseDto;
import net.vuega.vuega_backend.DTO.bookings.BookingDTO;
import net.vuega.vuega_backend.DTO.bookings.CreateBookingRequest;
import net.vuega.vuega_backend.Exception.BookingNotFoundException;
import net.vuega.vuega_backend.Model.bookings.BookingStatus;
import net.vuega.vuega_backend.Service.bookings.BookingService;

@RestController
@RequestMapping("/api/v2/bookings")
@RequiredArgsConstructor
public class NewBookingController {

    private final BookingService service;

    @PostMapping
    public ResponseEntity<ResponseDto<BookingDTO>> createBooking(
            @Valid @RequestBody CreateBookingRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ResponseDto.created(service.createBooking(request)));
    }

    @GetMapping("/{bookingId}")
    public ResponseEntity<ResponseDto<BookingDTO>> getBookingById(
            @PathVariable Long bookingId) {
        try {
            return ResponseEntity.ok(ResponseDto.success(service.getBookingById(bookingId)));
        } catch (BookingNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ResponseDto.notFound(e.getMessage()));
        }
    }

    @GetMapping("/pnr/{pnr}")
    public ResponseEntity<ResponseDto<BookingDTO>> getBookingByPnr(
            @PathVariable String pnr) {
        try {
            return ResponseEntity.ok(ResponseDto.success(service.getBookingByPnr(pnr)));
        } catch (BookingNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ResponseDto.notFound(e.getMessage()));
        }
    }

    @GetMapping("/partner/{partnerId}")
    public ResponseEntity<ResponseDto<List<BookingDTO>>> getBookingsByPartner(
            @PathVariable Long partnerId) {
        return ResponseEntity.ok(ResponseDto.success(service.getBookingsByPartner(partnerId)));
    }

    @PatchMapping("/{bookingId}/status")
    public ResponseEntity<ResponseDto<BookingDTO>> updateStatus(
            @PathVariable Long bookingId,
            @RequestParam BookingStatus status) {
        try {
            return ResponseEntity.ok(ResponseDto.success(service.updateStatus(bookingId, status)));
        } catch (BookingNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ResponseDto.notFound(e.getMessage()));
        }
    }
}

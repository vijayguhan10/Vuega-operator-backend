package net.vuega.vuega_backend.Controller.seats.lock;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import net.vuega.vuega_backend.DTO.ResponseDto;
import net.vuega.vuega_backend.DTO.seats.lock.AcquireLockRequest;
import net.vuega.vuega_backend.DTO.seats.lock.BookSeatRequest;
import net.vuega.vuega_backend.DTO.seats.lock.ReleaseLockRequest;
import net.vuega.vuega_backend.DTO.seats.lock.SeatLockDTO;
import net.vuega.vuega_backend.DTO.seats.seat.SeatDTO;
import net.vuega.vuega_backend.Exception.SeatLockConflictException;
import net.vuega.vuega_backend.Exception.SeatLockNotFoundException;
import net.vuega.vuega_backend.Exception.SeatNotAvailableException;
import net.vuega.vuega_backend.Exception.SeatNotFoundException;
import net.vuega.vuega_backend.Service.seats.lock.SeatLockService;

@RestController
@RequestMapping("/api/seats/{seatId}/lock")
@RequiredArgsConstructor
public class SeatLockController {

    private final SeatLockService service;

    // ─── GET ACTIVE LOCK ─────────────────────────────────────────────────────────

    @GetMapping
    public ResponseEntity<ResponseDto<SeatLockDTO>> getLock(@PathVariable Long seatId) {
        try {
            return ResponseEntity.ok(ResponseDto.success(service.getLockBySeat(seatId)));
        } catch (SeatLockNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ResponseDto.notFound(e.getMessage()));
        }
    }

    // ─── ACQUIRE LOCK ────────────────────────────────────────────────────────────

    @PostMapping
    public ResponseEntity<ResponseDto<SeatLockDTO>> acquireLock(
            @PathVariable Long seatId,
            @Valid @RequestBody AcquireLockRequest request) {
        try {
            return ResponseEntity.ok(ResponseDto.success(service.acquireLock(seatId, request)));
        } catch (SeatNotFoundException | SeatLockNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ResponseDto.notFound(e.getMessage()));
        } catch (SeatNotAvailableException e) {
            return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY)
                    .body(ResponseDto.error(422, e.getMessage()));
        } catch (SeatLockConflictException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(ResponseDto.error(409, e.getMessage()));
        }
    }

    // ─── RELEASE LOCK ────────────────────────────────────────────────────────────

    @DeleteMapping
    public ResponseEntity<ResponseDto<Void>> releaseLock(
            @PathVariable Long seatId,
            @Valid @RequestBody ReleaseLockRequest request) {
        try {
            service.releaseLock(seatId, request.getPartnerId());
            return ResponseEntity.ok(ResponseDto.success(null));
        } catch (SeatLockNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ResponseDto.notFound(e.getMessage()));
        }
    }

    // ─── BOOK SEAT ───────────────────────────────────────────────────────────────

    @PostMapping("/book")
    public ResponseEntity<ResponseDto<SeatDTO>> bookSeat(
            @PathVariable Long seatId,
            @Valid @RequestBody BookSeatRequest request) {
        try {
            return ResponseEntity.ok(ResponseDto.success(service.bookSeat(seatId, request.getPartnerId())));
        } catch (SeatNotFoundException | SeatLockNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ResponseDto.notFound(e.getMessage()));
        } catch (SeatNotAvailableException e) {
            return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY)
                    .body(ResponseDto.error(422, e.getMessage()));
        } catch (SeatLockConflictException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(ResponseDto.error(409, e.getMessage()));
        }
    }
}

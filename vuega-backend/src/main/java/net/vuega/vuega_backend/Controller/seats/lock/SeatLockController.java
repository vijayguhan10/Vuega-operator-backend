package net.vuega.vuega_backend.Controller.seats.lock;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
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
import net.vuega.vuega_backend.DTO.seats.lock.AcquireLockRequest;
import net.vuega.vuega_backend.DTO.seats.lock.SeatLockDTO;
import net.vuega.vuega_backend.DTO.seats.session.BookingSessionDTO;
import net.vuega.vuega_backend.exception.SeatLockConflictException;
import net.vuega.vuega_backend.exception.SeatLockNotFoundException;
import net.vuega.vuega_backend.Exception.SeatNotFoundException;
import net.vuega.vuega_backend.Exception.SessionExpiredException;
import net.vuega.vuega_backend.Exception.SessionNotFoundException;
import net.vuega.vuega_backend.Service.seats.lock.SeatLockService;

@RestController
@RequestMapping("/api/seats")
@RequiredArgsConstructor
public class SeatLockController {

    private final SeatLockService service;

    @GetMapping("/{seatId}/lock")
    public ResponseEntity<ResponseDto<SeatLockDTO>> getLock(
            @PathVariable Long seatId,
            @RequestParam Long scheduleId) {
        try {
            return ResponseEntity.ok(ResponseDto.success(service.getLockBySeat(seatId, scheduleId)));
        } catch (SeatLockNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ResponseDto.notFound(e.getMessage()));
        }
    }

    @PostMapping("/{seatId}/lock")
    public ResponseEntity<ResponseDto<SeatLockDTO>> acquireLock(
            @PathVariable Long seatId,
            @Valid @RequestBody AcquireLockRequest request) {
        try {
            return ResponseEntity.ok(ResponseDto.success(service.acquireLock(seatId, request)));
        } catch (SeatNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ResponseDto.notFound(e.getMessage()));
        } catch (SessionNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ResponseDto.notFound(e.getMessage()));
        } catch (SessionExpiredException e) {
            return ResponseEntity.status(HttpStatus.GONE)
                    .body(ResponseDto.error(410, e.getMessage()));
        } catch (SeatLockConflictException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(ResponseDto.error(409, e.getMessage()));
        }
    }

    @DeleteMapping("/{seatId}/lock")
    public ResponseEntity<ResponseDto<Void>> releaseLock(
            @PathVariable Long seatId,
            @RequestParam Long scheduleId) {
        try {
            service.releaseLock(seatId, scheduleId);
            return ResponseEntity.ok(ResponseDto.success(null));
        } catch (SeatLockNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ResponseDto.notFound(e.getMessage()));
        }
    }

    @GetMapping("/session/{sessionId}/locks")
    public ResponseEntity<ResponseDto<List<SeatLockDTO>>> getLocksBySession(
            @PathVariable Long sessionId) {
        return ResponseEntity.ok(ResponseDto.success(service.getLocksBySession(sessionId)));
    }

    @GetMapping("/session/{sessionId}")
    public ResponseEntity<ResponseDto<BookingSessionDTO>> getSession(
            @PathVariable Long sessionId) {
        try {
            return ResponseEntity.ok(ResponseDto.success(service.getSession(sessionId)));
        } catch (SessionNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ResponseDto.notFound(e.getMessage()));
        }
    }
}
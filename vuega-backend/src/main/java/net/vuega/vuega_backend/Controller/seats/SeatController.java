package net.vuega.vuega_backend.Controller.seats;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import net.vuega.vuega_backend.DTO.ResponseDto;
import net.vuega.vuega_backend.DTO.seats.CreateSeatRequest;
import net.vuega.vuega_backend.DTO.seats.CreateSeatsInBatchRequest;
import net.vuega.vuega_backend.DTO.seats.LockSeatRequest;
import net.vuega.vuega_backend.DTO.seats.SeatDTO;
import net.vuega.vuega_backend.DTO.seats.UpdateSeatRequest;
import net.vuega.vuega_backend.Exception.DuplicateSeatException;
import net.vuega.vuega_backend.Exception.InvalidStopRangeException;
import net.vuega.vuega_backend.Exception.SeatLockConflictException;
import net.vuega.vuega_backend.Exception.SeatNotAvailableException;
import net.vuega.vuega_backend.Exception.SeatNotFoundException;
import net.vuega.vuega_backend.Service.seats.SeatService;
import jakarta.validation.*;
/**
 * REST controller for the Seats resource.
 *
 * Endpoints:
 *
 * CRUD
 * POST /api/seats – create one seat
 * POST /api/seats/batch – batch-create seats
 * GET /api/seats/{id} – get by id
 * GET /api/seats/bus/{busId} – all seats for a bus
 * GET /api/seats/bus/{busId}/available – available seats for a bus
 * PUT /api/seats/{id} – partial update (seatNo, type, price)
 * DELETE /api/seats/{id} – hard-delete (only AVAILABLE)
 *
 * Locking
 * POST /api/seats/{id}/lock – lock seat (body: lockedBy, fromStop, toStop)
 * DELETE /api/seats/{id}/lock – unlock seat (?lockedBy=…)
 *
 * Booking
 * POST /api/seats/{id}/book – confirm booking (?lockedBy=…)
 * POST /api/seats/{id}/cancel – cancel booking → AVAILABLE
 */
@RestController
@RequestMapping("/api/seats")
@RequiredArgsConstructor
public class SeatController {

    private final SeatService service;

    // ======================== CRUD ========================

    /**
     * POST /api/seats
     * Body: { busId, seatNo, type, price }
     * 201 Created | 409 Conflict (duplicate) | 400 Bad Request (validation)
     */
    @PostMapping
    public ResponseEntity<ResponseDto<SeatDTO>> create(
            @Valid @RequestBody CreateSeatRequest request) {
        try {
            SeatDTO dto = service.createSeat(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(ResponseDto.created(dto));
        } catch (DuplicateSeatException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(ResponseDto.error(409, e.getMessage()));
        }
    }

    /**
     * POST /api/seats/batch
     * Body: { seats: [ {busId, seatNo, type, price}, … ] }
     * 201 Created | 409 Conflict (any duplicate) | 400 Bad Request
     */
    @PostMapping("/batch")
    public ResponseEntity<ResponseDto<List<SeatDTO>>> createBatch(
            @Valid @RequestBody CreateSeatsInBatchRequest request) {
        try {
            List<SeatDTO> dtos = service.createSeatsInBatch(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(ResponseDto.created(dtos));
        } catch (DuplicateSeatException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(ResponseDto.error(409, e.getMessage()));
        }
    }

    /**
     * GET /api/seats/{id}
     * 200 OK | 404 Not Found
     */
    @GetMapping("/{id}")
    public ResponseEntity<ResponseDto<SeatDTO>> getById(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(ResponseDto.success(service.getSeatById(id)));
        } catch (SeatNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ResponseDto.notFound(e.getMessage()));
        }
    }

    /**
     * GET /api/seats/bus/{busId}
     * Returns all seats (any status) for a bus, enriched with bus details.
     * 200 OK
     */
    @GetMapping("/bus/{busId}")
    public ResponseEntity<ResponseDto<List<SeatDTO>>> getByBus(@PathVariable Long busId) {
        return ResponseEntity.ok(ResponseDto.success(service.getSeatsByBus(busId)));
    }

    /**
     * GET /api/seats/bus/{busId}/available
     * Returns only AVAILABLE seats for a bus (fast — no Control Plane enrichment).
     * 200 OK
     */
    @GetMapping("/bus/{busId}/available")
    public ResponseEntity<ResponseDto<List<SeatDTO>>> getAvailable(@PathVariable Long busId) {
        return ResponseEntity.ok(ResponseDto.success(service.getAvailableSeats(busId)));
    }

    /**
     * PUT /api/seats/{id}
     * Body: { seatNo?, type?, price? } (all fields optional — null = keep current)
     * 200 OK | 404 | 409 (seat in use or duplicate seatNo)
     */
    @PutMapping("/{id}")
    public ResponseEntity<ResponseDto<SeatDTO>> update(
            @PathVariable Long id,
            @Valid @RequestBody UpdateSeatRequest request) {
        try {
            return ResponseEntity.ok(ResponseDto.success(service.updateSeat(id, request)));
        } catch (SeatNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ResponseDto.notFound(e.getMessage()));
        } catch (SeatNotAvailableException | DuplicateSeatException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(ResponseDto.error(409, e.getMessage()));
        }
    }

    /**
     * DELETE /api/seats/{id}
     * 200 OK | 404 | 409 (seat in use)
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<ResponseDto<Void>> delete(@PathVariable Long id) {
        try {
            service.deleteSeat(id);
            return ResponseEntity.ok(ResponseDto.success(null));
        } catch (SeatNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ResponseDto.notFound(e.getMessage()));
        } catch (SeatNotAvailableException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(ResponseDto.error(409, e.getMessage()));
        }
    }

    // ======================== LOCKING ========================

    /**
     * POST /api/seats/{id}/lock
     * Body: { lockedBy, fromStopOrder, toStopOrder }
     *
     * Acquires a pessimistic DB write-lock on the seat row so that two
     * concurrent requests cannot both see AVAILABLE and both succeed.
     *
     * 200 OK | 404 | 400 (invalid stop range) | 409 (locked) | 422 (booked)
     */
    @PostMapping("/{id}/lock")
    public ResponseEntity<ResponseDto<SeatDTO>> lock(
            @PathVariable Long id,
            @Valid @RequestBody LockSeatRequest request) {
        try {
            return ResponseEntity.ok(ResponseDto.success(service.lockSeat(id, request)));
        } catch (SeatNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ResponseDto.notFound(e.getMessage()));
        } catch (InvalidStopRangeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ResponseDto.error(400, e.getMessage()));
        } catch (SeatNotAvailableException e) {
            return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY)
                    .body(ResponseDto.error(422, e.getMessage()));
        } catch (SeatLockConflictException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(ResponseDto.error(409, e.getMessage()));
        }
    }

    /**
     * DELETE /api/seats/{id}/lock?lockedBy={sessionId}
     * Only the session that owns the lock can release it.
     *
     * 200 OK | 404 | 422 (not locked) | 403 (wrong owner)
     */
    @DeleteMapping("/{id}/lock")
    public ResponseEntity<ResponseDto<SeatDTO>> unlock(
            @PathVariable Long id,
            @RequestParam String lockedBy) {
        try {
            return ResponseEntity.ok(ResponseDto.success(service.unlockSeat(id, lockedBy)));
        } catch (SeatNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ResponseDto.notFound(e.getMessage()));
        } catch (SeatNotAvailableException e) {
            return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY)
                    .body(ResponseDto.error(422, e.getMessage()));
        } catch (SeatLockConflictException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(ResponseDto.error(403, e.getMessage()));
        }
    }

    // ======================== BOOKING ========================

    /**
     * POST /api/seats/{id}/book?lockedBy={sessionId}
     * Transitions a LOCKED seat to BOOKED.
     * The @Version column provides optimistic-locking safety as a last defence.
     *
     * 200 OK | 404 | 409 (concurrent write / wrong owner) | 422 (wrong state /
     * expired lock)
     */
    @PostMapping("/{id}/book")
    public ResponseEntity<ResponseDto<SeatDTO>> book(
            @PathVariable Long id,
            @RequestParam String lockedBy) {
        try {
            return ResponseEntity.ok(ResponseDto.success(service.bookSeat(id, lockedBy)));
        } catch (SeatNotFoundException e) {
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

    /**
     * POST /api/seats/{id}/cancel
     * Cancels a confirmed booking — returns the seat to AVAILABLE.
     *
     * 200 OK | 404 | 422 (not booked)
     */
    @PostMapping("/{id}/cancel")
    public ResponseEntity<ResponseDto<SeatDTO>> cancel(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(ResponseDto.success(service.cancelBooking(id)));
        } catch (SeatNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ResponseDto.notFound(e.getMessage()));
        } catch (SeatNotAvailableException e) {
            return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY)
                    .body(ResponseDto.error(422, e.getMessage()));
        }
    }
}
//
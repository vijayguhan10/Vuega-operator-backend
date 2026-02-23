error id: file:///C:/Projects/Vuega-backend/vuega-backend/src/main/java/net/vuega/vuega_backend/Controller/seats/seat/SeatController.java:net/vuega/vuega_backend/DTO/seats/seat/SeatDTO#
file:///C:/Projects/Vuega-backend/vuega-backend/src/main/java/net/vuega/vuega_backend/Controller/seats/seat/SeatController.java
empty definition using pc, found symbol in pc: net/vuega/vuega_backend/DTO/seats/seat/SeatDTO#
empty definition using semanticdb
empty definition using fallback
non-local guesses:

offset: 964
uri: file:///C:/Projects/Vuega-backend/vuega-backend/src/main/java/net/vuega/vuega_backend/Controller/seats/seat/SeatController.java
text:
```scala
package net.vuega.vuega_backend.Controller.seats.seat;

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
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import net.vuega.vuega_backend.DTO.ResponseDto;
import net.vuega.vuega_backend.DTO.seats.seat.CreateSeatRequest;
import net.vuega.vuega_backend.DTO.seats.seat.CreateSeatsInBatchRequest;
import net.vuega.vuega_backend.DTO.seats.seat.@@SeatDTO;
import net.vuega.vuega_backend.DTO.seats.seat.UpdateSeatRequest;
import net.vuega.vuega_backend.Exception.DuplicateSeatException;
import net.vuega.vuega_backend.Exception.InvalidStopRangeException;
import net.vuega.vuega_backend.Exception.SeatNotAvailableException;
import net.vuega.vuega_backend.Exception.SeatNotFoundException;
import net.vuega.vuega_backend.Service.seats.seat.SeatService;

// REST controller for seat CRUD and availability toggling.
@RestController
@RequestMapping("/api/seats")
@RequiredArgsConstructor
public class SeatController {

    private final SeatService service;

    // ─── CREATE ─────────────────────────────────────────────────────────────────

    @PostMapping
    public ResponseEntity<ResponseDto<SeatDTO>> create(
            @Valid @RequestBody CreateSeatRequest request) {
        try {
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(ResponseDto.created(service.createSeat(request)));
        } catch (InvalidStopRangeException e) {
            return ResponseEntity.badRequest().body(ResponseDto.error(400, e.getMessage()));
        } catch (DuplicateSeatException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(ResponseDto.error(409, e.getMessage()));
        }
    }

    @PostMapping("/batch")
    public ResponseEntity<ResponseDto<List<SeatDTO>>> createBatch(
            @Valid @RequestBody CreateSeatsInBatchRequest request) {
        try {
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(ResponseDto.created(service.createSeatsInBatch(request)));
        } catch (InvalidStopRangeException e) {
            return ResponseEntity.badRequest().body(ResponseDto.error(400, e.getMessage()));
        } catch (DuplicateSeatException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(ResponseDto.error(409, e.getMessage()));
        }
    }

    // ─── READ ────────────────────────────────────────────────────────────────────

    @GetMapping("/{id}")
    public ResponseEntity<ResponseDto<SeatDTO>> getById(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(ResponseDto.success(service.getSeatById(id)));
        } catch (SeatNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ResponseDto.notFound(e.getMessage()));
        }
    }

    @GetMapping("/bus/{busId}")
    public ResponseEntity<ResponseDto<List<SeatDTO>>> getByBus(@PathVariable Long busId) {
        return ResponseEntity.ok(ResponseDto.success(service.getSeatsByBus(busId)));
    }

    @GetMapping("/bus/{busId}/available")
    public ResponseEntity<ResponseDto<List<SeatDTO>>> getAvailable(@PathVariable Long busId) {
        return ResponseEntity.ok(ResponseDto.success(service.getAvailableSeats(busId)));
    }

    // ─── UPDATE ──────────────────────────────────────────────────────────────────

    @PutMapping("/{id}")
    public ResponseEntity<ResponseDto<SeatDTO>> update(
            @PathVariable Long id,
            @Valid @RequestBody UpdateSeatRequest request) {
        try {
            return ResponseEntity.ok(ResponseDto.success(service.updateSeat(id, request)));
        } catch (SeatNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ResponseDto.notFound(e.getMessage()));
        } catch (DuplicateSeatException | SeatNotAvailableException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(ResponseDto.error(409, e.getMessage()));
        }
    }

    // ─── TOGGLE AVAILABILITY ──────────────────────────────────────────────────

    @DeleteMapping("/{id}")
    public ResponseEntity<ResponseDto<SeatDTO>> toggle(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(ResponseDto.success(service.toggleAvailability(id)));
        } catch (SeatNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ResponseDto.notFound(e.getMessage()));
        }
    }

    // ─── CANCEL BOOKING ──────────────────────────────────────────────────────────

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

```


#### Short summary: 

empty definition using pc, found symbol in pc: net/vuega/vuega_backend/DTO/seats/seat/SeatDTO#
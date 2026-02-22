error id: file:///C:/Projects/Vuega-backend/vuega-backend/src/main/java/net/vuega/vuega_backend/Controller/seats/SeatController.java:net/vuega/vuega_backend/Service/seats/SeatService#
file:///C:/Projects/Vuega-backend/vuega-backend/src/main/java/net/vuega/vuega_backend/Controller/seats/SeatController.java
empty definition using pc, found symbol in pc: net/vuega/vuega_backend/Service/seats/SeatService#
empty definition using semanticdb
empty definition using fallback
non-local guesses:

offset: 1510
uri: file:///C:/Projects/Vuega-backend/vuega-backend/src/main/java/net/vuega/vuega_backend/Controller/seats/SeatController.java
text:
```scala
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

import jakarta.validation.Valid;
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
import net.vuega.vuega_backend.Service.seats.@@SeatService;

// Seat REST controller — CRUD, locking, and booking.
@RestController
@RequestMapping("/api/seats")
@RequiredArgsConstructor
public class SeatController {

    private final SeatService service;

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

empty definition using pc, found symbol in pc: net/vuega/vuega_backend/Service/seats/SeatService#
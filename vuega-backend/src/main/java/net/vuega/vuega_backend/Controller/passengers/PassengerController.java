package net.vuega.vuega_backend.Controller.passengers;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import net.vuega.vuega_backend.DTO.ResponseDto;
import net.vuega.vuega_backend.DTO.passengers.CreatePassengerRequest;
import net.vuega.vuega_backend.DTO.passengers.PassengerDTO;
import net.vuega.vuega_backend.Exception.PassengerNotFoundException;
import net.vuega.vuega_backend.Service.passengers.PassengerService;

@RestController
@RequestMapping("/api/v2/passengers")
@RequiredArgsConstructor
public class PassengerController {

    private final PassengerService service;

    @PostMapping
    public ResponseEntity<ResponseDto<PassengerDTO>> addPassenger(
            @Valid @RequestBody CreatePassengerRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ResponseDto.created(service.addPassenger(request)));
    }

    @GetMapping("/{passengerId}")
    public ResponseEntity<ResponseDto<PassengerDTO>> getPassengerById(
            @PathVariable Long passengerId) {
        try {
            return ResponseEntity.ok(ResponseDto.success(service.getPassengerById(passengerId)));
        } catch (PassengerNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ResponseDto.notFound(e.getMessage()));
        }
    }

    @GetMapping("/booking/{bookingId}")
    public ResponseEntity<ResponseDto<List<PassengerDTO>>> getPassengersByBooking(
            @PathVariable Long bookingId) {
        return ResponseEntity.ok(ResponseDto.success(service.getPassengersByBooking(bookingId)));
    }
}

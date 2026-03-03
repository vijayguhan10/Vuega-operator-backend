error id: file:///C:/Projects/Vuega-backend/vuega-backend/src/main/java/net/vuega/vuega_backend/Service/passengers/PassengerService.java:_empty_/CreatePassengerRequest#
file:///C:/Projects/Vuega-backend/vuega-backend/src/main/java/net/vuega/vuega_backend/Service/passengers/PassengerService.java
empty definition using pc, found symbol in pc: _empty_/CreatePassengerRequest#
empty definition using semanticdb
empty definition using fallback
non-local guesses:

offset: 1003
uri: file:///C:/Projects/Vuega-backend/vuega-backend/src/main/java/net/vuega/vuega_backend/Service/passengers/PassengerService.java
text:
```scala
package net.vuega.vuega_backend.Service.passengers;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import net.vuega.vuega_backend.DTO.passengers.CreatePassengerRequest;
import net.vuega.vuega_backend.DTO.passengers.PassengerDTO;
import net.vuega.vuega_backend.Exception.BookingNotFoundException;
import net.vuega.vuega_backend.Exception.PassengerNotFoundException;
import net.vuega.vuega_backend.Model.bookings.Booking;
import net.vuega.vuega_backend.Model.passengers.Passenger;
import net.vuega.vuega_backend.Repository.bookings.BookingRepository;
import net.vuega.vuega_backend.Repository.passengers.PassengerRepository;

@Service
@RequiredArgsConstructor
public class PassengerService {

    private final PassengerRepository repository;
    private final BookingRepository bookingRepository;

    @Transactional
    public PassengerDTO addPassenger(CreatePassengerReque@@st request) {
        Booking booking = bookingRepository.findById(request.getBookingId())
                .orElseThrow(() -> new BookingNotFoundException(request.getBookingId()));

        Passenger passenger = Passenger.builder()
                .booking(booking)
                .name(request.getName())
                .age(request.getAge())
                .gender(request.getGender())
                .build();

        return toDTO(repository.save(passenger));
    }

    @Transactional(readOnly = true)
    public PassengerDTO getPassengerById(Long passengerId) {
        return toDTO(repository.findById(passengerId)
                .orElseThrow(() -> new PassengerNotFoundException(passengerId)));
    }

    @Transactional(readOnly = true)
    public List<PassengerDTO> getPassengersByBooking(Long bookingId) {
        return repository.findByBookingBookingId(bookingId).stream()
                .map(this::toDTO)
                .toList();
    }

    private PassengerDTO toDTO(Passenger passenger) {
        return PassengerDTO.builder()
                .passengerId(passenger.getPassengerId())
                .bookingId(passenger.getBooking().getBookingId())
                .name(passenger.getName())
                .age(passenger.getAge())
                .gender(passenger.getGender())
                .build();
    }
}

```


#### Short summary: 

empty definition using pc, found symbol in pc: _empty_/CreatePassengerRequest#
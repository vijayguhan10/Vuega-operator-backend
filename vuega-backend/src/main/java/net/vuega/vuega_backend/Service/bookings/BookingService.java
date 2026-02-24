package net.vuega.vuega_backend.Service.bookings;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import net.vuega.vuega_backend.DTO.bookings.BookingDTO;
import net.vuega.vuega_backend.DTO.bookings.CreateBookingRequest;
import net.vuega.vuega_backend.Exception.BookingNotFoundException;
import net.vuega.vuega_backend.Model.bookings.Booking;
import net.vuega.vuega_backend.Model.bookings.BookingStatus;
import net.vuega.vuega_backend.Repository.bookings.BookingRepository;

@Service
@RequiredArgsConstructor
public class BookingService {

    private final BookingRepository repository;

    @Transactional
    public BookingDTO createBooking(CreateBookingRequest request) {
        Booking booking = Booking.builder()
                .pnr(request.getPnr())
                .partnerId(request.getPartnerId())
                .scheduleId(request.getScheduleId())
                .status(request.getStatus())
                .totalAmount(request.getTotalAmount())
                .build();

        return toDTO(repository.save(booking));
    }

    @Transactional(readOnly = true)
    public BookingDTO getBookingById(Long bookingId) {
        return toDTO(repository.findById(bookingId)
                .orElseThrow(() -> new BookingNotFoundException(bookingId)));
    }

    @Transactional(readOnly = true)
    public BookingDTO getBookingByPnr(String pnr) {
        return toDTO(repository.findByPnr(pnr)
                .orElseThrow(() -> new BookingNotFoundException("Booking not found for PNR: " + pnr)));
    }

    @Transactional(readOnly = true)
    public List<BookingDTO> getBookingsByPartner(Long partnerId) {
        return repository.findByPartnerId(partnerId).stream()
                .map(this::toDTO)
                .toList();
    }

    @Transactional
    public BookingDTO updateStatus(Long bookingId, BookingStatus status) {
        Booking booking = repository.findById(bookingId)
                .orElseThrow(() -> new BookingNotFoundException(bookingId));
        booking.setStatus(status);
        return toDTO(repository.save(booking));
    }

    private BookingDTO toDTO(Booking booking) {
        return BookingDTO.builder()
                .bookingId(booking.getBookingId())
                .pnr(booking.getPnr())
                .partnerId(booking.getPartnerId())
                .scheduleId(booking.getScheduleId())
                .status(booking.getStatus())
                .totalAmount(booking.getTotalAmount())
                .createdAt(booking.getCreatedAt())
                .build();
    }
}

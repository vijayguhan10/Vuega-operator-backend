package net.vuega.vuega_backend.DTO.seats.seat;

// Request DTO for bulk seat creation (max 100 seats per batch).
@Data

@NotEmpty(message="seats list must not be empty")@Size(max=100,message="Cannot create more than 100 seats in a single batch")@Valid private List<CreateSeatRequest>seats;}

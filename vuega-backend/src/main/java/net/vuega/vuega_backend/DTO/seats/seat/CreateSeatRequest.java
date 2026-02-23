package net.vuega.vuega_backend.DTO.seats.seat;

// Request DTO for creating a single seat.
@Data

@NotNull(message="busId is required")private Long busId;

@NotBlank(message="seatNo is required")@Size(max=10,message="seatNo must be at most 10 characters")private String seatNo;

@NotNull(message="type is required (SEATER or SLEEPER)")private SeatType type;

@NotNull(message="price is required")@DecimalMin(value="0.01",message="price must be greater than 0")private BigDecimal price;

@NotNull(message="fromStopOrder is required")@Min(value=0,message="fromStopOrder must be >= 0")private Integer fromStopOrder;

@NotNull(message="toStopOrder is required")@Min(value=1,message="toStopOrder must be >= 1")private Integer toStopOrder;}

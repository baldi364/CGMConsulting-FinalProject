package it.cgmconsulting.baldi.payload.request;

import jakarta.validation.constraints.Min;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class RentalRequest
{
    @Min(1)
    private Long filmId;

    @Min(1)
    private Long storeId;

    @Min(1)
    private Long customerId;

    private LocalDateTime returnRentalDate;
}


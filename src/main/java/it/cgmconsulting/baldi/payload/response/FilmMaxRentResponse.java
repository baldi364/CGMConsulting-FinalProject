package it.cgmconsulting.baldi.payload.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class FilmMaxRentResponse
{
    private long filmId;
    private String title;
    private long totalRents;
}

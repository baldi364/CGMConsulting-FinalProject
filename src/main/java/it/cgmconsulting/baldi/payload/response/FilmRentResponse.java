package it.cgmconsulting.baldi.payload.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class FilmRentResponse
{
    private long filmId;
    private String title;
    private String storeName;

    public FilmRentResponse(long filmId, String title, String storeName)
    {
        this.filmId = filmId;
        this.title = title;
        this.storeName = storeName;
    }
}

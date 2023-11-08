package it.cgmconsulting.baldi.payload.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;

@Getter
public class FilmRequest
{

    @NotBlank
    @Size(min = 1, max = 100)
    private String title;

    @NotBlank @Size(min = 10, max = 255)
    private String description;

    @Min(1850)
    private short releaseYear;

    @Min(1)
    private long languageId;

    @Min(1)
    private long genreId;
}

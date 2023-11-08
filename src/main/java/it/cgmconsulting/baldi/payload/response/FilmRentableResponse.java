package it.cgmconsulting.baldi.payload.response;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class FilmRentableResponse
{
    private String title;
    private String storeName;
    private long storeCopies;
    private long copiesAvailable;

    public FilmRentableResponse(String title, String storeName, long storeCopies)
    {
        this.title = title;
        this.storeName = storeName;
        this.storeCopies = storeCopies;
        this.copiesAvailable = storeCopies; //per far risultare le copie di film disponibili
    }
}

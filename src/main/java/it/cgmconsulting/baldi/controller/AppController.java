package it.cgmconsulting.baldi.controller;

import it.cgmconsulting.baldi.payload.request.FilmRequest;
import it.cgmconsulting.baldi.payload.request.RentalRequest;
import it.cgmconsulting.baldi.service.FilmService;
import it.cgmconsulting.baldi.service.RentalService;
import it.cgmconsulting.baldi.service.StoreService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.Set;

@RestController
@RequiredArgsConstructor
@Validated
public class AppController
{
    private final FilmService filmService;
    private final StoreService storeService;
    private final RentalService rentalService;


    //       --- PRIMA RICHIESTA ---

    //Nei parametri inserisco l'annotation @Valid per assicurarmi che vengano rispettati i valori delle request
    //Ovviamente nel controller ci vorrà l'annotation @Validated
    @PutMapping("/update-film/{filmId}")
    public ResponseEntity<?> updateFilm (@RequestBody @Valid FilmRequest filmRequest,
                                         @PathVariable @Min(1) long filmId)
    {
        return filmService.updateFilm(filmRequest, filmId);
    }

    //        --- SECONDA RICHIESTA --

    @GetMapping("/find-films-by-language/{languageId}")
    public ResponseEntity<?> getFilmByLanguage(@PathVariable @Min(1) long languageId)
    {
        return filmService.getFilmByLanguage(languageId);
    }

    //        --- TERZA RICHIESTA ---

    @PostMapping("/add-film-to-store/{storeId}/{filmId}")
    public ResponseEntity<?> addFilmToStore(@PathVariable @Min(1) long storeId,
                                            @PathVariable @Min(1) long filmId)
    {
        return filmService.addFilmToStore(storeId, filmId);
    }

    //        --- QUARTA RICHIESTA ---

    @GetMapping("/count-customers-by-store/{storeName}")
    public ResponseEntity<?> getCountCustomers(@PathVariable String storeName)
    {
        return storeService.getCountCustomers(storeName);
    }

    //      --- QUINTA RICHIESTA ---

    @PutMapping("/add-update-rental")
    public ResponseEntity<?> addOrUpdateRental(@RequestBody @Valid RentalRequest rentalRequest)
    {
        return rentalService.addOrUpdateRental(rentalRequest);
    }

    //      --- SESTA RICHIESTA ---

    @GetMapping("/count-rentals-in-date-range-by-store/{storeId}")
    public ResponseEntity <?> getCountRentalsInDataRange(@RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate start,
                                                         @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate end,
                                                         @PathVariable @Min(1) long storeId)
    {
        return rentalService.getCountRentalsInDataRange(storeId, start, end);
    }

    //      --- SETTIMA RICHIESTA ---

    @GetMapping("/find-all-films-rent-by-one-customer/{customerId}")
    public ResponseEntity<?> getAllFilmsRentByCustomer(@PathVariable @Min(1) long customerId)
    {
        return filmService.getAllFilmsRentByCustomer(customerId);
    }

    //      --- OTTAVA RICHIESTA ---

    @GetMapping("/find-film-with-max-number-of-rent")
    public ResponseEntity<?> getMaxFilmRented()
    {
        return filmService.getMaxFilmRented();
    }

    //      --- NONA RICHIESTA ---

    @GetMapping("/find-films-by-actors")
    public ResponseEntity<?> getActorsFilms(@RequestParam Set<Long> actorsId)
    {
        return filmService.getActorsList(actorsId);
    }

    //      --- DECIMA RICHIESTA ---

    @GetMapping("/find-rentable-films")
    public ResponseEntity<?> getRentableFilms(@RequestParam @NotBlank String title)
    {
        return filmService.getRentableFilms(title);
    }


}

package it.cgmconsulting.baldi.service;

import it.cgmconsulting.baldi.entity.*;
import it.cgmconsulting.baldi.exception.ResourceNotFoundException;
import it.cgmconsulting.baldi.payload.request.FilmRequest;
import it.cgmconsulting.baldi.payload.response.FilmMaxRentResponse;
import it.cgmconsulting.baldi.payload.response.FilmRentResponse;
import it.cgmconsulting.baldi.payload.response.FilmRentableResponse;
import it.cgmconsulting.baldi.payload.response.FilmResponse;
import it.cgmconsulting.baldi.repository.*;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class FilmService
{
    private final FilmRepository filmRepository;
    private final LanguageRepository languageRepository;
    private final GenreRepository genreRepository;
    private final StoreRepository storeRepository;
    private final InventoryRepository inventoryRepository;
    private final CustomerRepository customerRepository;

    @Transactional
    public ResponseEntity<?> updateFilm(FilmRequest filmRequest, long filmId)
    {
        //Prima trovo film, genere e linguaggio e controllo se esistono
        Film film = filmRepository.findById(filmId)
                .orElseThrow(() -> new ResourceNotFoundException("Film", "id", filmId));

        Genre genre = genreRepository.findById(filmRequest.getGenreId())
                .orElseThrow(() -> new ResourceNotFoundException("Genre", "id", filmRequest.getGenreId()));

        Language language = languageRepository.findById(filmRequest.getLanguageId())
                .orElseThrow(() -> new ResourceNotFoundException("Language", "id", filmRequest.getLanguageId()));

        //Se esistono setto tutti i valori da aggiornare
        film.setTitle(filmRequest.getTitle());
        film.setDescription(filmRequest.getDescription());
        film.setReleaseYear(film.getReleaseYear());
        film.setGenreId(genre);
        film.setLanguageId(language);

        return new ResponseEntity<>("Movie with title \"" + filmRequest.getTitle() + "\" has been updated", HttpStatus.OK);
    }

    public ResponseEntity<?> getFilmByLanguage(long languageId)
    {
        //Ricavo la lista
        List<FilmResponse> list = filmRepository.getFilmByLanguage(languageId);

        //Piccolo controllo in caso non esista film in quella lingua
        if(list.isEmpty())
        {
            return new ResponseEntity("There are no movie with id language " + languageId, HttpStatus.NOT_FOUND);
        }

        return new ResponseEntity<>(list, HttpStatus.OK);
    }

    public ResponseEntity<?> addFilmToStore(long storeId, long filmId)
    {
        //Ricavo film e store tramite un findById
        Film film = filmRepository.findById(filmId)
                .orElseThrow(() -> new ResourceNotFoundException("Film", "id", filmId));

        Store store = storeRepository.findById(storeId)
                .orElseThrow(() -> new ResourceNotFoundException("Store", "id", storeId));

        //Creo l'inventory che contiene il film e il suo store
        Inventory inventory = new Inventory(store, film);
        inventoryRepository.save(inventory);

        return new ResponseEntity<>("Movie \"" + film.getTitle() + "\" has been added to store " + store.getStoreName(), HttpStatus.OK);
    }

    public ResponseEntity<?> getAllFilmsRentByCustomer(long customerId)
    {
        //Controllo l'esistenza del cliente
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new ResourceNotFoundException("Customer", "id", customerId));
        List<FilmRentResponse> listFilm = filmRepository.getAllRentalByCustomer(customerId);
        return new ResponseEntity<>(listFilm, HttpStatus.OK);
    }


    public ResponseEntity<?> getMaxFilmRented()
    {
        //Qui avrò una lista ordinata in discendente dei film con pù noleggi
        List<FilmMaxRentResponse> listMaxRents = filmRepository.getMaxRentals();

        //Creo un'arraylist nuovo per gestire eventuali film con più noleggi
        List<FilmMaxRentResponse> finalListMaxRents = new ArrayList<>();

        //Aggiungo a questa lista il film con più noleggi ottenuto dalla query precedente
        finalListMaxRents.add(listMaxRents.get(0));

        //Ciclo la lista per controllare se ci sono film con più noleggi ed eventualmente aggiungerli
        //Parto da 1 perchè l'indice 0 è già presente
        for (int i = 1; i < listMaxRents.size(); i++)
        {
            FilmMaxRentResponse currentFilm = listMaxRents.get(i);
            if (currentFilm.getTotalRents() == finalListMaxRents.get(0).getTotalRents())
            {
                finalListMaxRents.add(currentFilm);
            }
            else
            {
                //Se trovo un film con un numero di noleggi diverso esco dal loop
                //La lista è ordinata in oridne decrescente e non ho bisogno di controllare anche i rent successivi
                break;
            }
        }
        return new ResponseEntity(finalListMaxRents, HttpStatus.OK);
    }

    public ResponseEntity<?> getRentableFilms(String title)
    {
        Film f = filmRepository.findByTitle(title);
        //Controllo che il film esista
        if(f == null)
        {
            return new ResponseEntity<>("Film with that name doesn't exists", HttpStatus.BAD_REQUEST);
        }

        //In questa lista ho il totale delle copie possedute dal negozio
        List<FilmRentableResponse> listCopiesInStore = filmRepository.findFilmInStore(title);

        //Utilizzo un for each per ciclare la lista ed eseguire le operazioni di sottrazione
        for (FilmRentableResponse films : listCopiesInStore)
        {
            //Con questa lista mi ricavo i film noleggiati a cui passo il titolo e il nome dello store in modo da non cancellare le copie disponibili
            //di altri negozi il cui film è stato restituito
            List<FilmRentableResponse> rentedCopies = filmRepository.getCopiesRented(films.getTitle(), films.getStoreName());

            //Se la lista non è vuota eseguo la sottrazione
            if(!rentedCopies.isEmpty())
            {
                long totalCopies = films.getStoreCopies(); //totale delle copie in possesso dal negozio
                long rentedCopiesCount = rentedCopies.get(0).getCopiesAvailable(); //totale delle copie disponibili
                long copiesAvailable = totalCopies - rentedCopiesCount; //sottraggo per ottenere il totale delle copie disponibili
                films.setCopiesAvailable(copiesAvailable); //setto il tutto
            }
        }
        return new ResponseEntity<>(listCopiesInStore, HttpStatus.OK);
    }

    public ResponseEntity<?> getActorsList(Set<Long> actorsId)
    {
        List<FilmResponse> listActors = filmRepository.findActorsInFilms(actorsId, actorsId.size());
        if(listActors.isEmpty())
        {
            return new ResponseEntity<>("These actors are not in the same film", HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(listActors, HttpStatus.OK);
    }
}

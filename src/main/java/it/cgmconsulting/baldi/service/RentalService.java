package it.cgmconsulting.baldi.service;

import it.cgmconsulting.baldi.entity.*;
import it.cgmconsulting.baldi.exception.ResourceNotFoundException;
import it.cgmconsulting.baldi.payload.request.RentalRequest;
import it.cgmconsulting.baldi.payload.response.FilmRentableResponse;
import it.cgmconsulting.baldi.repository.*;
import jakarta.servlet.http.HttpSession;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RentalService
{
    private final RentalRepository rentalRepository;
    private final StoreRepository storeRepository;
    private final FilmRepository filmRepository;
    private final CustomerRepository customerRepository;
    private final InventoryRepository inventoryRepository;

    public ResponseEntity<?> getCountRentalsInDataRange(long storeId, LocalDate start, LocalDate end)
    {
        Store store = storeRepository.findById(storeId)
                .orElseThrow(() -> new ResourceNotFoundException("Store", "id", storeId));
        long countRentals = rentalRepository.countRentalsInDataRange(storeId, start, end);

        return new ResponseEntity<>("There are a total of " + countRentals + " rentals in the store \"" + store.getStoreName() + "\"" , HttpStatus.OK);
    }

    @Transactional
    public ResponseEntity<?> addOrUpdateRental(RentalRequest rentalRequest)
    {
        //Ricavo gli ID di Film, Customer e Store
        Film f = filmRepository.findById(rentalRequest.getFilmId())
                .orElseThrow(() -> new ResourceNotFoundException("Film", "id", rentalRequest.getFilmId()));

        Customer c = customerRepository.findById(rentalRequest.getCustomerId())
                .orElseThrow(() -> new ResourceNotFoundException("Customer", "id", rentalRequest.getCustomerId()));

        Store s = storeRepository.findById(rentalRequest.getStoreId())
                .orElseThrow(() -> new ResourceNotFoundException("Store", "id", rentalRequest.getStoreId()));

       //Verifico con i dati ottenuti se il noleggio è ancora in corso
       List<Rental> onGoingRentals = rentalRepository.findOnGoingRental(f,c,s);

       //Se la query restituisce una copia non restituita vado a settargliela, altrimenti ne aggiungo una nuova
        if(!onGoingRentals.isEmpty())
        {
            Rental onGoing = onGoingRentals.get(0);
            onGoing.setRentalReturn(rentalRequest.getReturnRentalDate());
            return new ResponseEntity<>("Rental of movie \"" + f.getTitle() + "\"" + " updated", HttpStatus.OK);
        }

        //Verifico se ci sono copie disponibili
        List<Inventory> availableCopiesInStore = inventoryRepository.findAvailableCopies(f, s);
        if (availableCopiesInStore.isEmpty())
        {
            return new ResponseEntity<>("No film available in the store", HttpStatus.BAD_REQUEST);
        }

        //se non ci sono noleggi in corso e se ci sono copie disponibili, creo un nuovo noleggio
        if (!availableCopiesInStore.isEmpty())
        {
            Inventory firstCopyAvailable = availableCopiesInStore.get(0);
            Rental rental = new Rental(new RentalId(c, firstCopyAvailable, LocalDateTime.now()), rentalRequest.getReturnRentalDate());
            rental.setRentalReturn(null); //inizialmente la data di restituzione sarà nulla
            rentalRepository.save(rental);
        }
            return new ResponseEntity<>("You've successfully rented \"" + f.getTitle() + "\"", HttpStatus.OK);
    }
}

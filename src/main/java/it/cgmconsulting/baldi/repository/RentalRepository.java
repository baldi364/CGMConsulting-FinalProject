package it.cgmconsulting.baldi.repository;

import it.cgmconsulting.baldi.entity.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public interface RentalRepository extends JpaRepository<Rental, RentalId>
{
    //senza DATE la query funzionava ma mi trovava 0 risultati nella ricerca di un unico giorno
    @Query(value = "SELECT COUNT(*) " +
            "FROM rental r " +
            "JOIN inventory i ON r.inventory_id = i.inventory_id " +
            "JOIN store s ON i.store_id = s.store_id " +
            "WHERE s.store_id = :storeId " +
            "AND DATE(r.rental_date) BETWEEN :start AND :end", nativeQuery = true)
    long countRentalsInDataRange(long storeId, LocalDate start, LocalDate end);

    //questa query mi restituirà le copie con il noleggio in corso, dunque le copie con il return a NULL
    @Query("SELECT r FROM Rental r " +
            "WHERE r.rentalId.inventoryId.filmId = :film " +
            "AND r.rentalId.customerId = :customer " +
            "AND r.rentalId.inventoryId.storeId = :store " +
            "AND r.rentalReturn IS NULL")
    List<Rental> findOnGoingRental(Film film, Customer customer, Store store);
}

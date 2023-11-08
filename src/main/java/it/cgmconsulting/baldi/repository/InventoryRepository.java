package it.cgmconsulting.baldi.repository;

import it.cgmconsulting.baldi.entity.Film;
import it.cgmconsulting.baldi.entity.Inventory;
import it.cgmconsulting.baldi.entity.Store;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.util.List;

public interface InventoryRepository extends JpaRepository<Inventory, Long>
{
    //Questa query mi ritorna le copie disponibili di un determinato film in un determinato negozio
    //Nella prima subquery controllo che l'inventario non sia associato a nessun noleggio
    //Poi eseguo una seconda subquery (SELECT COUNT(r)) che mi servirà a contare i noleggi associati a quell'inventario in cui la rental_return sia null
    //In questo modo escludo le copie non restituite
    //Se risulta 0, significa che non ci sono noleggi in corso e la query mi restituirà le copie effettivamente disponibili.
    @Query("SELECT i FROM Inventory i " +
            "WHERE i.filmId = :film " +
            "AND i.storeId = :store " +
            "AND (i NOT IN (SELECT r.rentalId.inventoryId FROM Rental r) " +
            "OR (SELECT COUNT(r) FROM Rental r " +
            "WHERE r.rentalId.inventoryId = i " +
            "AND r.rentalReturn is null) = 0)")
    List<Inventory> findAvailableCopies(Film film, Store store);

}

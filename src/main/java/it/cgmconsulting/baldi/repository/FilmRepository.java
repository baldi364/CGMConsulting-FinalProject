package it.cgmconsulting.baldi.repository;

import it.cgmconsulting.baldi.entity.Film;
import it.cgmconsulting.baldi.payload.response.FilmMaxRentResponse;
import it.cgmconsulting.baldi.payload.response.FilmRentResponse;
import it.cgmconsulting.baldi.payload.response.FilmRentableResponse;
import it.cgmconsulting.baldi.payload.response.FilmResponse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Set;

public interface FilmRepository extends JpaRepository<Film, Long>
{

    //Con questa query JPQL mi ricavo una lista di FilmResponse in base all'id della lingua passato come parametro
    @Query("SELECT new it.cgmconsulting.baldi.payload.response.FilmResponse(" +
            "film.filmId, " +
            "film.title, " +
            "film.description, " +
            "film.releaseYear, " +
            "film.languageId.languageName) " +
            "FROM Film film " +
            "WHERE film.languageId.languageId = :languageId")
    List<FilmResponse> getFilmByLanguage(long languageId);

    //utilizzo il distinct per evitare doppioni di stessi film noleggiati
    //eseguo le varie join con inventory, store e rental
    //e la condizione in cui soltanto i noleggi associati a un determinato cliente siano inclusi nel risultato.
    @Query(value = "SELECT DISTINCT NEW it.cgmconsulting.baldi.payload.response.FilmRentResponse(" +
            "f.filmId, " +
            "f.title, " +
            "s.storeName) " +
            "FROM Film f " +
            "INNER JOIN Inventory i ON f.filmId = i.filmId.filmId " +
            "INNER JOIN Store s ON i.storeId.storeId = s.storeId " +
            "INNER JOIN Rental r ON i.inventoryId = r.rentalId.inventoryId.inventoryId " +
            "WHERE r.rentalId.customerId.customerId = :customerId")
    List<FilmRentResponse> getAllRentalByCustomer(long customerId);

    @Query("SELECT NEW it.cgmconsulting.baldi.payload.response.FilmMaxRentResponse(" +
            "f.filmId, " +
            "f.title, " +
            "COUNT(r.rentalId.inventoryId) as TotRents) " +
            "FROM Film f " +
            "LEFT JOIN Inventory i ON f.filmId = i.filmId.filmId " +
            "LEFT JOIN Rental r ON i.inventoryId = r.rentalId.inventoryId.inventoryId " +
            "GROUP BY f.filmId, f.title " +
            "ORDER BY TotRents DESC")
    List<FilmMaxRentResponse> getMaxRentals();

    //Creo una query per ricavarmi le copie in possesso del negozio
    @Query("SELECT NEW it.cgmconsulting.baldi.payload.response.FilmRentableResponse(" +
            "f.title, " +
            "s.storeName, " +
            "COUNT(i.inventoryId)) " +
            "FROM Film f " +
            "LEFT JOIN Inventory i ON f.filmId = i.filmId.filmId " +
            "LEFT JOIN Store s ON i.storeId.storeId = s.storeId " +
            "WHERE f.title = :title " +
            "GROUP BY f.title, s.storeName")
    List<FilmRentableResponse> findFilmInStore(String title);

    //Mi creo una seconda query per calcolarmi le copie noleggiate (a cui passo il nome dello store)
    @Query("SELECT NEW it.cgmconsulting.baldi.payload.response.FilmRentableResponse(" +
            "f.title, " +
            "s.storeName, " +
            "COUNT(i.inventoryId)) " +
            "FROM Film f " +
            "JOIN Inventory i ON f.filmId = i.filmId.filmId " +
            "JOIN Store s ON i.storeId.storeId = s.storeId " +
            "JOIN Rental r ON i.inventoryId = r.rentalId.inventoryId.inventoryId " +
            "WHERE f.title = :title " +
            "AND r.rentalReturn IS NULL " +
            "AND s.storeName = :storeName " +
            "GROUP BY f.title, s.storeName")
    List<FilmRentableResponse> getCopiesRented(String title, String storeName);

    Film findByTitle(String title);

    //In questa query mi ricavo innazitutto gli attributi di FilmResponse
    //Eseguo tutte le join per ricavarmi attori, film, ruolo e lingua
    //Controllo che il ruolo dello staff sia actor e che il set di actorsId sia in staff_id
    //Utilizzo un having count distinct per assicurarmi che trovi il film con esattamente gli attori passati
    @Query(value = "SELECT NEW it.cgmconsulting.baldi.payload.response.FilmResponse(" +
            "f.filmId, " +
            "f.title, " +
            "f.description, " +
            "f.releaseYear, " +
            "l.languageName" +
            ") FROM FilmStaff fs  " +
            "INNER JOIN fs.filmStaffId.staffId s " +
            "INNER JOIN fs.filmStaffId.filmId f " +
            "INNER JOIN fs.filmStaffId.roleId r " +
            "INNER JOIN f.languageId l " +
            "WHERE r.roleName = 'actor' " +
            "AND s.staffId IN :actorsId " +
            "GROUP BY f.filmId, f.title, f.description, f.releaseYear, l.languageName " +
            "HAVING COUNT(DISTINCT s.staffId) = :actorCount " +
            "ORDER BY f.title")
    List<FilmResponse> findActorsInFilms(Set<Long> actorsId, long actorCount);
}

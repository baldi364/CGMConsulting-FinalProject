package it.cgmconsulting.baldi.repository;

import it.cgmconsulting.baldi.entity.Store;
import it.cgmconsulting.baldi.payload.response.CustomerStoreResponse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface StoreRepository extends JpaRepository<Store, Long>
{
    Optional<Store> findByStoreName(String storeName);

    @Query("SELECT NEW it.cgmconsulting.baldi.payload.response.CustomerStoreResponse(" +
            "s.storeName, " +
            "COUNT(DISTINCT c.customerId)) " +
            "FROM Rental r " +
            "JOIN r.rentalId.customerId c " +
            "JOIN r.rentalId.inventoryId i " +
            "JOIN i.storeId s " +
            "WHERE s.storeId = :storeId " +
            "GROUP BY s.storeName")
    CustomerStoreResponse countCustomersByStore(long storeId);

}

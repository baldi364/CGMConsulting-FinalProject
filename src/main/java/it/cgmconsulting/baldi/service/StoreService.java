package it.cgmconsulting.baldi.service;

import it.cgmconsulting.baldi.entity.Store;
import it.cgmconsulting.baldi.exception.ResourceNotFoundException;
import it.cgmconsulting.baldi.payload.response.CustomerStoreResponse;
import it.cgmconsulting.baldi.repository.StoreRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class StoreService
{
    public final StoreRepository storeRepository;

    public ResponseEntity<?> getCountCustomers(String storeName)
    {
        //Verifico che lo store com quel nome esista
        Store store = storeRepository.findByStoreName(storeName)
                .orElseThrow(()-> new ResourceNotFoundException("Store", "storeName", storeName));

        //Eseguo la query JPQL
        CustomerStoreResponse customerStoreResponse = storeRepository.countCustomersByStore(store.getStoreId());

        return new ResponseEntity<>(customerStoreResponse, HttpStatus.OK);
    }
}
